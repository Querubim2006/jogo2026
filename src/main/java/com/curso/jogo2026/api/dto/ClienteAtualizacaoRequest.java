package com.curso.jogo2026.api.dto;

import jakarta.validation.constraints.Size;

public record ClienteAtualizacaoRequest(@Size(max = 30) String telefone) { }
