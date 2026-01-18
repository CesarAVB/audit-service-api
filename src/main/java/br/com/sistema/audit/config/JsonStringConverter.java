package br.com.sistema.audit.config; // Ajuste o pacote

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter(autoApply = false) // Aplicação manual com @Convert
@Slf4j
public class JsonStringConverter implements AttributeConverter<String, String> {

    // Usamos uma instância estática do ObjectMapper para evitar recriação constante
    // e garantir que o JavaTimeModule esteja registrado.
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        // Adicione outras configurações globais para o ObjectMapper aqui, se necessário.
        // Por exemplo: objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.trim().isEmpty()) {
            return null;
        }
        // O PostgreSQL espera uma string JSON válida para a coluna JSONB.
        // Podemos validar se a string é um JSON válido antes de enviar.
        try {
            objectMapper.readTree(attribute); // Tenta parsear para validar
            return attribute; // Se for válido, retorna a própria string
        } catch (JsonProcessingException e) {
            log.error("Erro: String não é um JSON válido para a coluna JSONB. String: '{}', Erro: {}", attribute, e.getMessage());
            // Dependendo da sua política, você pode:
            // 1. Lançar uma exceção: throw new IllegalArgumentException("Invalid JSON string", e);
            // 2. Retornar null: return null;
            // 3. Retornar uma string JSON de erro: return "{\"error\":\"Invalid JSON\", \"originalValue\":\"" + attribute + "\"}";
            // Por enquanto, vamos lançar uma exceção para que o erro seja claro.
            throw new IllegalArgumentException("A string fornecida para a coluna 'details' não é um JSON válido.", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        // Ao ler do banco, o PostgreSQL já nos dará uma string JSON válida.
        // Não precisamos fazer nada além de retornar a string.
        return dbData;
    }
}
