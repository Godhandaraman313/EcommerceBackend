package com.project.ecommerce.config;

import com.project.ecommerce.model.Product;
import com.project.ecommerce.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class ProductDescriptionUpdater implements CommandLineRunner {

    private final ProductRepository productRepository;

    public ProductDescriptionUpdater(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Product> products = productRepository.findAll();
        boolean updated = false;
        
        String[] adjectives = {"premium", "high-quality", "durable", "innovative", "stylish", "versatile", "top-rated"};
        String[] features = {"ergonomic design", "eco-friendly materials", "industry-leading warranty", "compact size", "seamless integration"};
        Random rand = new Random();

        for (Product p : products) {
            if (p.getDescription() == null || p.getDescription().trim().isEmpty()) {
                String adj = adjectives[rand.nextInt(adjectives.length)];
                String feat = features[rand.nextInt(features.length)];
                
                String generatedDesc = String.format(
                    "Experience the best with this %s %s. Featuring a %s, it is perfectly suited for your everyday needs. Highly recommended by top experts and designed for maximum customer satisfaction.",
                    adj, p.getName(), feat
                );
                
                p.setDescription(generatedDesc);
                productRepository.save(p);
                updated = true;
            }
        }
        
        if (updated) {
            System.out.println("====== Product descriptions have been dynamically updated! ======");
        }
    }
}
