# Auditory - Microserviço de Auditoria Centralizada

Um microserviço REST robusto desenvolvido com **Spring Boot 3.5.9** e **Java 21** para consumir, persistir e consultar eventos de auditoria de diversas aplicações. Garante rastreabilidade de ações de usuários e sistemas, conformidade regulatória e segurança dos dados de log através de uma arquitetura orientada a eventos com RabbitMQ.

## 📋 Características

- **Consumo de Eventos**: Integração com RabbitMQ para receber eventos de auditoria de múltiplos serviços
- **Persistência Centralizada**: Armazenamento estruturado de eventos de auditoria em PostgreSQL com JSONB
- **Consultas Flexíveis**: Endpoints para buscar eventos por ID, usuário, tipo, intervalo de tempo
- **Rastreabilidade Completa**: Registra usuário, origem (IP), tipo de evento e detalhes customizáveis
- **Índices Otimizados**: Índices de banco de dados para consultas rápidas
- **Migrações com Flyway**: Controle de versão do esquema de banco de dados
- **Documentação Automática**: Swagger UI/OpenAPI 3.0 integrado
- **Arquitetura Orientada a Eventos**: Pattern produtor/consumidor com fila durável
- **JSONB Support**: Armazenamento de detalhes complexos em formato JSON no PostgreSQL

## 🚀 Tecnologias

| Tecnologia | Versão | Propósito |
|-----------|--------|----------|
| Java | 21 | Linguagem base |
| Spring Boot | 3.5.9 | Framework principal |
| Spring AMQP | - | Integração com RabbitMQ |
| Spring Data JPA | - | ORM/Persistência |
| Spring Web | - | API REST |
| PostgreSQL | - | Banco de dados |
| RabbitMQ | - | Message Broker |
| Flyway | 9.x | Migração de BD |
| Lombok | - | Redução de boilerplate |
| Springdoc OpenAPI | 2.3.0 | Documentação API |

## 📦 Instalação e Configuração

### Pré-requisitos

- Java 21 instalado
- PostgreSQL instalado e rodando
- RabbitMQ instalado e rodando
- Maven 3.8.1+

### Passos de Instalação

1. **Clone o repositório**
   ```bash
   git clone https://github.com/seu-usuario/auditory.git
   cd auditory
   ```

2. **Configure o PostgreSQL**
   
   Crie um banco de dados:
   ```sql
   CREATE DATABASE auditory_db;
   ```

3. **Configure o RabbitMQ**
   
   Acesso padrão:
   ```
   URL: http://localhost:15672
   User: guest
   Password: guest
   ```

4. **Configure as variáveis de ambiente**
   
   Crie um arquivo `application-local.properties`:
   ```properties
   # PostgreSQL
   spring.datasource.url=jdbc:postgresql://localhost:5432/auditory_db
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
   
   # RabbitMQ
   spring.rabbitmq.host=localhost
   spring.rabbitmq.port=5672
   spring.rabbitmq.username=guest
   spring.rabbitmq.password=guest
   audit.queue.name=audit_events_queue
   
   # Flyway
   spring.flyway.enabled=true
   ```

5. **Construa e execute**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

6. **Acesse a documentação**
   ```
   http://localhost:8081/swagger-ui.html
   ```

## 📚 Estrutura do Projeto

```
src/main/java/br/com/sistema/audit/
├── config/
│   ├── OpenApiConfig.java          # Configuração Swagger/OpenAPI
│   └── RabbitMQConfig.java         # Configuração de filas RabbitMQ
├── controller/
│   └── AuditController.java        # Endpoints de consulta de auditoria
├── model/
│   ├── AuditEvent.java             # Entidade JPA persistida
│   └── AuditEventMessage.java      # DTO para mensagens RabbitMQ
├── consumer/
│   └── AuditEventConsumer.java     # Consumidor de eventos RabbitMQ
├── repositories/
│   └── AuditEventRepository.java   # Acesso a dados de eventos
└── Startup.java                    # Classe principal

src/main/resources/
├── application.properties           # Configuração geral
├── application-prod.properties      # Configuração produção
└── db/migration/
    └── V1__create_audit_events_table.sql  # Migration Flyway
```

