package com.rakutenmobile.messageapi.usermessage.configuration;

import com.rakutenmobile.messageapi.MessageApiApplication;
import com.rakutenmobile.messageapi.usermessage.adapter.out.kafka.Publisher;
import com.rakutenmobile.messageapi.usermessage.adapter.out.persistence.MessageRepository;
import com.rakutenmobile.messageapi.usermessage.port.in.MessageUseCase;
import com.rakutenmobile.messageapi.usermessage.port.out.PublishMessageUseCase;
import com.rakutenmobile.messageapi.usermessage.port.service.MessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = MessageApiApplication.class)
public class BeanConfiguration {

    @Bean
    MessageUseCase messageUseCase(final MessageRepository messageRepository) {
        return new MessageService(messageRepository);
    }

    @Bean
    PublishMessageUseCase publishMessageUseCase() {
        return new Publisher();
    }

}
