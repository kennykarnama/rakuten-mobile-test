package com.rakutenmobile.messageapi.usermessage.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MessageRepository extends ReactiveSortingRepository<MessageEntity, java.util.UUID> {
    Mono<MessageEntity> findMessageEntityByIdAndUserId(java.util.UUID id, String userId);
    Flux<MessageEntity> findMessageEntitiesByTopicContainsAndUserId(String topic, String userId, Pageable pageable);
    Flux<MessageEntity> findAllBy(Pageable pageable);
}
