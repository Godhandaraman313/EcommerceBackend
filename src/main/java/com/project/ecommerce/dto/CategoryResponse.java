package com.project.ecommerce.dto;

public class CategoryResponse {

    private Long id;
    private String name;
    private String code;
    private long productCount;
    private String imageUrl;

    public CategoryResponse(Long id, String name, String code, long productCount, String imageUrl) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.productCount = productCount;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public long getProductCount() {
        return productCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
