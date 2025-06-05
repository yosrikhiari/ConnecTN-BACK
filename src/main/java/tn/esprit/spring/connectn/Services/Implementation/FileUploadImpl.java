package tn.esprit.spring.connectn.Services.Implementation;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.connectn.Services.Interfaces.FileUpload;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadImpl implements FileUpload {
    private final Cloudinary cloudinary;

    @Override
    public Map<String, String> uploadFile(MultipartFile multipartFile) throws IOException {
        // Generate a unique publicId
        String publicId = UUID.randomUUID().toString();

        // Upload the file to Cloudinary
        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                multipartFile.getBytes(),
                Map.of("public_id", publicId)
        );

        // Return both the publicId and the URL
        return Map.of(
                "publicId", publicId,
                "url", uploadResult.get("url").toString()
        );
    }

    @Override
    public void deleteFile(String publicId) throws IOException {
        // Delete the file from Cloudinary using its public ID
        cloudinary.uploader().destroy(publicId, Map.of());
    }
}
