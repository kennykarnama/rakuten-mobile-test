package com.rakutenmobile.messageapi.usermessage.adapter.in.restful;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rakutenmobile.messageapi.usermessage.adapter.out.kafka.UserMessageDto;
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
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
public class MessageController implements MessagesApi, MessageApi {

    private final PublishMessageUseCase publisher;
    private final MessageUseCase messageUseCase;

    private final ReactiveKafkaProducerTemplate<String, String> kafka;

    public MessageController(PublishMessageUseCase publisher, MessageUseCase messageUseCase, ReactiveKafkaProducerTemplate<String, String> kafka) {
        this.publisher = publisher;
        this.messageUseCase = messageUseCase;
        this.kafka = kafka;
    }

    @Override
    public Mono<ResponseEntity<MessagesGet200Response>> messagesGet(Integer page, Integer pageSize, String userId, String topic, ServerWebExchange exchange) {
        return null;
    }

    @Override
    public Mono<Void> messagesPost(Flux<SubmitMessageRequest> submitMessageRequest, ServerWebExchange exchange) {
        Hooks.onOperatorDebug();
        return exchange.getPrincipal().map(v -> v.getName()).
                flatMapMany(usr -> submitMessageRequest.flatMap(req -> {
                    UserMessage userMessage = UserMessage.builder()
                            .content(req.getContent())
                            .topic(req.getTopic())
                            .createdAt(OffsetDateTime.now())
                            .userId(usr)
                            .build();
                    return Flux.just(userMessage);
                })).map(message -> {
                    UserMessageDto dto = new UserMessageDto(message.getId(),
                            message.getContent(), message.getTopic(), message.getCreatedAt(), message.getUserId());
                    System.out.println(message);
                    ObjectMapper mapper = JsonMapper.builder()
                            .addModule(new JavaTimeModule())
                            .build();
                    String json = "";
                    try {
                        json = mapper.writeValueAsString(dto);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    return kafka.send("send-message", json).subscribe();
                }).map(aduh -> {
                    kafka.flush();
                    System.out.println(aduh.toString());
                    return aduh;
                }).then().then();
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
