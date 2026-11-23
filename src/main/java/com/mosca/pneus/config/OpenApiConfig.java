package com.mosca.pneus.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI moscaOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Rodízio API")
                .description("Cadastro de pneus e equipamentos, com montagem na posição e histórico de movimentação.")
                .version("1.0.0"));
    }
}
