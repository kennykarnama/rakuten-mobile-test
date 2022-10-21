package com.rakutenmobile.messageapi.usermessage.port.in;

import com.rakutenmobile.openapi.models.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MessageUseCase {
    Flux<Message> submitMessages(List<Message> messages);
    Mono<Message> getMessageById(java.util.UUID id);
    void deleteMessageById(java.util.UUID id);
    Mono<Page<Message>> findAll(Pageable pageable);
}
