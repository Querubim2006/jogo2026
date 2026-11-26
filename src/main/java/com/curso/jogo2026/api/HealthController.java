package com.curso.jogo2026.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Infraestrutura", description = "Verificações técnicas da aplicação")
public class HealthController {

    @Operation(summary = "Verificar disponibilidade", description = "Retorna OK quando a API está respondendo.")
    @ApiResponse(responseCode = "200", description = "API disponível")
    @GetMapping("/api/health")
    public String health() {
        return "OK";
    }
}
