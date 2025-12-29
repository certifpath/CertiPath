package com.audit.comments.config;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class NatsConfig {

    @Value("${nats.servers:nats://localhost:4222}")
    private String natsServer;

    @Value("${nats.maxReconnects:60}")
    private int maxReconnects;

    @Value("${nats.reconnectWait:2}")
    private int reconnectWaitSeconds;

    @Bean
    public Connection natsConnection() throws IOException, InterruptedException {
        Options options = new Options.Builder()
            .server(natsServer)
            .maxReconnects(maxReconnects)
            .reconnectWait(java.time.Duration.ofSeconds(reconnectWaitSeconds))
            .connectionListener((conn, type) -> {
                System.out.println("NATS connection event: " + type);
            })
            .build();
        
        return Nats.connect(options);
    }
}
