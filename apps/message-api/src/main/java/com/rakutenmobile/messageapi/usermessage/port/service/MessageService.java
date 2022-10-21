package com.rakutenmobile.messageapi.usermessage.port.service;

import com.rakutenmobile.messageapi.usermessage.adapter.out.persistence.MessageRepository;
import com.rakutenmobile.messageapi.usermessage.port.in.MessageUseCase;
import com.rakutenmobile.openapi.models.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public class MessageService implements MessageUseCase {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Flux<Message> submitMessages(List<Message> messages) {
        return null;
    }

    @Override
    public Mono<Message> getMessageById(UUID id) {
        return null;
    }

    @Override
    public void deleteMessageById(UUID id) {

    }

    @Override
    public Mono<Page<Message>> findAll(Pageable pageable) {
        return null;
    }
}
