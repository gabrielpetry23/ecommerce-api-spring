package io.github.gabrielpetry23.ecommerceapi.service;

import io.github.gabrielpetry23.ecommerceapi.controller.dto.TrackingResponseDTO;
import io.github.gabrielpetry23.ecommerceapi.exceptions.EmailSendingException;
import io.github.gabrielpetry23.ecommerceapi.model.Order;
import io.github.gabrielpetry23.ecommerceapi.model.OrderStatus;
import io.github.gabrielpetry23.ecommerceapi.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.email.from}")
    private String emailFrom;

    @Async
    public void sendWelcomeEmail(User user) {
        try {
            log.info("Attempting to send welcome email to: {}", user.getEmail());
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("userName", user.getName());
            context.setVariable("siteName", "EcommerceAPI");

            String htmlContent = templateEngine.process("emails/welcome-email", context);

            helper.setTo(user.getEmail());
            helper.setFrom(emailFrom);
            helper.setSubject("Bem-vindo(a) ao EcommerceAPI!");
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("Successfully sent welcome email to: {}", user.getEmail());
        } catch (MailException | MessagingException e) {
            log.error("Failed to send welcome email to {}: {}", user.getEmail(), e.getMessage(), e);
            // Considere adicionar lógica de retry ou notificação de falha aqui.
        }
    }

    @Async
    public void sendOrderConfirmationEmail(User user, Order order) {
        try {
            log.info("Attempting to send order confirmation email for order #{} to: {}", order.getId(), user.getEmail());
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("userName", user.getName());
            context.setVariable("orderId", order.getId().toString().substring(0, 8));
            context.setVariable("orderDate", order.getCreatedAt());
            context.setVariable("totalAmount", order.getTotal());
            context.setVariable("orderItems", order.getItems());

            String htmlContent = templateEngine.process("emails/order-confirmation-email", context);

            helper.setTo(user.getEmail());
            helper.setFrom(emailFrom);
            helper.setSubject("Seu pedido #" + order.getId().toString().substring(0, 8) + " foi realizado com sucesso!");
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("Successfully sent order confirmation email for order #{} to: {}", order.getId(), user.getEmail());
        } catch (MailException | MessagingException e) {
            log.error("Failed to send order confirmation email for order #{} to {}: {}", order.getId(), user.getEmail(), e.getMessage(), e);
        }
    }

    @Async
    public void sendOrderStatusUpdateEmail(User user, Order order, String newStatus, TrackingResponseDTO trackingDetailsDTO) {
        try {
            log.info("Attempting to send order status update email for order #{} (status: {}) to: {}", order.getId(), newStatus, user.getEmail());
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("userName", user.getName());
            context.setVariable("orderId", order.getId().toString().substring(0, 8));
            context.setVariable("newStatus", newStatus);
            context.setVariable("trackingDetails", trackingDetailsDTO);
            context.setVariable("totalAmount", order.getTotal());
            context.setVariable("orderItems", order.getItems());

            String templateName = getTemplateNameForOrderStatus(OrderStatus.valueOf(newStatus));
            String subject = getSubjectForOrderStatus(OrderStatus.valueOf(newStatus), order);

            String htmlContent = templateEngine.process(templateName, context);

            helper.setTo(user.getEmail());
            helper.setFrom(emailFrom);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            log.info("Successfully sent order status update email for order #{} (status: {}) to: {}", order.getId(), newStatus, user.getEmail());
        } catch (MailException | MessagingException e) {
            log.error("Failed to send order status update email for order #{} (status: {}) to {}: {}", order.getId(), newStatus, user.getEmail(), e.getMessage(), e);
            throw new EmailSendingException("Failed to send order status update email for order #" + order.getId() + " (status: " + newStatus + ") to " + user.getEmail(), e);
        }
    }

    private String getTemplateNameForOrderStatus(OrderStatus status) {
        return switch (status) {
            case PAID -> "emails/order-status-paid-email";
            case DELIVERED -> "emails/order-status-delivered-email";
            case CANCELLED -> "emails/order-status-cancelled-email";
            case IN_DELIVERY -> "emails/order-status-in-delivery-email";
            default -> null;
        };
    }

    private String getSubjectForOrderStatus(OrderStatus status, Order order) {
        String orderIdShort = order.getId().toString().substring(0, 8);
        return switch (status) {
            case PAID -> "Confirmed! Your Order #" + orderIdShort + " Has Been Paid!";
            case DELIVERED -> "Good News! Your Order #" + orderIdShort + " Has Been Delivered!";
            case CANCELLED -> "Important: Your Order #" + orderIdShort + " Has Been Cancelled.";
            default -> "Update for Your Order #" + orderIdShort;
        };
    }
}