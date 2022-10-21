package com.rakutenmobile.messageapi.usermessage.adapter.in.restful;

import com.rakutenmobile.messageapi.usermessage.adapter.out.kafka.Publisher;
import com.rakutenmobile.messageapi.usermessage.domain.UserMessage;
import com.rakutenmobile.messageapi.usermessage.port.out.PublishMessageUseCase;
import com.rakutenmobile.openapi.models.MessagesGet200Response;
import com.rakutenmobile.openapi.models.SubmitMessageRequest;
import com.rakutenmobile.openapi.spring.reactive.api.MessagesApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@RestController
public class CustomerController implements MessagesApi {

    private final PublishMessageUseCase publisher;

    public CustomerController(PublishMessageUseCase publisher) {
        this.publisher = publisher;
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
}
