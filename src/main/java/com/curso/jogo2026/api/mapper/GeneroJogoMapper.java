package com.curso.jogo2026.api.mapper;

import com.curso.jogo2026.api.dto.GeneroJogoRequest;
import com.curso.jogo2026.api.dto.GeneroJogoResponse;
import com.curso.jogo2026.domain.GeneroJogo;
import org.springframework.stereotype.Component;

@Component
public class GeneroJogoMapper {

    public GeneroJogo toEntity(GeneroJogoRequest request) {
        return new GeneroJogo(
                request.nome()
        );
    }

    public GeneroJogoResponse toResponse(GeneroJogo genero) {
        return new GeneroJogoResponse(
                genero.getId(),
                genero.getNome(),
                genero.getStatus()
        );
    }
}
