package com.curso.jogo2026.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI gamePortOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("GamePort API")
                        .description("API para cadastro, consulta e manutenção de jogos, "
                                + "gêneros, desenvolvedoras e estoque.")
                        .version("1.0.0")
                        .contact(new Contact().name("Projeto GamePort")));
    }
}
