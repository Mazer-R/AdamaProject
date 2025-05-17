package com.adama.backoffice.messages.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false)
    private String receiverId;

    @Column(nullable = false)
    private String subject;

    private String content;

    @Builder.Default
    private boolean isRead = false;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    private boolean isReply;
    private String replyTo;

    @CreationTimestamp
    private String createdAt;

    public enum MessageType {
        INTERNAL,
        SYSTEM,
        NOTIFICATION
    }
}
