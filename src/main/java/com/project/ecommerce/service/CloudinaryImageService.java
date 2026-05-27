package com.project.ecommerce.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryImageService {

    private final Cloudinary cloudinary;
    private final boolean enabled;

    public CloudinaryImageService(
            Cloudinary cloudinary,
            @Value("${cloudinary.cloud-name:}") String cloudName,
            @Value("${cloudinary.api-key:}") String apiKey,
            @Value("${cloudinary.api-secret:}") String apiSecret
    ) {
        this.cloudinary = cloudinary;
        // Cloudinary is enabled only when all three credentials are provided
        this.enabled = cloudName != null && !cloudName.isBlank()
                && apiKey != null && !apiKey.isBlank()
                && apiSecret != null && !apiSecret.isBlank();
    }

    /**
     * Uploads the given {@code file} to Cloudinary under the provided {@code folder}.
     * Returns the secure URL of the uploaded asset, or {@code null} if Cloudinary is not enabled.
     */
    @SuppressWarnings("unchecked")
    public String upload(MultipartFile file, String folder) {
        if (!enabled) {
            // Cloudinary not configured – caller will fall back to local storage.
            return null;
        }
        try {
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "image"
            );
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            return null;
        }
    }
}
