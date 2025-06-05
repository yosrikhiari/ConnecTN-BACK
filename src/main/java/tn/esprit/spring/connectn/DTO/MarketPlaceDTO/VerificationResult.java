package tn.esprit.spring.connectn.DTO.MarketPlaceDTO;


import tn.esprit.spring.connectn.Entities.DocumentVerification;

public class VerificationResult {
    private final boolean success;
    private final DocumentVerification document;
    private final String errorMessage;

    public VerificationResult(boolean success, DocumentVerification document, String errorMessage) {
        this.success = success;
        this.document = document;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() { return success; }
    public DocumentVerification getDocument() { return document; }
    public String getErrorMessage() { return errorMessage; }
}