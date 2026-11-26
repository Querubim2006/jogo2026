package com.curso.jogo2026.api.error;

import java.time.Instant;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representação padronizada de um erro da API")
public record ApiError(
        @Schema(description = "Instante em que o erro ocorreu",
                example = "2026-11-24T12:30:00Z")
        Instant timestamp,
        @Schema(description = "Código HTTP numérico", example = "400")
        int status,
        @Schema(description = "Nome do status HTTP", example = "Bad Request")
        String error,
        @Schema(description = "Mensagem segura para o consumidor")
        String message,
        @Schema(description = "Caminho da requisição", example = "/api/jogos")
        String path,
        @Schema(description = "Mensagens de validação indexadas pelo campo")
        Map<String, String> fields
) {
}
