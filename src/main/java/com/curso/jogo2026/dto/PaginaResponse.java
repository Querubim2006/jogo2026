
package com.curso.jogo2026.dto;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

public record PaginaResponse<T>(
        List<T> conteudo,
        int pagina,
        int tamanho,
        long totalElementos,
        int totalPaginas,
        boolean primeira,
        boolean ultima
) {

    public static <S, T> PaginaResponse<T> de(
            Page<S> pagina,
            Function<S, T> conversor
    ) {
        List<T> conteudo = pagina.getContent()
                .stream()
                .map(conversor)
                .toList();

        return new PaginaResponse<>(
                conteudo,
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.isFirst(),
                pagina.isLast()
        );
    }
}
