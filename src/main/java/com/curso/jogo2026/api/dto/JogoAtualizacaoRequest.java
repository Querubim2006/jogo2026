
package com.curso.jogo2026.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Campos editáveis; código, saldo e status têm operações próprias")
public record JogoAtualizacaoRequest(

        @Schema(description = "Título do jogo", example = "Aventura Atualizada")
        @NotBlank(message = "Título é obrigatório")
        @Size(
                max = 150,
                message = "Título deve possuir no máximo 150 caracteres"
        )
        String titulo,

        @Schema(description = "Preço unitário", example = "219.90")
        @NotNull(message = "Valor unitário é obrigatório")
        @PositiveOrZero(message = "Valor unitário não pode ser negativo")
        BigDecimal valorUnitario,

        @Schema(description = "Data de lançamento", example = "2026-09-10")
        @NotNull(message = "Data de lançamento é obrigatória")
        LocalDate dataLancamento,

        @Schema(description = "Limite para indicar reposição", example = "3.000")
        @NotNull(message = "Estoque mínimo é obrigatório")
        @PositiveOrZero(message = "Estoque mínimo não pode ser negativo")
        BigDecimal estoqueMinimo,

        @Schema(description = "Identificador do gênero", example = "1")
        @NotNull(message = "Gênero é obrigatório")
        @Positive(message = "Identificador do gênero deve ser positivo")
        Long generoId,

        @Schema(description = "Identificador opcional da desenvolvedora",
                example = "1", nullable = true)
        @Positive(message = "Identificador da desenvolvedora deve ser positivo")
        Long desenvolvedoraId,

        @Schema(description = "Identificador opcional do fornecedor", example = "1", nullable = true)
        @Positive(message = "Identificador do fornecedor deve ser positivo")
        Long fornecedorId

) {
}
