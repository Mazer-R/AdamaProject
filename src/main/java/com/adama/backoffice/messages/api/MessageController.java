package com.adama.backoffice.messages.api;

import com.adama.backoffice.messages.entity.Message;
import com.adama.backoffice.messages.service.MessageService;
import com.adama.message.model.MessagePatchRequest;
import com.adama.message.model.MessageRequest;
import com.adama.message.model.MessageResponse;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public List<MessageResponse> getAllMessages(
            @RequestParam(required = false) Boolean isRead, @RequestParam(required = false) String messageType) {
        return messageService.getAllMessages(isRead, messageType);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createMessage(@RequestBody MessageRequest request) {
        return messageService.createMessage(request);
    }

    @GetMapping("/unread")
    public List<MessageResponse> getUnreadMessages() {
        return messageService.getUnreadMessagesForCurrentUser();
    }

    @GetMapping("/{id}")
    public MessageResponse getMessageById(@PathVariable String id) {
        return messageService.getMessageById(id);
    }

    @PatchMapping("/{id}")
    public MessageResponse markAsRead(@PathVariable String id, @RequestBody MessagePatchRequest request) {
        return messageService.markAsRead(id, request.getIsRead());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<MessageResponse>> getMessagesByType(@PathVariable String type) {
        try {
            Message.MessageType messageType = Message.MessageType.valueOf(type.toUpperCase());
            return ResponseEntity.ok(messageService.getMessagesByType(messageType));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de mensaje inválido. Valores permitidos: " + Arrays.toString(Message.MessageType.values()));
        }
    }
}
