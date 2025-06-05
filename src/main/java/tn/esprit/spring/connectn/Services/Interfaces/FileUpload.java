package tn.esprit.spring.connectn.Services.Interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface FileUpload {
    Map<String, String> uploadFile(MultipartFile multipartFile) throws IOException;
    void deleteFile(String publicId) throws IOException;
}