package com.rakutenmobile.messageapi.usermessage.adapter.in.restful;

import com.rakutenmobile.messageapi.usermessage.domain.UserMessage;
import com.rakutenmobile.messageapi.usermessage.port.in.MessageUseCase;
import com.rakutenmobile.messageapi.usermessage.port.out.PublishMessageUseCase;
import com.rakutenmobile.openapi.models.Message;
import com.rakutenmobile.openapi.models.MessagesGet200Response;
import com.rakutenmobile.openapi.models.SubmitMessageRequest;
import com.rakutenmobile.openapi.spring.reactive.api.MessageApi;
import com.rakutenmobile.openapi.spring.reactive.api.MessagesApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
public class MessageController implements MessagesApi, MessageApi {

    private final PublishMessageUseCase publisher;
    private final MessageUseCase messageUseCase;

    public MessageController(PublishMessageUseCase publisher, MessageUseCase messageUseCase) {
        this.publisher = publisher;
        this.messageUseCase = messageUseCase;
    }

    @Override
    public Mono<ResponseEntity<MessagesGet200Response>> messagesGet(Integer page, Integer pageSize, String userId, String topic, ServerWebExchange exchange) {
        return null;
    }

    @Override
    public Mono<ResponseEntity<Void>> messagesPost(Flux<SubmitMessageRequest> submitMessageRequest, ServerWebExchange exchange) {
        Flux<UserMessage> sources = submitMessageRequest.map(req -> UserMessage.builder()
                .content(req.getContent()).topic(req.getTopic()).createdAt(OffsetDateTime.now()).build());
        return publisher.Publish(sources).doOnError(e -> System.out.println(e.toString()))
                .doOnNext(r -> System.out.println(r.toString()))
                .then(Mono.empty());
    }

    @Override
    public Mono<ResponseEntity<Void>> messageIdDelete(UUID id, ServerWebExchange exchange) {
        return messageUseCase.deleteMessageById(id).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
    }

    @Override
    public Mono<ResponseEntity<Message>> messageIdGet(UUID id, ServerWebExchange exchange) {
        Mono<UserMessage> message = messageUseCase.getMessageById(id);
        return message.map(r -> {
            Message dto = new Message();
            dto.id(r.getId());
            dto.content(r.getContent());
            dto.createdAt(r.getCreatedAt());
            dto.topic(r.getTopic());
            dto.userId(r.getUserId());
            return new ResponseEntity<>(dto, HttpStatus.OK);
        });
    }
}
