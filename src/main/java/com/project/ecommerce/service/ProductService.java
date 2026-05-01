package com.project.ecommerce.service;

import com.project.ecommerce.model.Product;
import com.project.ecommerce.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    // 🔥 FILTER + SEARCH + PAGINATION
    public Page<Product> getFilteredProducts(
            String category,
            String search,
            Pageable pageable
    ) {
        if (category == null) category = "";
        if (search == null) search = "";

        return repo.findByCategoryIgnoreCaseContainingAndNameIgnoreCaseContaining(
                category,
                search,
                pageable
        );
    }

    // GET BY ID
    public Product getProductById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }

    // CREATE
    public Product addProduct(Product product) {

        if (product.getId() != null && repo.existsById(product.getId())) {
            throw new RuntimeException("Product Already Exists!!");
        }

        if (product.getName() == null || product.getName().isEmpty()) {
            throw new RuntimeException("Product name is required");
        }

        if (product.getCategory() == null || product.getCategory().isEmpty()) {
            throw new RuntimeException("Category is required");
        }

        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new RuntimeException("Price must be greater than 0");
        }

        boolean exists = repo.existsByNameIgnoreCaseAndCategoryIgnoreCaseAndPrice(
                product.getName(),
                product.getCategory(),
                product.getPrice()
        );

        if (exists) {
            throw new RuntimeException("Product already exists in ProductList");
        }

        product.setId(null);

        return repo.save(product);
    }

    // UPDATE
    public Product updateProduct(Long id, Product product) {

        Product existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setName(product.getName());
        existing.setCategory(product.getCategory());
        existing.setPrice(product.getPrice());

        return repo.save(existing);
    }

    // DELETE
    public void deleteProduct(Long id) {

        if (!repo.existsById(id)) {
            throw new RuntimeException("Product not found");
        }

        repo.deleteById(id);
    }
}