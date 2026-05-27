package com.project.ecommerce.config;

import com.project.ecommerce.model.Category;
import com.project.ecommerce.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Order(1) // Run first before other initializers (like ProductDescriptionUpdater)
public class DatabaseMigrationInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final CategoryRepository categoryRepository;

    public DatabaseMigrationInitializer(JdbcTemplate jdbcTemplate, CategoryRepository categoryRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("====== Checking Database Category Migration ======");
        
        // 1. Ensure categories table is populated from existing product category strings
        try {
            // Check if product table has category column
            List<Map<String, Object>> products = jdbcTemplate.queryForList("SELECT id, category, category_id FROM product");
            
            for (Map<String, Object> row : products) {
                Long productId = ((Number) row.get("id")).longValue();
                String categoryStr = (String) row.get("category");
                Object categoryIdObj = row.get("category_id");
                
                if (categoryIdObj == null && categoryStr != null && !categoryStr.trim().isEmpty()) {
                    String cleanName = categoryStr.trim();
                    // Find or create category
                    Category category = categoryRepository.findByName(cleanName)
                        .orElseGet(() -> {
                            Category newCat = new Category(cleanName);
                            return categoryRepository.save(newCat);
                        });
                    
                    // Update product table with category_id
                    jdbcTemplate.update("UPDATE product SET category_id = ? WHERE id = ?", category.getId(), productId);
                    System.out.println("Migrated product ID " + productId + " category '" + cleanName + "' to category_id " + category.getId());
                }
            }
            System.out.println("====== Category Migration Completed Successfully ======");

            // 2. Ensure countries table is populated
            System.out.println("====== Checking Countries Migration ======");
            Integer countryCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM countries", Integer.class);
            if (countryCount == null || countryCount == 0) {
                String[] defaultCountries = {
                        "India", "United States", "United Kingdom", "Canada", "Australia",
                        "Germany", "France", "Japan", "Singapore", "New Zealand",
                        "United Arab Emirates", "Saudi Arabia", "South Africa"
                };
                for (String country : defaultCountries) {
                    jdbcTemplate.update("INSERT INTO countries (name) VALUES (?)", country);
                }
                System.out.println("Inserted default countries.");
            }
            // 3. Restore old uploaded images
            System.out.println("====== Restoring Old Product Images ======");
            java.io.File productsDir = new java.io.File("uploads/products");
            if (productsDir.exists() && productsDir.isDirectory()) {
                java.io.File[] productFolders = productsDir.listFiles();
                if (productFolders != null) {
                    for (java.io.File folder : productFolders) {
                        if (folder.isDirectory()) {
                            try {
                                Long productId = Long.parseLong(folder.getName());
                                java.io.File[] images = folder.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg"));
                                if (images != null && images.length > 0) {
                                    String imageUrl = "/uploads/products/" + productId + "/" + images[0].getName();
                                    jdbcTemplate.update("UPDATE product SET image_url = ? WHERE id = ?", imageUrl, productId);
                                }
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
                System.out.println("====== Product Images Restored ======");
            }

        } catch (Exception e) {
            System.out.println("No migration needed or error during migration: " + e.getMessage());
        }
    }
}
