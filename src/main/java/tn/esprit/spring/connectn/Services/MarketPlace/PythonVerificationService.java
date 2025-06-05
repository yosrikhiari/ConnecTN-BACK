// PythonVerificationService.java
package tn.esprit.spring.connectn.Services.MarketPlace;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.connectn.DTO.MarketPlaceDTO.PythonVerificationResponse;

import java.io.IOException;

@Service
public class PythonVerificationService {

    private final RestTemplate restTemplate;
    private final String pythonServiceUrl = "http://localhost:5000/api/v1/detect"; // Update with your Python service URL

    public PythonVerificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PythonVerificationResponse verifyDocument(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
        body.add("confidence", "0.5"); // Default confidence threshold

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<PythonVerificationResponse> response = restTemplate.exchange(
                pythonServiceUrl,
                HttpMethod.POST,
                requestEntity,
                PythonVerificationResponse.class
        );

        return response.getBody();
    }

    public PythonVerificationResponse verifyDocument(byte[] fileData, String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(fileData) {
            @Override
            public String getFilename() {
                return fileName;
            }
        });
        body.add("confidence", "0.5");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(
                pythonServiceUrl,
                requestEntity,
                PythonVerificationResponse.class
        );
    }
}