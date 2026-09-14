package com.curso.jogo2026.api.dto;

import com.curso.jogo2026.domain.Status;

import java.math.BigDecimal;
import java.time.LocalDate;

public record JogoResponse(
        Long id,
        String codigoBarras,
        String titulo,
        BigDecimal saldoEstoque,
        BigDecimal valorUnitario,
        BigDecimal estoqueMinimo,
        BigDecimal valorEstoque,
        LocalDate dataLancamento,
        Status status,
        Long generoId,
        String generoNome,
        Long desenvolvedoraId,
        String desenvolvedoraNomeFantasia
) {
}