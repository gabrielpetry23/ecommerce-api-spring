package io.github.gabrielpetry23.ecommerceapi.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "email_queue")
@Data
@EntityListeners(AuditingEntityListener.class)
public class EmailQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "recipient_email", nullable = false, length = 255)
    private String recipientEmail;

    @Column(nullable = false, length = 255)
    private String subject;

    @Column(nullable = false)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EmailStatus status = EmailStatus.PENDING;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(name = "last_attempt_at")
    private LocalDateTime lastAttemptAt;

    @Column(name = "error_message")
    private String errorMessage;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}