package com.curso.jogo2026.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeneroJogoTest {

    @Test
    void deveAdicionarJogoEManterOsDoisLadosDaAssociacao() {
        GeneroJogo genero = new GeneroJogo("Hack and Slash");
        Jogo jogo = novoJogo("7890000000001");

        genero.adicionarJogo(jogo);

        assertEquals(1, genero.getJogos().size());
        assertSame(jogo, genero.getJogos().get(0));
        assertSame(genero, jogo.getGenero());
    }

    @Test
    void naoDeveAdicionarJogoNulo() {
        GeneroJogo genero = new GeneroJogo("Hack and Slash");

        assertThrows(
                NullPointerException.class,
                () -> genero.adicionarJogo(null)
        );
    }

    @Test
    void naoDeveAdicionarDoisJogosComOMesmoCodigo() {
        GeneroJogo genero = new GeneroJogo("Hack and Slash");

        genero.adicionarJogo(novoJogo("7890000000001"));

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> genero.adicionarJogo(novoJogo("7890000000001"))
        );

        assertEquals(
                "Código de barras já utilizado no gênero",
                excecao.getMessage()
        );
    }

    @Test
    void naoDevePermitirQueJogoPertençaADoisGeneros() {
        GeneroJogo hackAndSlash = new GeneroJogo("Hack and Slash");
        GeneroJogo corrida = new GeneroJogo("Corrida");

        Jogo jogo = novoJogo("7890000000001");

        hackAndSlash.adicionarJogo(jogo);

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> corrida.adicionarJogo(jogo)
        );

        assertEquals(
                "O jogo indicado já pertence a outro gênero",
                excecao.getMessage()
        );
    }

    @Test
    void naoDeveExporUmaListaInternaModificavel() {
        GeneroJogo genero = new GeneroJogo("Hack and Slash");

        Jogo jogo = novoJogo("7890000000001");
        genero.adicionarJogo(jogo);

        assertThrows(
                UnsupportedOperationException.class,
                () -> genero.getJogos().add(novoJogo("7890000000002"))
        );
    }

    private Jogo novoJogo(String codigoBarras) {
        return new Jogo(
                codigoBarras,
                "God of War Ragnarök",
                new BigDecimal("3.000"),
                new BigDecimal("146.99"),
                LocalDate.of(2022, 11, 9)
        );
    }
}