package com.curso.jogo2026.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GeneroJogoTest {

    @Test
    void deveAdicionarProdutoEManejarOsDoisLadosDaAssociacao(){
        GeneroJogo genero= new GeneroJogo("HackAndSlash");
        Jogo jogo=novoJogo("7890000000001");

        genero.adicionarJogo(jogo);

        assertEquals(1,genero.getJogo().size());
        assertSame(jogo,genero.getJogo().get(0));
        assertSame(genero, jogo.getGenero());
    }

    @Test
    void naooDeveAdicionarProdutoNulo(){
        GeneroJogo genero= new GeneroJogo("HackAndSlash");

        assertThrows(NullPointerException.class,()->genero.adicionarJogo(null));
    }

    @Test
    void naoDeveAdicionarDoisProdutosComOMesmoCodigo(){
        GeneroJogo genero= new GeneroJogo("HackAndSlash");
        genero.adicionarJogo(novoJogo("7890000000001"));

        IllegalArgumentException excecao=assertThrows(
                IllegalArgumentException.class,
                ()->genero.adicionarJogo(novoJogo("7890000000001"))
        );
        assertEquals("Código de barras já utilizado no gênero",excecao.getMessage());
    }

    @Test
    void naoDevePermitirQueJogoPertençaADoisGeneros(){
        GeneroJogo HackAndSlash= new GeneroJogo("HackAndSlash");
        GeneroJogo Corrida = new GeneroJogo("Corrida");
        Jogo jogo = novoJogo("7890000000001");
        HackAndSlash.adicionarJogo(jogo);

        IllegalStateException excecao= assertThrows(
                IllegalStateException.class,
                ()-> Corrida.adicionarJogo(jogo));

        assertEquals("O jogo indicado já pertence a outro gênero", excecao.getMessage());
    }

    @Test
    void naoDeveExporUmaListaInternaModificavel(){
        GeneroJogo genero = new GeneroJogo("HackAndSlash");
        Jogo jogo= novoJogo("7890000000001");
        genero.adicionarJogo(jogo);

        assertThrows(UnsupportedOperationException.class,
                ()-> genero.getJogo().add(novoJogo("7890000000002")));
    }

    private Jogo novoJogo(String codigoBarras){
        return new Jogo(
                codigoBarras,
                "PS4 PS5 e PC",
                new BigDecimal("3.000"),
                new BigDecimal("146.99"),
                LocalDate.of(2026,8,20));
    }

}
