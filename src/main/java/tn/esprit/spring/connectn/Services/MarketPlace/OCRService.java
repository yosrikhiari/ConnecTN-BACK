package tn.esprit.spring.connectn.Services.MarketPlace;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class OCRService {

    private final Tesseract tesseract;

    public OCRService() {
        this.tesseract = new Tesseract();
        // Set the path to your tessdata directory
        tesseract.setDatapath("C:\\Users\\moham\\Desktop\\connectn-b+f\\pidev\\src\\main\\resources\\tessdata");
        tesseract.setLanguage("eng"); // Set language
    }

    public String extractText(byte[] imageBytes) throws TesseractException, IOException {
        // Convert byte array to BufferedImage
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(bis);

        if (image == null) {
            throw new IOException("Unsupported image format");
        }

        return tesseract.doOCR(image);
    }
}