    -- V1__create_audit_events_table.sql
    -- Script de migração Flyway para criar a tabela de eventos de auditoria.

    CREATE TABLE audit_events (
        event_id VARCHAR(255) PRIMARY KEY,
        timestamp TIMESTAMP NOT NULL,
        event_type VARCHAR(100) NOT NULL,
        user_id VARCHAR(255),
        performed_by VARCHAR(255),
        ip_address VARCHAR(45),
        details JSONB
    );

    -- Índices para otimizar consultas comuns
    -- Estes índices ajudarão a acelerar as buscas por data/hora, usuário e tipo de evento.
    CREATE INDEX idx_audit_events_timestamp ON audit_events (timestamp);
    CREATE INDEX idx_audit_events_user_id ON audit_events (user_id);
    CREATE INDEX idx_audit_events_event_type ON audit_events (event_type);
