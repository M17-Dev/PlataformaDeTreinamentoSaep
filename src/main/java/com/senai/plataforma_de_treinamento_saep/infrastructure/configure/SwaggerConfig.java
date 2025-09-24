package com.senai.plataforma_de_treinamento_saep.infrastructure.configure;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI hugginSenaiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Plataforma de Treinamento Para o Saep")
                        .description("Plataforma para realizar testes práticos")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Equipe Vitor")
                                .email("suporte@cliente.com"))
                );
    }
}
