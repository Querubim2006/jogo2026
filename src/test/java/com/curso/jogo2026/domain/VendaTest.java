package com.curso.jogo2026.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class VendaTest {
    private Venda novaVenda() {
        Pessoa pessoaCliente = new Pessoa("Ana", "ana@example.com", "52998224725", LocalDate.now());
        Pessoa pessoaVendedor = new Pessoa("Bruno", "bruno@example.com", "11144477735", LocalDate.now());
        return new Venda(new Cliente(pessoaCliente, null), new Colaborador(pessoaVendedor, "V-1", FuncaoColaborador.VENDEDOR, LocalDate.now()), LocalDateTime.now());
    }
    private Jogo jogo(String code, String price) { return new Jogo(code, "Jogo " + code, BigDecimal.TEN, new BigDecimal(price), LocalDate.now(), BigDecimal.ZERO); }
    @Test void preservaPrecoHistoricoDepoisDeAlterarPrecoAtual() {
        Venda venda = novaVenda(); Jogo jogo = jogo("A", "20.00");
        venda.adicionarItem(jogo, new BigDecimal("2.000"));
        jogo.alterarValorUnitario(new BigDecimal("23.00"));
        assertEquals(new BigDecimal("40.00"), venda.calcularTotal());
        assertEquals(new BigDecimal("20.00"), venda.getItens().get(0).getValorUnitario());
    }
    @Test void vendaVaziaNaoFinalizaEVendaFechadaNaoAceitaAlteracao() {
        Venda venda = novaVenda();
        assertThrows(IllegalArgumentException.class, venda::finalizar);
        venda.adicionarItem(jogo("B", "10.00"), BigDecimal.ONE);
        venda.finalizar();
        assertEquals(StatusVenda.FINALIZADA, venda.getStatus());
        assertThrows(IllegalStateException.class, () -> venda.adicionarItem(jogo("C", "10.00"), BigDecimal.ONE));
    }
}
