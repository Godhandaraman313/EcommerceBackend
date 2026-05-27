package com.project.ecommerce.controller;

import com.project.ecommerce.model.Brand;
import com.project.ecommerce.model.Category;
import com.project.ecommerce.repository.BrandRepository;
import com.project.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/brands")
@CrossOrigin("http://localhost:5173")
public class BrandController {

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final com.project.ecommerce.service.CloudinaryImageService cloudinaryImageService;
    private final Path uploadRoot;

    public BrandController(
            BrandRepository brandRepository,
            CategoryRepository categoryRepository,
            com.project.ecommerce.service.CloudinaryImageService cloudinaryImageService,
            @Value("${app.upload.dir:uploads}") String uploadDir
    ) throws IOException {
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.cloudinaryImageService = cloudinaryImageService;
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadRoot.resolve("brands"));
    }

    // List all brands without pagination (useful for dropdown menus)
    @GetMapping("/all")
    public List<Brand> listAll() {
        return brandRepository.findAll();
    }

    // Paginated list with optional search and sort
    @GetMapping
    public Page<Brand> listBrands(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        String[] sortParams = sort.split(",");
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0])
        );

        if (search == null || search.trim().isEmpty()) {
            return brandRepository.findAll(pageable);
        } else {
            return brandRepository.findAll(search.trim(), pageable);
        }
    }

    // Get brand by ID
    @GetMapping("/{id}")
    public Brand getBrandById(@PathVariable Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found with ID: " + id));
    }

    // Create Brand (Admin only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Brand createBrand(@RequestBody Brand brand) {
        if (brand.getName() == null || brand.getName().trim().isEmpty()) {
            throw new RuntimeException("Brand name is required");
        }

        // Map Category IDs to Category entities
        if (brand.getCategoryIds() != null && !brand.getCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(brand.getCategoryIds()));
            brand.setCategories(categories);
        }

        if (brand.getLogo() == null || brand.getLogo().isBlank()) {
            brand.setLogo("default-logo.png");
        }

        return brandRepository.save(brand);
    }

    // Update Brand (Admin only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Brand updateBrand(@PathVariable Long id, @RequestBody Brand brandDetails) {
        Brand existing = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        if (brandDetails.getName() == null || brandDetails.getName().trim().isEmpty()) {
            throw new RuntimeException("Brand name is required");
        }

        existing.setName(brandDetails.getName().trim());

        if (brandDetails.getCategoryIds() != null) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(brandDetails.getCategoryIds()));
            existing.setCategories(categories);
        }

        if (brandDetails.getLogo() != null && !brandDetails.getLogo().isBlank()) {
            existing.setLogo(brandDetails.getLogo());
        }

        return brandRepository.save(existing);
    }

    // Delete Brand (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteBrand(@PathVariable Long id) {
        if (!brandRepository.existsById(id)) {
            throw new RuntimeException("Brand not found");
        }
        brandRepository.deleteById(id);
        return "Brand deleted successfully";
    }

    // Upload Brand Logo (Admin only)
    @PostMapping(value = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Brand uploadLogo(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Logo image file is required");
        }

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        String contentType = file.getContentType();
        String ext = ".jpg";
        if (contentType != null) {
            ext = switch (contentType) {
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                case "image/gif" -> ".gif";
                default -> ".jpg";
            };
        }

        String filename = UUID.randomUUID() + ext;
        
        // Prefer Cloudinary; fallback to local filesystem if Cloudinary not configured
        String publicPath = cloudinaryImageService.upload(file, "brand-logos/" + id);
        if (publicPath == null) {
            Path brandDir = uploadRoot.resolve("brands").resolve(String.valueOf(id));
            Files.createDirectories(brandDir);
            Path target = brandDir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            publicPath = "/uploads/brands/" + id + "/" + filename;
        }

        brand.setLogo(publicPath);
        return brandRepository.save(brand);
    }
}
