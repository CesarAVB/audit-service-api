package br.com.sistema.audit.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.sistema.audit.model.AuditEvent;
import br.com.sistema.audit.model.AuditEventMessage;
import br.com.sistema.audit.repositories.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor // Gera construtor para injeção de dependências finais
@Slf4j // Adiciona um logger via Lombok
public class AuditEventConsumer {

    private final AuditEventRepository auditEventRepository;
    private final ObjectMapper objectMapper; // Injetado para desserialização

    // Construtor customizado para configurar o ObjectMapper
    public AuditEventConsumer(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Para desserializar LocalDateTime
    }

    @Value("${audit.queue.name:audit_events_queue}")
    private String auditQueueName;

    @RabbitListener(queues = "${audit.queue.name:audit_events_queue}")
    public void receiveAuditEvent(String jsonEvent) {
        try {
            // Desserializa a mensagem JSON para o objeto AuditEventMessage
            AuditEventMessage eventMessage = objectMapper.readValue(jsonEvent, AuditEventMessage.class);

            // Mapeia o objeto de mensagem para a entidade JPA AuditEvent
            AuditEvent auditEvent = AuditEvent.builder()
                .eventId(eventMessage.getEventId())
                .timestamp(eventMessage.getTimestamp())
                .eventType(eventMessage.getEventType())
                .userId(eventMessage.getUserId())
                .performedBy(eventMessage.getPerformedBy())
                .ipAddress(eventMessage.getIpAddress())
                .details(eventMessage.getDetails())
                .build();

            auditEventRepository.save(auditEvent);
            log.info("Evento de auditoria salvo no BD: {} - {}", auditEvent.getEventType(), auditEvent.getEventId());
        } catch (Exception e) {
            log.error("Erro ao processar e salvar evento de auditoria: {}", e.getMessage(), e);
            // Em um cenário real, você pode querer enviar mensagens com erro para uma Dead Letter Queue (DLQ)
            // para inspeção manual e reprocessamento.
        }
    }
}
