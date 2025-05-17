package com.adama.backoffice.messages.mapper;

import static java.time.LocalTime.now;

import com.adama.backoffice.messages.entity.Message;
import com.adama.backoffice.users.entity.User;
import com.adama.backoffice.users.repository.UserRepository;
import com.adama.message.model.MessageRequest;
import com.adama.message.model.MessageResponse;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    private final UserRepository userRepository;

    public MessageMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Message toEntity(MessageRequest request) {
        User receiver = userRepository
                .findByUsername(request.getReceiverUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario receptor no encontrado"));
        return Message.builder()
                .receiverId(request.getReceiverUsername())
                .subject(request.getSubject())
                .content(request.getContent())
                .replyTo(request.getReplyTo())
                .isRead(false)
                .isReply(request.getReplyTo() != null)
                .messageType(Message.MessageType.INTERNAL)
                .createdAt(now().toString())
                .build();
    }

    public MessageResponse toResponse(Message message) {
        return MessageResponse.builder()
                .id(UUID.fromString(message.getId()))
                .senderUsername(getUsernameById(message.getSenderId()))
                .receiverUsername(getUsernameById(message.getReceiverId()))
                .subject(message.getSubject())
                .content(message.getContent())
                .isRead(message.isRead())
                .messageType(MessageResponse.MessageTypeEnum.valueOf(
                        message.getMessageType().name()))
                .replyTo(message.isReply() ? message.getReplyTo() : "")
                .createdAt(message.getCreatedAt())
                .build();
    }

    private String getUsernameById(String userId) {
        return userRepository
                .findById(UUID.fromString(userId))
                .map(User::getUsername)
                .orElse("Unknown");
    }
}
