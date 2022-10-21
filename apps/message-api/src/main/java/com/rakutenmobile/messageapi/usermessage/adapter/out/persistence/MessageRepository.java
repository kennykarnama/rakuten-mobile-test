package com.rakutenmobile.messageapi.usermessage.adapter.out.persistence;

import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface MessageRepository extends ReactiveSortingRepository<MessageEntity, java.util.UUID> {
}
