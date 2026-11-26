package com.curso.jogo2026.api.dto;

import com.curso.jogo2026.domain.FuncaoColaborador;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ColaboradorAtualizacaoRequest(@NotBlank @Size(max = 30) String matricula,
                                           @NotNull FuncaoColaborador funcao) { }
