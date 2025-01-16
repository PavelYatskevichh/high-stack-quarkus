package com.yatskevich.hs.quarkus.content_creation.consumer;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class MessageConsumer {

    @ConfigProperty(name = "kafka.topic")
    private String topic;

    @Incoming("hs-spring")
    public void consume(double price) {
        // process your price.
    }
}
