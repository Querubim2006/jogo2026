package com.curso.jogo2026.api.dto;

import java.math.BigDecimal;

public record ItemVendaResponse(Long id, Long produtoId, String produtoDescricao, BigDecimal quantidade,
                                BigDecimal valorUnitario, BigDecimal subtotal) { }
