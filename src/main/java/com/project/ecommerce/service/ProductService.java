package com.project.ecommerce.service;

import com.project.ecommerce.model.Product;
import com.project.ecommerce.model.Category;
import com.project.ecommerce.model.Brand;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.CategoryRepository;
import com.project.ecommerce.repository.BrandRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
            Pageable pageable
    ) {
        if (category == null) category = "";
        if (search == null) search = "";

        Page<Product> page = repo.searchProducts(category.trim(), brandId, search.trim(), pageable);
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
}
