package com.curso.jogo2026.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JogoTest {

    @Test
    void deveCriarJogoAtivoComDadosValidos() {
        Jogo jogo = novoJogo("3.000", "146.99");

        assertEquals("7890000000001", jogo.getCodigoBarras());
        assertEquals("God of War Ragnarök", jogo.getTitulo());
        assertEquals(Status.ATIVO, jogo.getStatus());
        assertEquals(LocalDate.of(2022, 11, 9), jogo.getDataLancamento());
    }

    @Test
    void deveCalcularValorDosEstoques() {
        Jogo jogo = novoJogo("3.000", "146.99");

        BigDecimal valorEstoque = jogo.calcularValorEstoque();

        assertEquals(0, new BigDecimal("440.97").compareTo(valorEstoque));
    }

    @Test
    void deveReceberERetirarEstoque() {
        Jogo jogo = novoJogo("3.000", "146.99");

        jogo.receberEstoque(new BigDecimal("2.500"));
        jogo.retirarEstoque(new BigDecimal("1.000"));

        assertEquals(
                0,
                new BigDecimal("4.500").compareTo(jogo.getSaldoEstoque())
        );
    }

    @Test
    void naoDeveRetirarQuantidadeMaiorQueOSaldo() {
        Jogo jogo = novoJogo("3.000", "146.99");

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> jogo.retirarEstoque(new BigDecimal("3.001"))
        );

        assertEquals("Saldo de estoque insuficiente", excecao.getMessage());
    }

    @Test
    void naoDeveCriarJogoComCodigoEmBranco() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Jogo(
                        " ",
                        "Forza Horizon 5",
                        BigDecimal.ZERO,
                        new BigDecimal("249.99"),
                        LocalDate.of(2021, 11, 9)
                )
        );
    }

    @Test
    void naoDeveCriarJogoComSaldoNegativo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> novoJogo("-0.001", "146.99")
        );
    }

    @Test
    void deveAlterarOStatusPorComportamentoExplicito() {
        Jogo jogo = novoJogo("3.000", "146.99");

        jogo.desativar();
        assertEquals(Status.INATIVO, jogo.getStatus());

        jogo.ativar();
        assertEquals(Status.ATIVO, jogo.getStatus());
    }

    private Jogo novoJogo(String saldo, String valorUnitario) {
        return new Jogo(
                "7890000000001",
                "God of War Ragnarök",
                new BigDecimal(saldo),
                new BigDecimal(valorUnitario),
                LocalDate.of(2022, 11, 9)
        );
    }
}