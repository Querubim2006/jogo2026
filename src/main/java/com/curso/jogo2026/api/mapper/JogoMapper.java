package com.curso.jogo2026.api.mapper;

import com.curso.jogo2026.api.dto.JogoRequest;
import com.curso.jogo2026.api.dto.JogoResponse;
import com.curso.jogo2026.domain.Desenvolvedora;
import com.curso.jogo2026.domain.Jogo;
import org.springframework.stereotype.Component;

@Component
public class JogoMapper {

    public Jogo toEntity(JogoRequest request) {
        return new Jogo(
                request.codigoBarras(),
                request.titulo(),
                request.saldoEstoque(),
                request.valorUnitario(),
                request.dataLancamento(),
                request.estoqueMinimo()
        );
    }

    public JogoResponse toResponse(Jogo jogo) {
        Desenvolvedora desenvolvedora = jogo.getDesenvolvedora();

        return new JogoResponse(
                jogo.getId(),
                jogo.getCodigoBarras(),
                jogo.getTitulo(),
                jogo.getSaldoEstoque(),
                jogo.getValorUnitario(),
                jogo.getEstoqueMinimo(),
                jogo.calcularValorEstoque(),
                jogo.getDataLancamento(),
                jogo.getStatus(),
                jogo.getGenero().getId(),
                jogo.getGenero().getNome(),
                desenvolvedora == null ? null : desenvolvedora.getId(),
                desenvolvedora == null ? null : desenvolvedora.getNomeFantasia()
        );
    }
}