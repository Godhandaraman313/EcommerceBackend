package com.project.ecommerce.controller;

import com.project.ecommerce.dto.CategoryResponse;
import com.project.ecommerce.model.Category;
import com.project.ecommerce.repository.CategoryRepository;
import com.project.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin("http://localhost:5173")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Value("${app.product.default-image-url:/images/sample-product.jpg}")
    private String defaultCategoryImageUrl;

    public CategoryController(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<CategoryResponse> listCategories() {
        // Map of category name to product count
        Map<String, Long> countMap = productRepository.findCategorySummaries().stream()
                .filter(row -> row[0] != null)
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1],
                        (a, b) -> a
                ));

        return categoryRepository.findAll().stream()
                .map(cat -> new CategoryResponse(
                        cat.getId(),
                        cat.getName(),
                        cat.getAlias(),
                        countMap.getOrDefault(cat.getName(), 0L),
                        defaultCategoryImageUrl
                ))
                .toList();
    }
}
