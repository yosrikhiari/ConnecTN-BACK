package tn.esprit.spring.connectn.Services.Implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.*;

@Service
public class HuggingFaceService {

    @Value("${huggingface.token}")
    private String hfToken;

    private static final String[] MODELS = {
            "facebook/detr-resnet-50",                   // Good general object detection
            "google/vit-base-patch16-224",               // Image classification model
            "Salesforce/blip-image-captioning-large"     // Caption-based detection
    };

    // Model task types for proper API configuration
    private static final Map<String, String> MODEL_TASKS = new HashMap<>();
    static {
        MODEL_TASKS.put("facebook/detr-resnet-50", "object-detection");
        MODEL_TASKS.put("google/vit-base-patch16-224", "image-classification");
        MODEL_TASKS.put("Salesforce/blip-image-captioning-large", "image-to-text");
    }

    // ExecutorService for parallel API calls
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public Map<String, Object> classifyImage(byte[] imageBytes) {
        // Results containers
        List<String> allLabels = Collections.synchronizedList(new ArrayList<>());
        Map<String, Integer> labelFrequency = new ConcurrentHashMap<>();

        // List for futures
        List<Future<?>> futures = new ArrayList<>();

        // Call all models in parallel
        for (String model : MODELS) {
            Future<?> future = executor.submit(() -> {
                try {
                    List<String> labels = callModel(model, imageBytes);
                    if (labels != null && !labels.isEmpty()) {
                        allLabels.addAll(labels);
                        // Update frequency count
                        for (String label : labels) {
                            labelFrequency.put(label, labelFrequency.getOrDefault(label, 0) + 1);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error with model " + model + ": " + e.getMessage());
                }
            });
            futures.add(future);
        }

        // Wait for all API calls to complete (with timeout)
        for (Future<?> future : futures) {
            try {
                future.get(15, TimeUnit.SECONDS);
            } catch (Exception e) {
                future.cancel(true);
                System.err.println("Model call timed out or failed: " + e.getMessage());
            }
        }

        // Fallback if no labels were detected
        if (allLabels.isEmpty()) {
            System.out.println("No labels detected by any model. Using fallback detection.");
            allLabels.add("image");
            labelFrequency.put("image", 1);
        }

        // Return both raw labels and their frequencies
        Map<String, Object> result = new HashMap<>();
        result.put("labels", allLabels);
        result.put("frequencies", labelFrequency);

        return result;
    }

    private List<String> callModel(String modelUrl, byte[] imageBytes) {
        String apiUrl = "https://api-inference.huggingface.co/models/" + modelUrl;
        String task = MODEL_TASKS.getOrDefault(modelUrl, "object-detection");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setBearerAuth(hfToken);

        // Add specific task header if needed
        headers.add("X-Use-Cache", "false");
        headers.add("X-Wait-For-Model", "true");

        HttpEntity<byte[]> request = new HttpEntity<>(imageBytes, headers);
        RestTemplate restTemplate = new RestTemplate();

        List<String> labels = new ArrayList<>();

        int maxRetries = 2;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    ObjectMapper objectMapper = new ObjectMapper();

                    // Process based on model task type
                    if ("image-to-text".equals(task)) {
                        // For caption models (returns [{"generated_text": "a cat sitting on a chair"}])
                        List<Map<String, Object>> results = objectMapper.readValue(response.getBody(), List.class);
                        for (Map<String, Object> result : results) {
                            String caption = (String) result.get("generated_text");
                            if (caption != null) {
                                // Extract key objects from the caption
                                String[] words = caption.toLowerCase().split("\\s+");
                                for (String word : words) {
                                    // Filter out common stop words and short words
                                    if (word.length() > 3 && !isCommonWord(word)) {
                                        labels.add(word);
                                    }
                                }
                            }
                        }
                    } else if ("image-classification".equals(task)) {
                        // For image classification models
                        List<Map<String, Object>> results = objectMapper.readValue(response.getBody(), List.class);
                        for (Map<String, Object> result : results) {
                            String label = (String) result.get("label");
                            if (label != null) {
                                labels.add(label.toLowerCase());
                            }
                        }
                    } else {
                        // For object detection models
                        List<Map<String, Object>> results = objectMapper.readValue(response.getBody(), List.class);
                        for (Map<String, Object> result : results) {
                            String label = (String) result.get("label");
                            if (label != null) {
                                labels.add(label.toLowerCase());
                            }
                        }
                    }
                    return labels;
                }

                // Handle model loading
                if (response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                    System.out.println("Model " + modelUrl + " not ready. Retrying in 2 seconds...");
                    Thread.sleep(2000);
                    continue;
                }
            } catch (Exception e) {
                System.err.println("Attempt " + attempt + " failed for model " + modelUrl + ": " + e.getMessage());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }

        return labels;
    }

    // Helper method to filter out common words from captions
    private boolean isCommonWord(String word) {
        Set<String> commonWords = Set.of("the", "and", "with", "this", "that", "there", "here", "from", "into", "your", "some");
        return commonWords.contains(word);
    }
}
