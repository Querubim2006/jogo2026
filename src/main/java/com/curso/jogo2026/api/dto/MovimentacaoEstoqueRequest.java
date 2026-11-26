
package com.curso.jogo2026.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Quantidade de cópias movimentadas no estoque")
public record MovimentacaoEstoqueRequest(

        @Schema(description = "Quantidade positiva a receber ou retirar", example = "3.000")
        @NotNull(message = "Quantidade é obrigatória")
        @Positive(message = "Quantidade deve ser maior que zero")
        BigDecimal quantidade

) {
}
