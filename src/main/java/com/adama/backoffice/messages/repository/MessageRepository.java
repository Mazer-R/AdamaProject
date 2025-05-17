package com.adama.backoffice.messages.repository;

import com.adama.backoffice.messages.entity.Message;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findByMessageType(Message.MessageType type);

    List<Message> findByReceiverIdAndIsRead(String receiverId, boolean isRead);

    List<Message> findBySenderId(String senderId);

    default List<Message> findInbox(String receiverId) {
        return findByReceiverIdAndIsRead(receiverId, false);
    }
}
