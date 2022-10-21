package com.rakutenmobile.messageapi.usermessage.adapter.in.kafka;

import com.rakutenmobile.messageapi.usermessage.adapter.out.kafka.Publisher;
import com.rakutenmobile.messageapi.usermessage.port.out.ConsumeMessageUseCase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class Consumer implements ConsumeMessageUseCase, CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Publisher.class.getName());

    private static final String TOPIC = "send-message";
    private static final String BOOTSTRAP_SERVERS = "localhost:29092";

    private final ReceiverOptions<String, String> receiverOptions;
    private final DateTimeFormatter dateFormat;

    public Consumer() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "sample-consumer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "sample-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        receiverOptions = ReceiverOptions.create(props);
        dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss:SSS z dd MMM yyyy");
    }
    @Override
    public Flux<ReceiverRecord<String, String>> consume() {
        ReceiverOptions<String, String> options = receiverOptions.subscription(Collections.singleton(TOPIC))
                .addAssignListener(partitions -> log.debug("onPartitionsAssigned {}", partitions))
                .addRevokeListener(partitions -> log.debug("onPartitionsRevoked {}", partitions));
        return KafkaReceiver.create(options).receive();
        
    }

    @Override
    public void run(String... args) throws Exception {
        consume().subscribe(record -> {
            ReceiverOffset offset = record.receiverOffset();
            Instant timestamp = Instant.ofEpochMilli(record.timestamp());
            System.out.println(record.toString());
            offset.acknowledge();
        });
    }
}
