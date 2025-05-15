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
        return Message.builder()
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .subject(request.getSubject())
                .content(request.getContent())
                .replyTo(request.getReplyTo())
                .messageType(Message.MessageType.INTERNAL)
                .build();
    }

    public MessageResponse toResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .senderUsername(getUsernameById(message.getSenderId()))
                .receiverUsername(getUsernameById(message.getReceiverId()))
                .subject(message.getSubject())
                .content(message.getContent())
                .isRead(message.isRead())
                .messageType(MessageResponse.MessageTypeEnum.valueOf(
                        message.getMessageType().name()))
                .replyTo(message.isReply() ? message.getReplyTo() : "")
                .createdAt(now().toString())
                .build();
    }

    private String getUsernameById(String userId) {
        return userRepository
                .findById(UUID.fromString(userId))
                .map(User::getUsername)
                .orElse("Unknown");
    }
}
