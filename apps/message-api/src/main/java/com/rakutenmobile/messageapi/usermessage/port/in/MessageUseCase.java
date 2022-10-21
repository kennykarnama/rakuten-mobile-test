package com.rakutenmobile.messageapi.usermessage.port.in;

import com.rakutenmobile.messageapi.usermessage.domain.UserMessage;
import com.rakutenmobile.openapi.models.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface MessageUseCase {
    Mono<UserMessage> submitMessage(UserMessage message);
    Mono<UserMessage> getMessageById(java.util.UUID id);
    void deleteMessageById(java.util.UUID id);
    Mono<Page<UserMessage>> findAll(Pageable pageable);
}
