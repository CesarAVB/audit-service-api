package br.com.sistema.audit.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${audit.queue.name:audit_events_queue}")
    private String auditQueueName;

    @Bean
    public Queue auditEventsQueue() {
        // Declara a fila. 'true' significa que a fila é durável (sobrevive a reinícios do RabbitMQ).
        return new Queue(auditQueueName, true);
    }
}