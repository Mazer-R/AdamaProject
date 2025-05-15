package com.adama.backoffice.messages.service;

import com.adama.backoffice.exception.MessageNotFoundException;
import com.adama.backoffice.messages.entity.Message;
import com.adama.backoffice.messages.mapper.MessageMapper;
import com.adama.backoffice.messages.repository.MessageRepository;
import com.adama.backoffice.users.repository.UserRepository;
import com.adama.message.model.MessageRequest;
import com.adama.message.model.MessageResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    public List<MessageResponse> getAllMessages(Boolean isRead, String messageType) {
        List<Message> messages;

        if (isRead != null && messageType != null) {
            Message.MessageType type = Message.MessageType.valueOf(messageType.toUpperCase());
            messages = messageRepository.findByIsReadAndMessageType(isRead, type);
        } else if (isRead != null) {
            messages = messageRepository.findByIsRead(isRead);
        } else if (messageType != null) {
            Message.MessageType type = Message.MessageType.valueOf(messageType.toUpperCase());
            messages = messageRepository.findByMessageType(type);
        } else {
            messages = messageRepository.findAll();
        }

        return messages.stream().map(messageMapper::toResponse).toList();
    }

    public MessageResponse createMessage(MessageRequest request) {
        Message message = messageMapper.toEntity(request);
        message.setMessageType(Message.MessageType.INTERNAL);

        String currentUserId = getCurrentUserId();
        if (!currentUserId.equals(request.getSenderId())) {
            throw new SecurityException("Sender ID must match authenticated user");
        }

        return messageMapper.toResponse(messageRepository.save(message));
    }

    public List<MessageResponse> getUnreadMessagesForCurrentUser() {
        String currentUserId = getCurrentUserId();
        return messageRepository.findByReceiverIdAndIsRead(currentUserId, false).stream()
                .map(messageMapper::toResponse)
                .toList();
    }

    public MessageResponse getMessageById(String id) {
        return messageRepository
                .findById(id)
                .map(messageMapper::toResponse)
                .orElseThrow(() -> new MessageNotFoundException(id));
    }

    // 5. Marcar como leído
    public MessageResponse markAsRead(String id, boolean isRead) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new MessageNotFoundException(id));

        String currentUserId = getCurrentUserId();
        if (!currentUserId.equals(message.getReceiverId())) {
            throw new SecurityException("Only message receiver can mark as read");
        }

        message.setRead(isRead);
        return messageMapper.toResponse(messageRepository.save(message));
    }

    public List<MessageResponse> getMessagesByType(Message.MessageType type) {
        return messageRepository.findByMessageType(type).stream()
                .map(messageMapper::toResponse)
                .toList();
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository
                .findByUsername(username)
                .map(user -> user.getId().toString())
                .orElseThrow(() -> new SecurityException("User not found"));
    }
}
