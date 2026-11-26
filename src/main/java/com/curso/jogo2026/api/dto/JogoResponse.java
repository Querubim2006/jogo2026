package com.curso.jogo2026.api.dto;

import com.curso.jogo2026.domain.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Representação completa de um jogo")
public record JogoResponse(
        @Schema(description = "Identificador", example = "10")
        Long id,
        @Schema(description = "Código único", example = "GP-EXEMPLO-001")
        String codigoBarras,
        @Schema(description = "Título", example = "Aventura de Exemplo")
        String titulo,
        @Schema(description = "Saldo atual de cópias", example = "10.000")
        BigDecimal saldoEstoque,
        @Schema(description = "Preço unitário", example = "199.90")
        BigDecimal valorUnitario,
        @Schema(description = "Limite para indicar reposição", example = "2.000")
        BigDecimal estoqueMinimo,
        @Schema(description = "Valor total do estoque atual", example = "1999.00")
        BigDecimal valorEstoque,
        @Schema(description = "Data de lançamento", example = "2026-09-10")
        LocalDate dataLancamento,
        @Schema(description = "Situação", example = "ATIVO")
        Status status,
        @Schema(description = "Identificador do gênero", example = "1")
        Long generoId,
        @Schema(description = "Nome do gênero", example = "Ação e aventura")
        String generoNome,
        @Schema(description = "Identificador da desenvolvedora", example = "1",
                nullable = true)
        Long desenvolvedoraId,
        @Schema(description = "Nome da desenvolvedora", example = "Estúdio Exemplo",
                nullable = true)
        String desenvolvedoraNome
){
}
