package com.rakutenmobile.messageapi.usermessage.port.service;

import com.rakutenmobile.messageapi.usermessage.adapter.out.persistence.MessageEntity;
import com.rakutenmobile.messageapi.usermessage.adapter.out.persistence.MessageRepository;
import com.rakutenmobile.messageapi.usermessage.domain.exception.MessageNotFoundException;
import com.rakutenmobile.messageapi.usermessage.port.in.MessageUseCase;
import com.rakutenmobile.messageapi.usermessage.domain.UserMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class MessageService implements MessageUseCase {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Mono<UserMessage> submitMessage(UserMessage message) {
        MessageEntity entity = MessageEntity.builder().content(message.getContent())
                .topic(message.getTopic()).createdAt(message.getCreatedAt()).userId(message.getUserId()).build();
        Mono<MessageEntity> result = messageRepository.save(entity);
        return result.map(r -> UserMessage.builder().id(r.getId())
                .content(r.getContent())
                .topic(r.getTopic())
                .createdAt(r.getCreatedAt())
                .userId(r.getUserId()).build());
    }

    @Override
    public Mono<UserMessage> getMessageById(UUID id) {
        return messageRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new MessageNotFoundException("Message not found"))))
                .map(r -> UserMessage.builder().id(r.getId())
                        .content(r.getContent())
                        .topic(r.getTopic())
                        .createdAt(r.getCreatedAt())
                        .userId(r.getUserId()).build());
    }

    @Override
    public void deleteMessageById(UUID id) {
        return;
    }

    @Override
    public Mono<Page<UserMessage>> findAll(Pageable pageable) {
        return null;
    }
}
