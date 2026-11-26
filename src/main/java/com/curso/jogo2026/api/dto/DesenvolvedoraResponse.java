package com.curso.jogo2026.api.dto;

import com.curso.jogo2026.domain.Status;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representação de uma desenvolvedora")
public record DesenvolvedoraResponse(
        @Schema(description = "Identificador", example = "1")
        Long id,
        @Schema(description = "Nome de divulgação", example = "Estúdio Exemplo")
        String nomeFantasia,
        @Schema(description = "CNPJ fictício", example = "00000000000000")
        String cnpj,
        @Schema(description = "Situação da desenvolvedora", example = "ATIVO")
        Status status
) {

}
