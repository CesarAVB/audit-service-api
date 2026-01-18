package br.com.sistema.audit.repositories;

import br.com.sistema.audit.model.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, String> {
    List<AuditEvent> findByUserId(String userId);
    List<AuditEvent> findByEventType(String eventType);
    List<AuditEvent> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
