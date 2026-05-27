package com.project.ecommerce.service;

import com.project.ecommerce.model.Product;
import com.project.ecommerce.model.ProductDetail;
import com.project.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import com.project.ecommerce.service.CloudinaryImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductImageService {

    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private final ProductRepository productRepository;
    private final Path uploadRoot;
    private final CloudinaryImageService cloudinaryImageService;

    @Value("${app.product.default-image-url:/images/sample-product.jpg}")
    private String defaultImageUrl;

    public ProductImageService(
            ProductRepository productRepository,
            CloudinaryImageService cloudinaryImageService,
            @Value("${app.upload.dir:uploads}") String uploadDir
    ) throws IOException {
        this.productRepository = productRepository;
        this.cloudinaryImageService = cloudinaryImageService;
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadRoot.resolve("products"));
    }

    public String getDefaultImageUrl() {
        return defaultImageUrl;
    }

    public void applyDefaultImageIfMissing(Product product) {
        if (product.getImageUrl() == null || product.getImageUrl().isEmpty()) {
            product.setImageUrl(defaultImageUrl);
        }
    }

    public Product uploadImage(Long productId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Image file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED.contains(contentType)) {
            throw new RuntimeException("Only JPEG, PNG, WEBP, or GIF images are allowed");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String ext = resolveExtension(contentType);
        String filename = UUID.randomUUID() + ext;

        // Prefer Cloudinary; fallback to local filesystem if Cloudinary not configured
        String publicPath = cloudinaryImageService.upload(file, "product-images/" + productId);
        if (publicPath == null) { // Cloudinary disabled, use local fallback
            Path productDir = uploadRoot.resolve("products").resolve(String.valueOf(productId));
            Files.createDirectories(productDir);
            Path target = productDir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            publicPath = "/uploads/products/" + productId + "/" + filename;
        }

        product.setImageUrl(publicPath);
        return productRepository.save(product);
    }

    public Product uploadExtraImages(Long productId, MultipartFile[] files) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String contentType = file.getContentType();
                if (contentType != null && ALLOWED.contains(contentType)) {
                    String ext = resolveExtension(contentType);
                    String filename = UUID.randomUUID() + ext;
                    String publicPath;

                    // Use Cloudinary for extra images; fallback to local if needed
                    String extraPath = cloudinaryImageService.upload(file, "product-images/" + productId + "/extras");
                    if (extraPath == null) {
                        Path extrasDir = uploadRoot.resolve("products").resolve(String.valueOf(productId)).resolve("extras");
                        Files.createDirectories(extrasDir);
                        Path target = extrasDir.resolve(filename);
                        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                        extraPath = "/uploads/products/" + productId + "/extras/" + filename;
                    }
                    product.addExtraImage(extraPath);
                }
            }
        }
        return productRepository.save(product);
    }

    /** Delete the main product thumbnail image. */
    public Product deleteThumbnail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        // Reset to default image URL (could be a placeholder image)
        product.setImageUrl(getDefaultImageUrl());
        return productRepository.save(product);
    }

    /** Delete a specific extra image from the product gallery. */
    public Product deleteExtraImage(Long productId, String imagePath) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        // Remove from entity list
        product.removeExtraImage(imagePath);
        // Attempt to delete the physical file if stored locally (fallback case)
        if (imagePath != null && imagePath.startsWith("/uploads/")) {
            try {
                Path file = Paths.get("." + imagePath);
                Files.deleteIfExists(file);
            } catch (Exception e) {
                // Log but do not fail the operation
                System.err.println("Failed to delete image file: " + e.getMessage());
            }
        }
        return productRepository.save(product);
    }


    private String resolveExtension(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
    }
}
