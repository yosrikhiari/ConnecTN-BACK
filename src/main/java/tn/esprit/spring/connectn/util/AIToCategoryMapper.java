package tn.esprit.spring.connectn.util;

import java.util.*;

public enum AIToCategoryMapper {
    ;

    // Define keywords for each category
    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new HashMap<>();

    // Primary keywords (strong indicators)
    private static final Map<String, List<String>> PRIMARY_KEYWORDS = new HashMap<>();

    static {
        // Infrastructure related keywords
        CATEGORY_KEYWORDS.put("INFRASTRUCTURE", Arrays.asList(
                "road", "street", "highway", "pothole", "bridge", "sidewalk",
                "construction", "building", "pavement", "crosswalk", "streetlight", "asphalt",
                "light", "pole", "sign", "concrete", "path", "lane", "infrastructure",
                "pipe", "sewer", "utility", "electric", "power", "grid", "fiber", "broadband",
                "drainage", "water supply", "plumbing", "internet", "telecom"
        ));

        // Transport related keywords
        CATEGORY_KEYWORDS.put("TRANSPORT", Arrays.asList(
                "traffic", "vehicle", "car", "bus", "train", "transportation",
                "railway", "intersection", "truck", "parking", "public transport",
                "metro", "subway", "tram", "bicycle", "bike", "scooter", "motorcycle",
                "commute", "passenger", "station", "terminal", "airport", "taxi", "ride",
                "transit", "travel", "fleet", "ferry", "port", "transport", "mobility"
        ));

        // Healthcare related keywords
        CATEGORY_KEYWORDS.put("HEALTHCARE", Arrays.asList(
                "hospital", "ambulance", "mask", "doctor", "nurse", "patient", "clinic",
                "medical", "health", "pharmacy", "medicine", "emergency", "wheelchair",
                "bandage", "injury", "treatment", "vaccine", "healthcare", "care", "drug",
                "pill", "syringe", "sick", "disease", "sanitizer", "hygiene", "first-aid",
                "therapy", "mental health", "dental", "specialist", "diagnosis", "preventive"
        ));

        // Education related keywords
        CATEGORY_KEYWORDS.put("EDUCATION", Arrays.asList(
                "school", "university", "college", "book", "library", "classroom", "student",
                "teacher", "blackboard", "whiteboard", "lecture", "education", "pencil",
                "notebook", "desk", "campus", "study", "learning", "diploma", "graduation",
                "textbook", "academic", "lesson", "knowledge", "class", "paper", "test",
                "training", "curriculum", "tutor", "course", "education", "kindergarten"
        ));

        // Environmental related keywords
        CATEGORY_KEYWORDS.put("ENVIRONMENT", Arrays.asList(
                "trash", "garbage", "pollution", "waste", "recycle", "green", "tree", "plant",
                "forest", "park", "nature", "litter", "disposal", "bin", "dump", "plastic",
                "contamination", "environmental", "ecology", "water", "river", "lake", "ocean",
                "flower", "grass", "garden", "air", "energy", "climate", "solar", "wildlife",
                "sustainable", "conservation", "biodiversity", "emissions", "eco"
        ));

        // Primary keywords (more weight)
        PRIMARY_KEYWORDS.put("INFRASTRUCTURE", Arrays.asList(
                "road", "pothole", "highway", "bridge", "construction", "street", "infrastructure",
                "pipe", "sewer", "utility", "broadband", "drainage"
        ));

        PRIMARY_KEYWORDS.put("TRANSPORT", Arrays.asList(
                "traffic", "bus", "train", "transportation", "railway", "vehicle", "transport",
                "commute", "transit", "mobility", "public transport"
        ));

        PRIMARY_KEYWORDS.put("HEALTHCARE", Arrays.asList(
                "hospital", "doctor", "ambulance", "medical", "emergency", "healthcare", "patient",
                "clinic", "pharmacy", "treatment"
        ));

        PRIMARY_KEYWORDS.put("EDUCATION", Arrays.asList(
                "school", "university", "classroom", "education", "student", "teacher", "college",
                "campus", "curriculum", "learning"
        ));

        PRIMARY_KEYWORDS.put("ENVIRONMENT", Arrays.asList(
                "trash", "garbage", "pollution", "waste", "recycle", "contamination", "litter",
                "environmental", "sustainable", "conservation"
        ));
    }

    public static String mapLabelToCategory(List<String> labels, Map<String, Integer> frequencies) {
        // Score each category based on matching keywords and their frequencies
        Map<String, Double> categoryScores = new HashMap<>();

        for (Map.Entry<String, List<String>> category : CATEGORY_KEYWORDS.entrySet()) {
            double score = 0;

            for (String label : labels) {
                String lowerLabel = label.toLowerCase();
                // Check if any category keyword is contained in the label
                for (String keyword : category.getValue()) {
                    if (lowerLabel.contains(keyword.toLowerCase())) {
                        // Use frequency as weight if available
                        int frequency = frequencies.getOrDefault(label, 1);
                        double keywordScore = frequency;

                        // Give extra weight to primary keywords
                        if (PRIMARY_KEYWORDS.containsKey(category.getKey()) &&
                                PRIMARY_KEYWORDS.get(category.getKey()).stream()
                                        .anyMatch(k -> lowerLabel.contains(k.toLowerCase()))) {
                            keywordScore *= 2.5;  // Primary keywords get 2.5x weight
                        }

                        score += keywordScore;
                        break; // Count match only once per label
                    }
                }
            }

            categoryScores.put(category.getKey(), score);
        }

        // Debug print scores
        System.out.println("Category scores: " + categoryScores);

        // Find category with highest score
        String bestCategory = "OTHER";
        double highestScore = 0;
        double threshold = 1.0; // Minimum score needed to assign a category

        for (Map.Entry<String, Double> entry : categoryScores.entrySet()) {
            if (entry.getValue() > highestScore) {
                highestScore = entry.getValue();
                bestCategory = entry.getKey();
            }
        }

        // Return OTHER if no significant matches found or score below threshold
        return highestScore >= threshold ? bestCategory : "OTHER";
    }

    // Helper method to check if a label matches keywords for a category
    public static boolean matchesCategory(String label, String category) {
        if (!CATEGORY_KEYWORDS.containsKey(category)) {
            return false;
        }

        label = label.toLowerCase();
        for (String keyword : CATEGORY_KEYWORDS.get(category)) {
            if (label.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // Helper method to get keywords for a category
    public static List<String> getKeywordsForCategory(String category) {
        return CATEGORY_KEYWORDS.getOrDefault(category, new ArrayList<>());
    }
}
