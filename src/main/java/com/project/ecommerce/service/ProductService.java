package com.project.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import com.project.ecommerce.model.Product;
import com.project.ecommerce.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public List<Product> saveAllProducts(List<Product> products) {
        return repo.saveAll(products);
    }
    public void deleteProduct(Long id) {
        repo.deleteById(id);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = repo.findById(id).orElseThrow();
        existing.setName(updatedProduct.getName());
        return repo.save(existing);
    }
}