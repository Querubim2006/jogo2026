package com.curso.jogo2026.api.dto;

import com.curso.jogo2026.api.validation.CnpjValido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FornecedorRequest(@NotBlank @Size(max = 150) @Schema(example = "Distribuidora Exemplo") String razaoSocial,
                                @NotBlank @CnpjValido @Schema(description = "CNPJ de 14 dígitos sem pontuação; validação verifica os dígitos, não a existência", example = "11222333000181") String cnpj) { }
