package com.curso.jogo2026.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PessoaTest {
    @Test void aceitaUmaPessoaComPapelDeClienteEColaborador() {
        Pessoa pessoa = new Pessoa("Ana Souza", "ANA@example.com", "52998224725", LocalDate.now());
        Cliente cliente = new Cliente(pessoa, null);
        Colaborador colaborador = new Colaborador(pessoa, "MAT-01", FuncaoColaborador.VENDEDOR, LocalDate.now());
        assertSame(pessoa, cliente.getPessoa());
        assertSame(pessoa, colaborador.getPessoa());
        assertEquals("ana@example.com", pessoa.getEmail());
    }
    @Test void rejeitaCpfComDigitoVerificadorInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new Pessoa("Ana", "ana@example.com", "52998224724", LocalDate.now()));
    }
}
