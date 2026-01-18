package br.com.sistema.audit.model;

import java.time.LocalDateTime;

import br.com.sistema.audit.config.JsonStringConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "audit_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidade de banco de dados para um evento de auditoria.")
public class AuditEvent {

    @Id
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

    @Column(columnDefinition = "jsonb") // Informa ao Hibernate que a coluna é JSONB
    @Convert(converter = JsonStringConverter.class) // Usa um conversor customizado para String
    @Schema(description = "Detalhes adicionais do evento em formato JSON.", example = "{\"username\":\"novo_usuario\", \"email\":\"novo@email.com\"}")
    private String details;
}
