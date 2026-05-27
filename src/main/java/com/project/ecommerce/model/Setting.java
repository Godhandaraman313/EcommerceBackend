package com.project.ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "settings")
public class Setting {

    @Id
    @Column(name = "`key`", nullable = false, unique = true)
    private String key;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String type; // e.g., STRING, BOOLEAN, INTEGER

    @Column(name = "category")
    private String category;

    // Getters and Setters
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
