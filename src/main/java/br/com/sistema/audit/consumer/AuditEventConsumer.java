package br.com.sistema.audit.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.sistema.audit.model.AuditEvent;
import br.com.sistema.audit.model.AuditEventMessage;
import br.com.sistema.audit.repositories.AuditEventRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuditEventConsumer {

    private final AuditEventRepository auditEventRepository;
    private final ObjectMapper objectMapper;

    @Value("${audit.queue.name:audit_events_queue}")
    private String auditQueueName;

    public AuditEventConsumer(AuditEventRepository auditEventRepository, ObjectMapper objectMapper) {
        this.auditEventRepository = auditEventRepository;
        this.objectMapper = objectMapper;
   }

    @RabbitListener(queues = "${audit.queue.name:audit_events_queue}")
    public void receiveAuditEvent(String jsonEvent) {
        try {
            AuditEventMessage eventMessage = objectMapper.readValue(jsonEvent, AuditEventMessage.class);

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
        }
    }
}