## 🔌 Arquitetura de Eventos

### Fluxo de Processamento

```
Aplicações Produtoras
        ↓
   RabbitMQ Queue
   (audit_events_queue)
        ↓
AuditEventConsumer
        ↓
   PostgreSQL
   (audit_events)
        ↓
AuditController (Consultas)
```

### Padrão Produtor/Consumidor

1. **Produtor**: Aplicações enviam eventos JSON para a fila `audit_events_queue`
2. **Consumidor**: `AuditEventConsumer` recebe, desserializa e persiste eventos
3. **Persistência**: Eventos armazenados em `audit_events` com detalhes em JSONB
4. **Consulta**: Endpoints REST para retriar eventos armazenados

## 🔌 Endpoints da API

### Listar Eventos
- **GET** `/api/audit` - Retorna todos os eventos (considere paginação em produção)

### Buscar por ID
- **GET** `/api/audit/{eventId}` - Retorna evento específico

### Filtrar por Usuário
- **GET** `/api/audit/user/{userId}` - Retorna eventos de um usuário

### Filtrar por Tipo
- **GET** `/api/audit/type/{eventType}` - Retorna eventos de um tipo específico

### Filtrar por Intervalo de Tempo
- **GET** `/api/audit/time-range?start=2026-01-01T00:00:00&end=2026-01-31T23:59:59`
- Retorna eventos dentro do intervalo de tempo

## 📊 Modelos de Dados

### AuditEvent (Entidade JPA)
```json
{
  "eventId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
  "timestamp": "2026-01-17T23:03:00",
  "eventType": "USER_CREATED",
  "userId": "user123",
  "performedBy": "admin_api",
  "ipAddress": "192.168.1.100",
  "details": "{\"username\":\"novo_usuario\", \"email\":\"novo@email.com\"}"
}
```

### Tipos de Eventos Comuns
- `USER_CREATED` - Novo usuário criado
- `USER_DELETED` - Usuário removido
- `LOGIN_SUCCESS` - Login bem-sucedido
- `LOGIN_FAILED` - Falha de autenticação
- `DATA_ACCESS` - Acesso a dados
- `DATA_MODIFIED` - Modificação de dados
- `PERMISSION_CHANGED` - Alteração de permissões
- `ADMIN_ACTION` - Ação administrativo

## 📨 Exemplo de Produtor de Eventos

Exemplo em Java de como enviar eventos para a fila:

```java
@Component
@RequiredArgsConstructor
public class AuditEventProducer {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void sendAuditEvent(AuditEventMessage event) {
        rabbitTemplate.convertAndSend(
            "audit_events_queue", 
            new ObjectMapper().writeValueAsString(event)
        );
    }
}
```

Uso:
```java
AuditEventMessage event = new AuditEventMessage(
    "USER_CREATED",
    "user123",
    "admin_api",
    "192.168.1.100",
    "{\"username\":\"novo_usuario\"}"
);

auditEventProducer.sendAuditEvent(event);
```

## 📝 Exemplos de Uso

### Buscar todos os eventos
```bash
curl -X GET http://localhost:8081/api/audit \
  -H "Content-Type: application/json"
```

### Buscar evento por ID
```bash
curl -X GET http://localhost:8081/api/audit/a1b2c3d4-e5f6-7890-1234-567890abcdef \
  -H "Content-Type: application/json"
```

### Buscar eventos de um usuário
```bash
curl -X GET http://localhost:8081/api/audit/user/user123 \
  -H "Content-Type: application/json"
```

### Buscar eventos por tipo
```bash
curl -X GET http://localhost:8081/api/audit/type/LOGIN_SUCCESS \
  -H "Content-Type: application/json"
```

