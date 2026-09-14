package com.curso.jogo2026.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DesenvolvedoraRequest(
        @NotBlank(message = "Nome fantasia é obrigatório")
        @Size(max = 150, message = "Nome fantasia deve possuir no máximo 150 caracteres")
        String nomeFantasia,

        @NotBlank(message = "CNPJ é obrigatório")
        @Size(min = 14, max = 18, message = "CNPJ deve possuir entre 14 e 18 caracteres")
        String cnpj
) {
}