package com.rakutenmobile.messageapi.usermessage.adapter.out.persistence;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;

public interface MessageRepository extends ReactiveSortingRepository<MessageEntity, java.util.UUID> {
    Mono<MessageEntity> findMessageEntityByIdAndUserId(java.util.UUID id, String userId);
}
