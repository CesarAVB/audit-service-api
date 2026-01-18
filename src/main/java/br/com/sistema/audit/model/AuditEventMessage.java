package br.com.sistema.audit.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Representa um evento de auditoria recebido de outros serviços.")
public class AuditEventMessage {

    @Schema(description = "Identificador único do evento de auditoria.", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
    private String eventId;

    @Schema(description = "Timestamp da ocorrência do evento.", example = "2026-01-17T23:03:00")
    private LocalDateTime timestamp;

    @Schema(description = "Tipo do evento de auditoria (ex: USER_CREATED, LOGIN_SUCCESS, DATA_ACCESS).", example = "USER_CREATED")
    private String eventType;

    @Schema(description = "ID do usuário principal afetado ou relacionado ao evento.", example = "user123")
    private String userId;

    @Schema(description = "ID do usuário ou sistema que realizou a ação.", example = "admin_api")
    private String performedBy;

    @Schema(description = "Endereço IP de origem da requisição.", example = "192.168.1.100")
    private String ipAddress;

    @Schema(description = "Detalhes adicionais do evento em formato JSON.", example = "{\"username\":\"novo_usuario\", \"email\":\"novo@email.com\"}")
    private String details; // Armazenaremos como String JSON, que será persistido como JSONB no BD.

    // Construtor customizado para facilitar a criação sem precisar do builder para campos básicos
    // Útil para testes ou para um produtor simples
    public AuditEventMessage(String eventType, String userId, String performedBy, String ipAddress, String details) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.eventType = eventType;
        this.userId = userId;
        this.performedBy = performedBy;
        this.ipAddress = ipAddress;
        this.details = details;
    }
}
