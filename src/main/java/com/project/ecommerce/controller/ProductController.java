package com.project.ecommerce.controller;

import com.project.ecommerce.model.Product;
import com.project.ecommerce.service.ProductImageService;
import com.project.ecommerce.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Map;

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
            @RequestParam(defaultValue = "") String hashtag,
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

        return service.getFilteredProducts(category, brandId, search, hashtag, pageable);
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

    // CSV Export (admin only) - all matching product data except images
    @GetMapping(value = "/export", produces = "text/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportProductsCsv(
            @RequestParam(defaultValue = "") String category,
            @RequestParam(required = false) Long brandId,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String hashtag
    ) {
        String csv = service.exportProductsCsv(category, brandId, search, hashtag);
        byte[] body = csv.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products-export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(body);
    }

    // CSV Template (admin only) - plain column names
    @GetMapping(value = "/import-template", produces = "text/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadImportTemplate() {
        String csv = service.getImportTemplateCsv();
        byte[] body = csv.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products-template.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(body);
    }

    // CSV Import (admin only)
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> importProductsCsv(@RequestParam("file") MultipartFile file) {
        return service.importProductsCsv(file);
    }

    // Per-product image upload (categories keep the default sample image).
        @PostMapping(
            value = "/{id}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
        )
        @PreAuthorize("hasRole('ADMIN')")
        public Product uploadThumbnail(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
        ) throws Exception {
        return imageService.uploadImage(id, file);
        }

        /** Per-product extra images upload (gallery). */
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