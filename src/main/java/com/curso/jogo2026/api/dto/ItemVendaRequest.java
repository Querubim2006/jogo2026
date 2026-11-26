package com.curso.jogo2026.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ItemVendaRequest(@NotNull @Positive @Schema(example = "1") Long produtoId,
                               @NotNull @DecimalMin("0.001") @Digits(integer = 15, fraction = 3)
                               @Schema(description = "Quantidade positiva, aceita até três casas decimais", example = "2.000") BigDecimal quantidade) { }
