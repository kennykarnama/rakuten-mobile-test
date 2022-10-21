package com.rakutenmobile.messageapi.usermessage.port.out;

import com.rakutenmobile.messageapi.usermessage.domain.UserMessage;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderResult;

public interface PublishMessageUseCase {
    Flux<SenderResult<String>> Publish(Flux<UserMessage> messages);
}
