
package com.curso.jogo2026.dto;

import java.util.List;
import java.util.function.Function;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

@Schema(description = "Página de resultados com metadados estáveis da API")
public record PaginaResponse<T>(
        @Schema(description = "Itens da página atual")
        List<T> conteudo,
        @Schema(description = "Número da página, iniciado em zero", example = "0")
        int pagina,
        @Schema(description = "Quantidade de itens por página", example = "20")
        int tamanho,
        @Schema(description = "Total de itens correspondentes aos filtros", example = "37")
        long totalElementos,
        @Schema(description = "Quantidade total de páginas", example = "2")
        int totalPaginas,
        @Schema(description = "Indica se esta é a primeira página", example = "true")
        boolean primeira,
        @Schema(description = "Indica se esta é a última página", example = "false")
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
