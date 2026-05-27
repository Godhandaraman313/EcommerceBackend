package com.project.ecommerce.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "brands")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 45, unique = true)
    private String name;

    @Column(nullable = false, length = 128)
    private String logo;

    @ManyToMany
    @JoinTable(
        name = "brand_categories",
        joinColumns = @JoinColumn(name = "brand_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    public Brand() {
    }

    public Brand(String name) {
        this.name = name;
        this.logo = "default-logo.png";
    }

    public Brand(Long id, String name) {
        this.id = id;
        this.name = name;
        this.logo = "default-logo.png";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }

    @Transient
    private java.util.List<Long> categoryIds;

    public java.util.List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(java.util.List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public Set<Category> getCategories() { return categories; }
    public void setCategories(Set<Category> categories) { this.categories = categories; }
}
