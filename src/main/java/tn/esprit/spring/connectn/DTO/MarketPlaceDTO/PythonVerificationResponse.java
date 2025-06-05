package tn.esprit.spring.connectn.DTO.MarketPlaceDTO;
// PythonVerificationResponse.java

import lombok.Data;
import java.util.List;

@Data
public class PythonVerificationResponse {
    private List<Detection> detections;
    private String annotated_image;

    @Data
    public static class Detection {
        private String class_name;
        private double confidence;
        private List<Integer> coordinates;
    }
}