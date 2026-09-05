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
        assertEquals("PS4,PS5 e PC", jogo.getDescricao());
        assertEquals(Status.ATIVO, jogo.getStatus());
        assertEquals(LocalDate.of(2026, 8, 20), jogo.getDataCadastro());
    }

    @Test
    void deveCalcularValorDosEstoques(){
        Jogo jogo = novoJogo("3.000", "146.99");

        BigDecimal valorEstoque=jogo.calcularValorEstoque();

        assertEquals(0, new BigDecimal("440.97").compareTo(valorEstoque));
    }

    @Test
    void deveReceberERetirarEstoque(){
        Jogo jogo= novoJogo("3.000", "146.99");

        jogo.receberEstoque(new BigDecimal("2.500"));
        jogo.retirarEstoque(new BigDecimal("1.000"));

        assertEquals(0, new BigDecimal("4.500").compareTo(jogo.getSaldoEstoque()));
    }

    @Test
    void naoDeveRetirarQuantidadeMaiorQueOSaldo(){
        Jogo jogo=novoJogo("3.000", "146.99");

        IllegalArgumentException excecao=assertThrows(IllegalArgumentException.class,
                () -> jogo.retirarEstoque(new BigDecimal("3.001")));

        assertEquals("Saldo de estoque insuficiente",excecao.getMessage());
    }

    @Test
    void naoDeveCriarProdutoComCodigoEmBranco(){
        assertThrows(IllegalArgumentException.class,
                () -> new Jogo(" ","Corrida",
                        BigDecimal.ZERO,new BigDecimal("146.99"),LocalDate.of(2026,8,20)));
    }

    @Test
    void naoDeveCriarJogoComSaldoNegativo(){
        assertThrows(IllegalArgumentException.class,
                () -> novoJogo("-0.001","146.99"));
    }

    @Test
    void deveAlterarOStatusPorComportamentoExplicito(){
        Jogo jogo = novoJogo("3.000","146.99");

        jogo.desativar();
        assertEquals(Status.INATIVO, jogo.getStatus());

        jogo.ativar();
        assertEquals(Status.ATIVO, jogo.getStatus());
    }

    private Jogo novoJogo(String saldo, String valorUnitario) {
        return new Jogo(
                "7890000000001",
                "PS4,PS5 e PC",
                new BigDecimal(saldo),
                new BigDecimal(valorUnitario),
                LocalDate.of(2026, 8, 20));
    }
}
