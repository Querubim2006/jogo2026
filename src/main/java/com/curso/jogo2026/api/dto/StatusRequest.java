package com.curso.jogo2026.api.dto;

import com.curso.jogo2026.domain.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Novo status do recurso")
public record StatusRequest(
        @Schema(description = "Status permitido", example = "INATIVO")
        @NotNull(message = "Status é obrigatório") Status status
){
}
