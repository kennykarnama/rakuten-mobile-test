package com.rakutenmobile.messageapi.usermessage.adapter.out.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rakutenmobile.messageapi.usermessage.domain.UserMessage;
import com.rakutenmobile.messageapi.usermessage.port.out.PublishMessageUseCase;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Publisher implements PublishMessageUseCase {
    private static final Logger log = LoggerFactory.getLogger(Publisher.class.getName());

    private static final String TOPIC = "send-message";
    private static final String BOOTSTRAP_SERVERS = "localhost:29092";
    private static final String CLIENT_ID_CONFIG = "kafka-message-publisher";

    private final KafkaSender<String, String> sender;
    private final SimpleDateFormat dateFormat;

    public Publisher() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID_CONFIG);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        SenderOptions<String, String> senderOptions = SenderOptions.create(props);
        sender = KafkaSender.create(senderOptions);

        dateFormat = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");
    }

    @Override
    public Flux<SenderResult<String>> Publish(Flux<UserMessage> messages) {
        UUID fakeUserId = UUID.randomUUID();
        return sender.<String>send(messages
                .map(message -> {
                    UUID t = UUID.randomUUID();
                    log.debug("uuid " + t);
                    UserMessageDto dto = new UserMessageDto(message.getId(), message.getContent(), message.getTopic(), message.getCreatedAt(), message.getUserId());
                    // todo: removed once authentication has been implemented
                    dto.setUserId(fakeUserId.toString());
                    ObjectMapper mapper = JsonMapper.builder()
                            .addModule(new JavaTimeModule())
                            .build();
                    String json = "";
                    try {
                        json = mapper.writeValueAsString(dto);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    return SenderRecord.create(new ProducerRecord<>(TOPIC,
                            t.toString(), json), t.toString());
                }));
    }
}
