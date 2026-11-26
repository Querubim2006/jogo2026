package com.curso.jogo2026.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criar ou renomear um gênero de jogo")
public record GeneroJogoRequest(
        @Schema(description = "Nome do gênero", example = "Ação e aventura")
        @NotBlank(message = "Nome do gênero é obrigatório")
        @Size(max = 120, message = "Nome do gênero deve possuir no máximo 120 caracteres")
        String nome
) {
}
