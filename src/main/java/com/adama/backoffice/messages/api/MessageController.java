package com.adama.backoffice.messages.api;

import com.adama.backoffice.exception.MessageNotFoundException;
import com.adama.backoffice.messages.entity.Message;
import com.adama.backoffice.messages.service.MessageService;
import com.adama.message.api.MessageApi;
import com.adama.message.model.MessagePatchRequest;
import com.adama.message.model.MessageRequest;
import com.adama.message.model.MessageResponse;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController implements MessageApi {

    private final MessageService messageService;

    @Override
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<MessageResponse>> getAllMessages(
            @RequestParam(required = false) Boolean isRead, @RequestParam(required = false) String messageType) {

        try {
            List<MessageResponse> responses = messageService.getAllMessages(messageType);
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de mensaje inválido. Valores permitidos: " + Arrays.toString(Message.MessageType.values()));
        }
    }

    @Override
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> createMessage(@Valid @RequestBody MessageRequest messageRequest) {
        try {
            MessageResponse response = messageService.createMessage(messageRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar el mensaje");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado: " + e.getMessage());
        }
    }

    @Override
    @GetMapping("/my-messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageResponse>> getReceivedMessages(@RequestParam(required = false) Boolean isRead) {
        return ResponseEntity.ok(messageService.getReceivedMessages(true));
    }

    @Override
    @GetMapping("/my-messages/sent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageResponse>> getSentMessages() {
        return ResponseEntity.ok(messageService.getSentMessages());
    }

    @Override
    @GetMapping("/my-messages/inbox")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageResponse>> getInboxMessages() {
        return ResponseEntity.ok(messageService.getUnreadInbox());
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> getMessageById(@PathVariable("id") String id) {

        try {
            MessageResponse response = messageService.getMessageById(id);
            return ResponseEntity.ok(response);
        } catch (MessageNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al recuperar el mensaje");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado: " + e.getMessage());
        }
    }

    @Override
    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> markAsRead(
            @PathVariable("id") String id, @Valid @RequestBody MessagePatchRequest messagePatchRequest) {
        try {
            MessageResponse response = messageService.markAsRead(id, messagePatchRequest.getIsRead());
            return ResponseEntity.ok(response);
        } catch (MessageNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar el mensaje");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado: " + e.getMessage());
        }
    }

    @Override
    @GetMapping("/type/{type}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MessageResponse>> getMessagesByType(@PathVariable("type") String type) {
        try {
            Message.MessageType messageType = Message.MessageType.valueOf(type.toUpperCase());
            List<MessageResponse> responses = messageService.getMessagesByType(messageType);
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de mensaje inválido. Valores permitidos: " + Arrays.toString(Message.MessageType.values()));
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al recuperar los mensajes");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado: " + e.getMessage());
        }
    }
}
