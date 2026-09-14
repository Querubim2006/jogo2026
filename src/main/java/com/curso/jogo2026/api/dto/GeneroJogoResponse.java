package com.curso.jogo2026.api.dto;

import com.curso.jogo2026.domain.Status;

public record GeneroJogoResponse(
        Long id,
        String nome,
        Status status
) {
}
