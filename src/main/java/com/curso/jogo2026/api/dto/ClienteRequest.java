package com.curso.jogo2026.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ClienteRequest(@Schema(example = "1") @NotNull @Positive Long pessoaId,
                             @Schema(example = "11999990000", nullable = true) @Size(max = 30) String telefone) { }
