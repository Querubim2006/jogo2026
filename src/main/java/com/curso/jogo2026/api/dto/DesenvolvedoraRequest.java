package com.curso.jogo2026.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastrar ou alterar uma desenvolvedora")
public record DesenvolvedoraRequest(
        @Schema(description = "Nome de divulgação", example = "Estúdio Exemplo")
        @NotBlank(message = "Nome fantasia é obrigatório")
        @Size(max = 150, message = "Nome fantasia deve possuir no máximo 150 caracteres")
        String nomeFantasia,

        @Schema(description = "CNPJ fictício, entre 14 e 18 caracteres",
                example = "00000000000000")
        @NotBlank(message = "CNPJ é obrigatório")
        @Size(min = 14, max = 18, message = "CNPJ deve possuir entre 14 e 18 caracteres")
        String cnpj
) {
}
