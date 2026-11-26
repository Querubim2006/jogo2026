package com.curso.jogo2026.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VendaRequest(@NotNull @Positive @Schema(example = "1") Long clienteId,
                           @NotNull @Positive @Schema(example = "1") Long vendedorId) { }
