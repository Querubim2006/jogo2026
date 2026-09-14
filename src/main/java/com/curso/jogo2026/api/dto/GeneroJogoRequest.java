package com.curso.jogo2026.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GeneroJogoRequest(
        @NotBlank(message = "Nome do gênero é obrigatório")
        @Size(max = 120, message = "Nome do gênero deve possuir no máximo 120 caracteres")
        String nome
) {
}