### Buscar eventos em intervalo de tempo
```bash
curl -X GET "http://localhost:8081/api/audit/time-range?start=2026-01-01T00:00:00&end=2026-01-31T23:59:59" \
  -H "Content-Type: application/json"
```

## 🗄️ Esquema do Banco de Dados

### Tabela: audit_events

| Coluna | Tipo | Descrição |
|--------|------|-----------|
| event_id | VARCHAR(255) PK | Identificador único (UUID) |
| timestamp | TIMESTAMP | Quando o evento ocorreu |
| event_type | VARCHAR(100) | Tipo do evento (USER_CREATED, etc) |
| user_id | VARCHAR(255) | ID do usuário afetado |
| performed_by | VARCHAR(255) | ID do usuário/sistema que atuou |
| ip_address | VARCHAR(45) | IP de origem da requisição |
| details | JSONB | Detalhes adicionais em JSON |

### Índices
- `idx_audit_events_timestamp` - Busca rápida por data/hora
- `idx_audit_events_user_id` - Busca rápida por usuário
- `idx_audit_events_event_type` - Busca rápida por tipo

## 🔄 Fluxo de Consumo de Eventos

1. **Receção**: `@RabbitListener` recebe mensagem JSON da fila
2. **Desserialização**: Jackson desserializa JSON para `AuditEventMessage`
3. **Mapeamento**: Converte `AuditEventMessage` para entidade `AuditEvent`
4. **Persistência**: Salva no PostgreSQL via JPA
5. **Log**: Registra sucesso ou erro com SLF4J
6. **Tratamento de Erro**: Erros logados (considere DLQ em produção)

## ⚙️ Configuração de Ambiente

### Desenvolvimento (local)
```properties
spring.profiles.active=local
spring.jpa.show-sql=true
logging.level.br.com.sistema=DEBUG
```

### Produção
```properties
spring.profiles.active=prod
server.port=8080
logging.level.root=INFO
```

Variáveis de ambiente para produção:
- `POSTGRES_HOST` - Host do PostgreSQL
- `POSTGRES_PORT` - Porta do PostgreSQL
- `POSTGRES_DB` - Nome do banco
- `POSTGRES_USER` - Usuário do BD
- `POSTGRES_PASSWORD` - Senha do BD
- `RABBITMQ_HOST` - Host do RabbitMQ
- `RABBITMQ_PORT` - Porta do RabbitMQ
- `RABBITMQ_USERNAME` - User RabbitMQ
- `RABBITMQ_PASSWORD` - Senha RabbitMQ
- `RABBITMQ_QUEUE` - Nome da fila

## 🧪 Testes

Execute os testes:
```bash
mvn test
```

## 🛡️ Boas Práticas em Produção

1. **Dead Letter Queue (DLQ)**: Configure DLQ para mensagens com erro
2. **Replicação de BD**: Use replicação PostgreSQL para alta disponibilidade
3. **Cluster RabbitMQ**: Implante RabbitMQ em cluster
4. **Paginação**: Adicione paginação ao endpoint `GET /api/audit`
5. **Segurança**: Implemente autenticação/autorização com Spring Security
6. **Retenção de Dados**: Define políticas de limpeza de eventos antigos
7. **Monitoramento**: Integre Prometheus/Grafana para métricas
8. **Compressão**: Comprima eventos JSONB grandes
9. **Particionamento**: Particione tabela por data para melhor performance
10. **Backup**: Configure backup automático do PostgreSQL

## 📖 Documentação da API

Acesse a documentação interativa em:
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/api-docs

## 🤝 Contribuindo

1. Faça um Fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está licenciado sob a MIT License - veja o arquivo LICENSE para detalhes.

## 👨‍💻 Autor

**César Augusto**
- Email: cesar.augusto.rj1@gmail.com
- Portfolio: https://portfolio.cesaraugusto.dev.br/

## 📞 Suporte

Para suporte, abra uma issue no repositório ou entre em contato pelo email.

---

**Última atualização**: Janeiro 2026
