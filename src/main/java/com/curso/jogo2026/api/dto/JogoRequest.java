package com.curso.jogo2026.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Dados para cadastrar um jogo")
public record JogoRequest(
        @Schema(description = "Código único do jogo", example = "GP-EXEMPLO-001")
        @NotBlank(message = "Código de barras é obrigatório")
        @Size(max = 50, message = "Código de barras deve possuir no máximo 50 caracteres")
        String codigoBarras,

        @Schema(description = "Título do jogo", example = "Aventura de Exemplo")
        @NotBlank(message = "Título é obrigatório")
        @Size(max = 150, message = "Título deve possuir no máximo 150 caracteres")
        String titulo,

        @Schema(description = "Saldo inicial de cópias", example = "10.000")
        @NotNull(message = "Saldo de estoque é obrigatório")
        @PositiveOrZero(message = "Saldo de estoque não pode ser negativo")
        BigDecimal saldoEstoque,

        @Schema(description = "Preço unitário", example = "199.90")
        @NotNull(message = "Valor unitário é obrigatório")
        @PositiveOrZero(message = "Valor unitário não pode ser negativo")
        BigDecimal valorUnitario,

        @Schema(description = "Data de lançamento", example = "2026-09-10")
        @NotNull(message = "Data de lançamento é obrigatória")
        LocalDate dataLancamento,

        @Schema(description = "Limite para indicar reposição", example = "2.000")
        @NotNull(message = "Estoque mínimo é obrigatório")
        @PositiveOrZero(message = "Estoque mínimo não pode ser negativo")
        BigDecimal estoqueMinimo,

        @Schema(description = "Identificador de gênero existente", example = "1")
        @NotNull(message = "Gênero é obrigatório")
        @Positive(message = "Identificador do gênero deve ser positivo")
        Long generoId,

        @Schema(description = "Identificador opcional de desenvolvedora existente",
                example = "1", nullable = true)
        @Positive(message = "Identificador da desenvolvedora deve ser positivo")
        Long desenvolvedoraId
) {
}
