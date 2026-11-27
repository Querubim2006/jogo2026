package com.curso.jogo2026.service;

import com.curso.jogo2026.domain.*;
import com.curso.jogo2026.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
        Pessoa pc = pessoaRepository.save(new Pessoa("Cliente Teste", "cliente" + marker + "@example.com", cpfUnico(marker), LocalDate.now()));
        Pessoa pv = pessoaRepository.save(new Pessoa("Vendedor Teste", "vendedor" + marker + "@example.com", cpfUnico(marker + 1), LocalDate.now()));
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

    @Test
    void impedeDuasVendasConcorrentesDeConsumiremOMesmoEstoque() throws Exception {
        long marker = System.nanoTime();
        Pessoa pessoaCliente = null;
        Pessoa pessoaVendedor = null;
        Cliente cliente = null;
        Colaborador vendedor = null;
        GeneroJogo genero = null;
        Jogo jogo = null;
        Venda primeira = null;
        Venda segunda = null;
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            pessoaCliente = pessoaRepository.save(new Pessoa(
                    "Cliente Concorrente", "cliente-concorrente" + marker + "@example.com",
                    cpfUnico(marker), LocalDate.now()));
            pessoaVendedor = pessoaRepository.save(new Pessoa(
                    "Vendedor Concorrente", "vendedor-concorrente" + marker + "@example.com",
                    cpfUnico(marker + 1), LocalDate.now()));
            cliente = clienteService.cadastrar(pessoaCliente.getId(), null);
            vendedor = colaboradorService.cadastrar(
                    pessoaVendedor.getId(), "VC" + marker,
                    FuncaoColaborador.VENDEDOR, LocalDate.now());
            genero = generoRepository.save(new GeneroJogo("Concorrencia" + marker));
            jogo = jogoService.cadastrar(
                    new Jogo("TX-CONC" + marker, "Jogo concorrente",
                            new BigDecimal("5.000"), new BigDecimal("10.00"),
                            LocalDate.now(), BigDecimal.ZERO),
                    genero.getId(), null, null);

            primeira = criarVendaComItem(cliente.getId(), vendedor.getId(), jogo.getId());
            segunda = criarVendaComItem(cliente.getId(), vendedor.getId(), jogo.getId());
            CountDownLatch inicioSimultaneo = new CountDownLatch(1);
            Long idPrimeiraVenda = primeira.getId();
            Long idSegundaVenda = segunda.getId();

            Future<ResultadoFinalizacao> resultadoPrimeiro = executor.submit(
                    () -> finalizarAposSinal(idPrimeiraVenda, inicioSimultaneo));
            Future<ResultadoFinalizacao> resultadoSegundo = executor.submit(
                    () -> finalizarAposSinal(idSegundaVenda, inicioSimultaneo));
            inicioSimultaneo.countDown();

            List<ResultadoFinalizacao> resultados = List.of(
                    resultadoPrimeiro.get(10, TimeUnit.SECONDS),
                    resultadoSegundo.get(10, TimeUnit.SECONDS));

            assertEquals(1, resultados.stream()
                    .filter(ResultadoFinalizacao::sucesso).count());
            assertEquals(1, resultados.stream()
                    .filter(resultado -> !resultado.sucesso()).count());
            assertTrue(resultados.stream()
                    .filter(resultado -> !resultado.sucesso())
                    .allMatch(resultado -> "Saldo de estoque insuficiente"
                            .equals(resultado.mensagem())));
            assertEquals(new BigDecimal("1.000"), jogoRepository.findById(jogo.getId())
                    .orElseThrow().getSaldoEstoque());

            List<StatusVenda> estados = List.of(
                    vendaService.buscarPorId(primeira.getId()).getStatus(),
                    vendaService.buscarPorId(segunda.getId()).getStatus());
            assertEquals(1, estados.stream()
                    .filter(StatusVenda.FINALIZADA::equals).count());
            assertEquals(1, estados.stream()
                    .filter(StatusVenda.ABERTA::equals).count());
        } finally {
            executor.shutdownNow();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } finally {
                if (primeira != null) vendaRepository.deleteById(primeira.getId());
                if (segunda != null) vendaRepository.deleteById(segunda.getId());
                if (jogo != null) jogoRepository.deleteById(jogo.getId());
                if (genero != null) generoRepository.deleteById(genero.getId());
                if (vendedor != null) colaboradorRepository.deleteById(vendedor.getId());
                if (cliente != null) clienteRepository.deleteById(cliente.getId());
                if (pessoaCliente != null) pessoaRepository.deleteById(pessoaCliente.getId());
                if (pessoaVendedor != null) pessoaRepository.deleteById(pessoaVendedor.getId());
            }
        }
    }

    private Venda criarVendaComItem(Long clienteId, Long vendedorId, Long jogoId) {
        Venda venda = vendaService.cadastrar(clienteId, vendedorId);
        vendaService.adicionarItem(venda.getId(), jogoId, new BigDecimal("4.000"));
        return venda;
    }

    private ResultadoFinalizacao finalizarAposSinal(
            Long vendaId,
            CountDownLatch inicioSimultaneo) throws InterruptedException {
        if (!inicioSimultaneo.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("As tarefas não receberam o sinal de início");
        }
        try {
            vendaService.finalizar(vendaId);
            return new ResultadoFinalizacao(true, null);
        } catch (IllegalArgumentException exception) {
            return new ResultadoFinalizacao(false, exception.getMessage());
        }
    }

    private String cpfUnico(long seed) {
        long numero = Math.floorMod(seed, 1_000_000_000L);
        while (true) {
            String base = String.format("%09d", numero);
            String cpfComPrimeiroDigito = base + calcularDigitoCpf(base, 10);
            String cpf = cpfComPrimeiroDigito
                    + calcularDigitoCpf(cpfComPrimeiroDigito, 11);
            if (DocumentoFiscal.cpfValido(cpf) && !pessoaRepository.existsByCpf(cpf)) {
                return cpf;
            }
            numero = (numero + 1) % 1_000_000_000L;
        }
    }

    private int calcularDigitoCpf(String base, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += Character.getNumericValue(base.charAt(i)) * (pesoInicial - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private record ResultadoFinalizacao(boolean sucesso, String mensagem) { }
}
