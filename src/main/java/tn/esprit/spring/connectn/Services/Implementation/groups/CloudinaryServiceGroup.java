package tn.esprit.spring.connectn.Services.Implementation.groups;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceGroup {

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryServiceGroup(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "use_filename", true,
                            "unique_filename", true,
                            "resource_type", "auto"
                    )
            );
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new IOException("Échec de l'upload vers Cloudinary: " + e.getMessage());
        }
    }

    public void deleteImage(String imageUrl) throws IOException {
        try {
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (IOException e) {
            throw new IOException("Échec de la suppression d'image: " + e.getMessage());
        }
    }

    public String extractPublicIdFromUrl(String url) {
        if (url == null || !url.contains("cloudinary.com")) return null;

        try {
            String[] parts = url.split("/upload/");
            if (parts.length < 2) return null;

            String path = parts[1].split("\\.")[0]; // Enlève l'extension
            if (path.contains("v")) {
                path = path.substring(path.indexOf("/") + 1);
            }
            return path;
        } catch (Exception e) {
            return null;
        }
    }

}