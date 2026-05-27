package com.project.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import com.project.ecommerce.model.OrderItem;

import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private String loadTemplate(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        try (Scanner scanner = new Scanner(resource.getInputStream(), StandardCharsets.UTF_8.name())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    public void sendVerificationEmail(String to, String token) throws MessagingException, IOException {
        String subject = "Verify your email address";
        String template = loadTemplate("templates/verification.html");
        String content = template.replace("${token}", token);
        sendHtmlMessage(to, subject, content);
    }

    public void sendPasswordResetEmail(String to, String token) throws MessagingException, IOException {
        String subject = "Reset your password";
        String template = loadTemplate("templates/reset_password.html");
        String content = template.replace("${token}", token);
        sendHtmlMessage(to, subject, content);
    }

    public void sendOrderConfirmationEmail(String to, String orderId, List<OrderItem> items) throws MessagingException, IOException {
        String subject = "Kaimart - Order Confirmation #" + orderId;
        String template = loadTemplate("templates/order_confirmation.html");
        String content = template.replace("${orderId}", orderId);

        // Build product rows HTML
        StringBuilder rows = new StringBuilder();
        double grandTotal = 0;
        for (OrderItem item : items) {
            double subtotal = item.getPrice() * item.getQuantity();
            grandTotal += subtotal;
            rows.append("<tr>")
                .append("<td>").append(item.getProductName()).append("</td>")
                .append("<td>").append(item.getQuantity()).append("</td>")
                .append("<td class=\"price\">₹").append(String.format("%.2f", subtotal)).append("</td>")
                .append("</tr>");
        }
        rows.append("<tr class=\"total-row\">")
            .append("<td colspan=\"2\" style=\"text-align:right;\">Total</td>")
            .append("<td class=\"price\">₹").append(String.format("%.2f", grandTotal)).append("</td>")
            .append("</tr>");

        content = content.replace("${orderItems}", rows.toString());
        sendHtmlMessage(to, subject, content);
    }

    public void sendRegistrationEmail(String to) throws MessagingException, IOException {
        String subject = "Welcome to Kaimart!";
        String template = loadTemplate("templates/registration.html");
        sendHtmlMessage(to, subject, template);
    }

    public void sendOrderStatusUpdateEmail(String to, String orderId, String newStatus, String notes) throws MessagingException, IOException {
        String subject = "Kaimart - Order #" + orderId + " Status Update: " + newStatus;
        String template = loadTemplate("templates/order_status_update.html");
        String content = template.replace("${orderId}", orderId)
                                 .replace("${newStatus}", newStatus)
                                 .replace("${notes}", notes != null ? notes : "");
        sendHtmlMessage(to, "publicuservj@gmail.com", subject, content);
    }

    private void sendHtmlMessage(String to, String subject, String htmlContent) throws MessagingException {
        sendHtmlMessage(to, null, subject, htmlContent);
    }

    private void sendHtmlMessage(String to, String cc, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        if (cc != null && !cc.isEmpty()) {
            helper.setCc(cc);
        }
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
