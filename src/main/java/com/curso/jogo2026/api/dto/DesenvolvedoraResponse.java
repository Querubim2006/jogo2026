package com.curso.jogo2026.api.dto;

import com.curso.jogo2026.domain.Status;

public record DesenvolvedoraResponse(
        Long id,
        String nomeFantasia,
        String cnpj,
        Status status
) {

}
