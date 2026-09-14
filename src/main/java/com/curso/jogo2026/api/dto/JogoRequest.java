package com.curso.jogo2026.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record JogoRequest(
        @NotBlank(message = "Código de barras é obrigatório")
        @Size(max = 50, message = "Código de barras deve possuir no máximo 50 caracteres")
        String codigoBarras,

        @NotBlank(message = "Título é obrigatório")
        @Size(max = 150, message = "Título deve possuir no máximo 150 caracteres")
        String titulo,

        @NotNull(message = "Saldo de estoque é obrigatório")
        @PositiveOrZero(message = "Saldo de estoque não pode ser negativo")
        BigDecimal saldoEstoque,

        @NotNull(message = "Valor unitário é obrigatório")
        @PositiveOrZero(message = "Valor unitário não pode ser negativo")
        BigDecimal valorUnitario,

        @NotNull(message = "Data de lançamento é obrigatória")
        LocalDate dataLancamento,

        @NotNull(message = "Estoque mínimo é obrigatório")
        @PositiveOrZero(message = "Estoque mínimo não pode ser negativo")
        BigDecimal estoqueMinimo,

        @NotNull(message = "Gênero é obrigatório")
        @Positive(message = "Identificador do gênero deve ser positivo")
        Long generoId,

        @Positive(message = "Identificador da desenvolvedora deve ser positivo")
        Long desenvolvedoraId
) {
}