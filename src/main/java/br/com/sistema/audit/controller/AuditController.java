package br.com.sistema.audit.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.sistema.audit.model.AuditEvent;
import br.com.sistema.audit.repositories.AuditEventRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@Tag(name = "Consulta de Auditoria", description = "API para consultar eventos de auditoria registrados.")
public class AuditController {

    private final AuditEventRepository auditEventRepository;

    @GetMapping
    @Operation(summary = "Retorna todos os eventos de auditoria (em um cenário real, use paginação).")
    @ApiResponse(responseCode = "200", description = "Lista de eventos de auditoria.")
    public ResponseEntity<List<AuditEvent>> getAllAuditEvents() {
        return ResponseEntity.ok(auditEventRepository.findAll());
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Retorna um evento de auditoria específico pelo seu ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento de auditoria encontrado."),
        @ApiResponse(responseCode = "404", description = "Evento de auditoria não encontrado.")
    })
    public ResponseEntity<AuditEvent> getAuditEventById(
        @Parameter(description = "ID do evento de auditoria.", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef") @PathVariable String eventId
    ) {
        return auditEventRepository.findById(eventId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Retorna eventos de auditoria por ID de usuário.")
    @ApiResponse(responseCode = "200", description = "Lista de eventos de auditoria para o usuário especificado.")
    public ResponseEntity<List<AuditEvent>> getAuditEventsByUserId(
        @Parameter(description = "ID do usuário.", required = true, example = "user123") @PathVariable String userId
    ) {
        return ResponseEntity.ok(auditEventRepository.findByUserId(userId));
    }

    @GetMapping("/type/{eventType}")
    @Operation(summary = "Retorna eventos de auditoria por tipo de evento.")
    @ApiResponse(responseCode = "200", description = "Lista de eventos de auditoria do tipo especificado.")
    public ResponseEntity<List<AuditEvent>> getAuditEventsByEventType(
        @Parameter(description = "Tipo do evento (ex: USER_CREATED).", required = true, example = "USER_CREATED") @PathVariable String eventType
    ) {
        return ResponseEntity.ok(auditEventRepository.findByEventType(eventType));
    }

    @GetMapping("/time-range")
    @Operation(summary = "Retorna eventos de auditoria dentro de um intervalo de tempo.")
    @ApiResponse(responseCode = "200", description = "Lista de eventos de auditoria no intervalo de tempo especificado.")
    public ResponseEntity<List<AuditEvent>> getAuditEventsByTimeRange(
        @Parameter(description = "Início do intervalo de tempo (formato yyyy-MM-ddTHH:mm:ss).", required = true, example = "2026-01-01T00:00:00")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
        @Parameter(description = "Fim do intervalo de tempo (formato yyyy-MM-ddTHH:mm:ss).", required = true, example = "2026-01-31T23:59:59")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(auditEventRepository.findByTimestampBetween(start, end));
    }
}
