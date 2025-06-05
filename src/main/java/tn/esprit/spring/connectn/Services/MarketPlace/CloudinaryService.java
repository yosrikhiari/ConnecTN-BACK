package tn.esprit.spring.connectn.Services.MarketPlace;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Upload file to Cloudinary
     *
     * @param file The file to upload
     * @param folder Optional folder path
     * @return URL of the uploaded file
     * @throws IOException If upload fails
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        logger.debug("Uploading file to Cloudinary: {} ({}KB)",
                file.getOriginalFilename(), file.getSize() / 1024);

        Map<String, Object> params = new HashMap<>();
        params.put("resource_type", "auto");

        if (folder != null && !folder.isEmpty()) {
            params.put("folder", folder);
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            String url = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            logger.info("File uploaded successfully to Cloudinary. Public ID: {}", publicId);
            return url;
        } catch (IOException e) {
            logger.error("Failed to upload file to Cloudinary", e);
            throw e;
        }
    }

    /**
     * Delete file from Cloudinary
     *
     * @param publicId The public ID of the file to delete
     * @return Result of the deletion operation
     * @throws IOException If deletion fails
     */
    public Map deleteFile(String publicId) throws IOException {
        logger.debug("Deleting file from Cloudinary: {}", publicId);

        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            logger.info("File deleted successfully from Cloudinary: {}", publicId);
            return result;
        } catch (IOException e) {
            logger.error("Failed to delete file from Cloudinary: {}", publicId, e);
            throw e;
        }
    }

    /**
     * Get public ID from URL
     *
     * @param url The Cloudinary URL
     * @return The public ID
     */
    public String getPublicIdFromUrl(String url) {
        // Example URL: https://res.cloudinary.com/demo/image/upload/v1234567890/folder/filename.jpg
        if (url == null || !url.contains("/upload/")) {
            return null;
        }

        String[] parts = url.split("/upload/");
        if (parts.length < 2) {
            return null;
        }

        // Extract everything after the version number (v1234567890/)
        String path = parts[1];
        if (path.contains("/v")) {
            String[] versionParts = path.split("/v\\d+/");
            if (versionParts.length > 1) {
                path = versionParts[1];
            }
        }

        // Remove file extension
        int extensionIndex = path.lastIndexOf(".");
        if (extensionIndex > 0) {
            path = path.substring(0, extensionIndex);
        }

        return path;
    }
}