package com.adama.backoffice.messages.repository;

import com.adama.backoffice.messages.entity.Message;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findByMessageType(Message.MessageType type);

    List<Message> findByReceiverIdAndIsRead(String currentUserId, boolean b);

    List<Message> findAll(Specification<Message> spec);

    List<Message> findByIsRead(boolean isRead);

    List<Message> findByIsReadAndMessageType(boolean isRead, Message.MessageType messageType);
}
