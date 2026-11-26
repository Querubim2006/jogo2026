package com.curso.jogo2026.service;

import com.curso.jogo2026.domain.*;
import com.curso.jogo2026.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class VendaServiceTest {
    @Autowired VendaService vendaService;
    @Autowired PessoaRepository pessoaRepository;
    @Autowired ClienteService clienteService;
    @Autowired ColaboradorService colaboradorService;
    @Autowired JogoService jogoService;
    @Autowired GeneroJogoRepository generoRepository;
    @Autowired JogoRepository jogoRepository;
    @Autowired VendaRepository vendaRepository;
    @Autowired ClienteRepository clienteRepository;
    @Autowired ColaboradorRepository colaboradorRepository;

    @Test void baixaMultiplosJogosComRollbackSeAlgumSaldoForInsuficiente() {
        long marker = System.nanoTime();
        Pessoa pc = pessoaRepository.save(new Pessoa("Cliente Teste", "cliente" + marker + "@example.com", "52998224725", LocalDate.now()));
        Pessoa pv = pessoaRepository.save(new Pessoa("Vendedor Teste", "vendedor" + marker + "@example.com", "11144477735", LocalDate.now()));
        Cliente cliente = clienteService.cadastrar(pc.getId(), null);
        Colaborador vendedor = colaboradorService.cadastrar(pv.getId(), "V" + marker, FuncaoColaborador.VENDEDOR, LocalDate.now());
        GeneroJogo genero = generoRepository.save(new GeneroJogo("Venda" + marker));
        Jogo primeiro = jogoService.cadastrar(new Jogo("TX-A-" + marker, "Primeiro", new BigDecimal("5.000"), new BigDecimal("10.00"), LocalDate.now(), BigDecimal.ZERO), genero.getId(), null, null);
        Jogo segundo = jogoService.cadastrar(new Jogo("TX-B-" + marker, "Segundo", BigDecimal.ZERO, new BigDecimal("15.00"), LocalDate.now(), BigDecimal.ZERO), genero.getId(), null, null);
        Venda venda = vendaService.cadastrar(cliente.getId(), vendedor.getId());
        vendaService.adicionarItem(venda.getId(), primeiro.getId(), BigDecimal.ONE);
        vendaService.adicionarItem(venda.getId(), segundo.getId(), BigDecimal.ONE);

        assertThrows(IllegalArgumentException.class, () -> vendaService.finalizar(venda.getId()));
        assertEquals(new BigDecimal("5.000"), jogoRepository.findById(primeiro.getId()).orElseThrow().getSaldoEstoque());
        assertEquals(StatusVenda.ABERTA, vendaService.buscarPorId(venda.getId()).getStatus());

        vendaRepository.deleteById(venda.getId());
        jogoRepository.deleteById(primeiro.getId());
        jogoRepository.deleteById(segundo.getId());
        generoRepository.deleteById(genero.getId());
        colaboradorRepository.deleteById(vendedor.getId());
        clienteRepository.deleteById(cliente.getId());
        pessoaRepository.deleteById(pc.getId());
        pessoaRepository.deleteById(pv.getId());
    }
}
