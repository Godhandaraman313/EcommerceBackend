package com.project.ecommerce.controller;

import com.project.ecommerce.model.Product;
import com.project.ecommerce.service.ProductImageService;
import com.project.ecommerce.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("http://localhost:5173")
public class ProductController {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductImageService imageService;

    // 🔥 FULL FILTER + SEARCH + SORT + PAGINATION
    @GetMapping
    public Page<Product> getProducts(
            @RequestParam(defaultValue = "") String category,
            @RequestParam(required = false) Long brandId,
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

        return service.getFilteredProducts(category, brandId, search, pageable);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return service.getProductById(id);
    }

    // CREATE (admin only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product add(@RequestBody Product product) {
        return service.addProduct(product);
    }

    // UPDATE (admin only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        return service.updateProduct(id, product);
    }

    // DELETE (admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        service.deleteProduct(id);
        return "Product deleted successfully";
    }

    /** Per-product image upload (categories keep the default sample image). */
    @PostMapping(
            value = "/{id}/extra-images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public Product uploadExtraImages(
            @PathVariable Long id,
            @RequestParam("files") MultipartFile[] files
    ) throws Exception {

        return imageService.uploadExtraImages(id, files);
    }

    /** Extra product images upload for gallery */
    // DELETE thumbnail image (admin only)
    @DeleteMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public Product deleteThumbnail(@PathVariable Long id) {
        return imageService.deleteThumbnail(id);
    }

    // DELETE an extra image from product gallery (admin only)
    @DeleteMapping("/{id}/extra-images")
    @PreAuthorize("hasRole('ADMIN')")
    public Product deleteExtraImage(@PathVariable Long id, @RequestParam String imagePath) {
        return imageService.deleteExtraImage(id, imagePath);
    }
}