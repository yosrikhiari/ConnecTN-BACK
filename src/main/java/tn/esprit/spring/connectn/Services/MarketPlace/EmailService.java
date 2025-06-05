package tn.esprit.spring.connectn.Services.MarketPlace;
import org.springframework.beans.factory.annotation.Value;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "ConnectN - Green Points");  // Add a friendly name
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            // Add headers to improve deliverability
            message.addHeader("List-Unsubscribe", "<mailto:" + fromEmail + "?subject=Unsubscribe>");
            message.addHeader("Precedence", "bulk");
            message.addHeader("X-Auto-Response-Suppress", "OOF, AutoReply");

            System.out.println("Attempting to send email to: " + to);
            mailSender.send(message);
            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send email", e);
        } catch (Exception e) {
            System.err.println("Unexpected error sending email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Unexpected error sending email", e);
        }
    }
}