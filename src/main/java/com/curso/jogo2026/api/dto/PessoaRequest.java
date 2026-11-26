package com.curso.jogo2026.api.dto;

import com.curso.jogo2026.api.validation.CpfValido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados de identidade civil de uma pessoa")
public record PessoaRequest(
        @Schema(example = "Ana Souza") @NotBlank @Size(max = 150) String nome,
        @Schema(example = "ana@example.com") @NotBlank @Email @Size(max = 180) String email,
        @Schema(description = "CPF com 11 dígitos, sem pontuação", example = "52998224725") @NotBlank @CpfValido String cpf) { }
