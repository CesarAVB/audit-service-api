package br.com.sistema.audit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(buildInfo()).servers(buildServers());
    }

    /**
     * Constrói as informações da API (título, versão, descrição, contato e licença)
     */
    private Info buildInfo() {
        return new Info()
                .title("Audit Service API - Microserviço de Auditoria Centralizada")
                .version("1.0.0") // Mantenha a versão 1.0.0 para o Audit Service
                .description("Microserviço REST especializado em consumir, persistir e consultar eventos de auditoria de diversas aplicações. " +
                             "Garante a rastreabilidade de ações de usuários e sistemas, conformidade e segurança dos dados de log. " +
                             "Desenvolvido com Spring Boot, Java 17+, RabbitMQ e PostgreSQL.")
                .contact(buildContact())
                .license(buildLicense());
    }

    /**
     * Constrói as informações de contato
     */
    private Contact buildContact() {
        return new Contact()
                .name("César Augusto") // Seu nome
                .email("cesar.augusto.rj1@gmail.com") // Seu e-mail
                .url("https://portfolio.cesaraugusto.dev.br/"); // Seu portfólio
    }

    /**
     * Constrói as informações de licença do projeto
     */
    private License buildLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    /**
     * Constrói a lista de servidores disponíveis para a API
     */
    private List<Server> buildServers() {
        Server developmentServer = new Server()
                .url("http://localhost:8081") // Porta do Audit Service
                .description("Servidor de Desenvolvimento - Ambiente Local");
        // Se você tiver um ambiente de produção para o Audit Service, adicione aqui:
        // Server productionServer = new Server()
        //         .url("https://api-audit.yourcompany.com.br/") // Exemplo de URL de produção
        //         .description("Servidor de Produção - Ambiente Produtivo");
        return List.of(developmentServer); // Retorna apenas o servidor de desenvolvimento por enquanto
    }
}
