package com.project.ecommerce.service;

import com.project.ecommerce.model.Product;
import com.project.ecommerce.model.Category;
import com.project.ecommerce.model.Brand;
import com.project.ecommerce.model.ProductDetail;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.CategoryRepository;
import com.project.ecommerce.repository.BrandRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    @Autowired
    private ProductImageService imageService;
    // Added import for Objects


    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    public Page<Product> getFilteredProducts(
            String category,
            Long brandId,
            String search,
            String hashtag,
            Pageable pageable
    ) {
        if (category == null) category = "";
        if (search == null) search = "";
        if (hashtag == null) hashtag = "";

        Page<Product> page = repo.searchProducts(category.trim(), brandId, search.trim(), hashtag.trim(), pageable);
        page.forEach(imageService::applyDefaultImageIfMissing);
        return page;
    }

    public Product getProductById(Long id) {
        Product product = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        imageService.applyDefaultImageIfMissing(product);
        return product;
    }

    public Product addProduct(Product product) {
        if (product.getId() != null && repo.existsById(product.getId())) {
            throw new RuntimeException("Product Already Exists!!");
        }

        if (product.getName() == null || product.getName().isEmpty()) {
            throw new RuntimeException("Product name is required");
        }

        String categoryName = product.getCategory();
        if (categoryName == null || categoryName.isEmpty()) {
            throw new RuntimeException("Category is required");
        }

        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new RuntimeException("Price must be greater than 0");
        }

        // Map Category name to entity
        Category category = categoryRepository.findByName(categoryName.trim())
                .orElseGet(() -> categoryRepository.save(new Category(categoryName.trim())));
        product.setCategory(category);

        // Map Brand ID to entity if provided
        if (product.getBrandId() != null) {
            Brand brand = brandRepository.findById(Objects.requireNonNull(product.getBrandId())).orElse(null);
            product.setBrand(brand);
        }

        boolean exists = repo.existsByNameIgnoreCaseAndCategoryNameIgnoreCaseAndPrice(
                product.getName(),
                categoryName,
                product.getPrice()
        );

        if (exists) {
            throw new RuntimeException("Product already exists in ProductList");
        }

        product.setId(null);
        applyDefaults(product);

        // Ensure details have the parent product reference
        if (product.getDetails() != null) {
            product.getDetails().forEach(d -> d.setProduct(product));
        }

        return repo.save(product);
    }

    public Product updateProduct(Long id, Product product) {
        Product existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setName(product.getName());
        existing.setPrice(product.getPrice());

        String categoryName = product.getCategory();
        if (categoryName != null && !categoryName.isEmpty()) {
            Category category = categoryRepository.findByName(categoryName.trim())
                    .orElseGet(() -> categoryRepository.save(new Category(categoryName.trim())));
            existing.setCategory(category);
        }

        if (product.getBrandId() != null) {
            Brand brand = brandRepository.findById(Objects.requireNonNull(product.getBrandId())).orElse(null);
            existing.setBrand(brand);
        } else if (product.getBrand() != null) {
            existing.setBrand(product.getBrand());
        }

        if (product.getDescription() != null) {
            existing.setDescription(product.getDescription());
        }
        existing.setInStock(product.isInStock());
        existing.setHashtags(product.getHashtags());

        if (product.getImageUrl() != null && !product.getImageUrl().isBlank()) {
            existing.setImageUrl(product.getImageUrl());
        }

        // Sync product details (specifications)
        if (product.getDetails() != null) {
            existing.getDetails().clear();
            for (com.project.ecommerce.model.ProductDetail detail : product.getDetails()) {
                existing.addDetail(detail.getName(), detail.getValue());
            }
        }

        imageService.applyDefaultImageIfMissing(existing);
        return repo.save(existing);
    }

    public void deleteProduct(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        repo.deleteById(id);
    }

    public String getImportTemplateCsv() {
        return "id,name,category,brand,price,description,inStock,hashtags,details\n";
    }

    public String exportProductsCsv(String category, Long brandId, String search, String hashtag) {
        Page<Product> page = getFilteredProducts(
                category,
                brandId,
                search,
                hashtag,
                PageRequest.of(0, Integer.MAX_VALUE)
        );

        StringBuilder csv = new StringBuilder();
        csv.append("id,name,category,brand,price,description,inStock,hashtags,details\n");

        for (Product product : page.getContent()) {
            String details = "";
            if (product.getDetails() != null && !product.getDetails().isEmpty()) {
                List<String> pairs = new ArrayList<>();
                for (ProductDetail detail : product.getDetails()) {
                    String detailName = detail.getName() == null ? "" : detail.getName().trim();
                    String detailValue = detail.getValue() == null ? "" : detail.getValue().trim();
                    if (!detailName.isBlank() || !detailValue.isBlank()) {
                        pairs.add(detailName + ":" + detailValue);
                    }
                }
                details = String.join("|", pairs);
            }

            csv.append(product.getId() == null ? "" : product.getId()).append(',')
                    .append(escapeCsv(product.getName())).append(',')
                    .append(escapeCsv(product.getCategory())).append(',')
                    .append(escapeCsv(product.getBrand() != null ? product.getBrand().getName() : "")).append(',')
                    .append(product.getPrice() == null ? "" : product.getPrice()).append(',')
                    .append(escapeCsv(product.getDescription())).append(',')
                    .append(product.isInStock()).append(',')
                    .append(escapeCsv(product.getHashtags())).append(',')
                    .append(escapeCsv(details))
                    .append('\n');
        }

        return csv.toString();
    }

    public Map<String, Object> importProductsCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("CSV file is required");
        }

        String content;
        try {
            content = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Unable to read CSV file");
        }

        if (content.startsWith("\uFEFF")) {
            content = content.substring(1);
        }

        String[] lines = content.split("\\r?\\n");
        if (lines.length == 0 || lines[0].isBlank()) {
            throw new RuntimeException("CSV header is missing");
        }

        List<String> headers = parseCsvLine(lines[0]);
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            idx.put(headers.get(i).trim().toLowerCase(), i);
        }

        if (!idx.containsKey("name") || !idx.containsKey("category") || !idx.containsKey("price")) {
            throw new RuntimeException("CSV must include columns: name, category, price");
        }

        int created = 0;
        int updated = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();

        for (int row = 1; row < lines.length; row++) {
            String raw = lines[row];
            if (raw == null || raw.trim().isBlank()) {
                continue;
            }

            try {
                List<String> values = parseCsvLine(raw);

                String idRaw = getCsvValue(values, idx, "id");
                String name = getCsvValue(values, idx, "name");
                String categoryName = getCsvValue(values, idx, "category");
                String brandName = getCsvValue(values, idx, "brand");
                String priceRaw = getCsvValue(values, idx, "price");
                String description = getCsvValue(values, idx, "description");
                String inStockRaw = getCsvValue(values, idx, "instock");
                String hashtags = getCsvValue(values, idx, "hashtags");
                String detailsRaw = getCsvValue(values, idx, "details");

                if (name == null || name.isBlank()) {
                    throw new RuntimeException("name is required");
                }
                if (categoryName == null || categoryName.isBlank()) {
                    throw new RuntimeException("category is required");
                }
                if (priceRaw == null || priceRaw.isBlank()) {
                    throw new RuntimeException("price is required");
                }

                Double price;
                try {
                    price = Double.valueOf(priceRaw.trim());
                } catch (NumberFormatException ex) {
                    throw new RuntimeException("invalid price: " + priceRaw);
                }

                Product target;
                boolean isUpdate = false;

                if (idRaw != null && !idRaw.isBlank()) {
                    Long id;
                    try {
                        id = Long.valueOf(idRaw.trim());
                    } catch (NumberFormatException ex) {
                        throw new RuntimeException("invalid id: " + idRaw);
                    }
                    Product existing = repo.findById(id).orElse(null);
                    if (existing != null) {
                        target = existing;
                        isUpdate = true;
                    } else {
                        target = new Product();
                    }
                } else {
                    target = new Product();
                }

                target.setName(name.trim());
                target.setPrice(price);

                Category category = categoryRepository.findByName(categoryName.trim())
                        .orElseGet(() -> categoryRepository.save(new Category(categoryName.trim())));
                target.setCategory(category);

                if (brandName != null && !brandName.isBlank()) {
                    Brand brand = brandRepository.findByNameIgnoreCase(brandName.trim())
                            .orElseGet(() -> brandRepository.save(new Brand(brandName.trim())));
                    target.setBrand(brand);
                } else {
                    target.setBrand(null);
                }

                target.setDescription(description == null ? "" : description);
                target.setInStock(inStockRaw == null || inStockRaw.isBlank() || Boolean.parseBoolean(inStockRaw.trim()));
                target.setHashtags((hashtags == null || hashtags.isBlank()) ? null : hashtags.trim());

                target.getDetails().clear();
                if (detailsRaw != null && !detailsRaw.isBlank()) {
                    String[] detailPairs = detailsRaw.split("\\|");
                    for (String pair : detailPairs) {
                        if (pair == null || pair.isBlank()) continue;
                        String[] nv = pair.split(":", 2);
                        String detailName = nv.length > 0 ? nv[0].trim() : "";
                        String detailValue = nv.length > 1 ? nv[1].trim() : "";
                        if (!detailName.isBlank() || !detailValue.isBlank()) {
                            target.addDetail(detailName, detailValue);
                        }
                    }
                }

                applyDefaults(target);
                repo.save(target);

                if (isUpdate) {
                    updated++;
                } else {
                    created++;
                }
            } catch (Exception ex) {
                skipped++;
                errors.add("Row " + (row + 1) + ": " + ex.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("created", created);
        result.put("updated", updated);
        result.put("skipped", skipped);
        result.put("errors", errors);
        return result;
    }

    private void applyDefaults(Product product) {
        if (product.getDescription() == null) {
            product.setDescription("");
        }
        imageService.applyDefaultImageIfMissing(product);
        if (product.getAverageRating() == null) {
            product.setAverageRating(0.0);
        }
        if (product.getReviewCount() == null) {
            product.setReviewCount(0);
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\"\"");
        return '"' + escaped + '"';
    }

    private String getCsvValue(List<String> values, Map<String, Integer> index, String header) {
        Integer pos = index.get(header.toLowerCase());
        if (pos == null || pos < 0 || pos >= values.size()) {
            return "";
        }
        return values.get(pos);
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        values.add(current.toString());
        return values;
    }
}
