package com.curso.jogo2026.service;

import com.curso.jogo2026.domain.*;
import com.curso.jogo2026.exception.RecursoNaoEncontradoException;
import com.curso.jogo2026.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VendaService {
    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final JogoRepository jogoRepository;
    public VendaService(VendaRepository vendaRepository, ClienteRepository clienteRepository,
                        ColaboradorRepository colaboradorRepository, JogoRepository jogoRepository) {
        this.vendaRepository = vendaRepository; this.clienteRepository = clienteRepository;
        this.colaboradorRepository = colaboradorRepository; this.jogoRepository = jogoRepository;
    }
    @Transactional public Venda cadastrar(Long clienteId, Long vendedorId) {
        Cliente cliente = clienteRepository.findOneById(clienteId).orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
        Colaborador vendedor = colaboradorRepository.findOneById(vendedorId).orElseThrow(() -> new RecursoNaoEncontradoException("Colaborador não encontrado"));
        validarParticipantes(cliente, vendedor);
        return vendaRepository.save(new Venda(cliente, vendedor, LocalDateTime.now()));
    }
    @Transactional(readOnly = true) public Venda buscarPorId(Long id) { return buscarEntidade(id); }
    @Transactional(readOnly = true) public Page<Venda> pesquisar(StatusVenda status, Long clienteId, LocalDate dataInicial, LocalDate dataFinal,
                                                                    int pagina, int tamanho, String ordenarPor, String direcao) {
        if (dataInicial != null && dataFinal != null && dataInicial.isAfter(dataFinal)) throw new IllegalArgumentException("Data inicial não pode ser posterior à data final");
        Pageable pageable = PessoaService.pageable(pagina, tamanho, ordenarPor, direcao, List.of("id", "dataVenda", "status"));
        Specification<Venda> filtros = (root, query, cb) -> cb.conjunction();
        if (status != null) filtros = filtros.and((root,q,cb) -> cb.equal(root.get("status"), status));
        if (clienteId != null) filtros = filtros.and((root,q,cb) -> cb.equal(root.get("cliente").get("id"), clienteId));
        if (dataInicial != null) filtros = filtros.and((root,q,cb) -> cb.greaterThanOrEqualTo(root.get("dataVenda"), dataInicial.atStartOfDay()));
        if (dataFinal != null) filtros = filtros.and((root,q,cb) -> cb.lessThan(root.get("dataVenda"), dataFinal.plusDays(1).atStartOfDay()));
        return vendaRepository.findAll(filtros, pageable);
    }
    @Transactional public Venda adicionarItem(Long vendaId, Long jogoId, BigDecimal quantidade) {
        Venda venda = buscarEntidade(vendaId);
        Jogo jogo = jogoRepository.findById(jogoId).orElseThrow(() -> new RecursoNaoEncontradoException("Jogo não encontrado"));
        if (jogo.getStatus() != Status.ATIVO) throw new IllegalArgumentException("Jogo deve estar ativo");
        venda.adicionarItem(jogo, quantidade);
        return venda;
    }
    @Transactional public Venda removerItem(Long vendaId, Long itemId) { Venda venda = buscarEntidade(vendaId); venda.removerItem(itemId); return venda; }
    @Transactional public Venda finalizar(Long id) {
        Venda venda = buscarEntidade(id);
        validarParticipantes(venda.getCliente(), venda.getVendedor());
        for (ItemVenda item : venda.getItens()) {
            if (item.getProduto().getStatus() != Status.ATIVO) throw new IllegalArgumentException("Todos os jogos da venda devem estar ativos");
            item.getProduto().retirarEstoque(item.getQuantidade());
        }
        venda.finalizar();
        return venda;
    }
    @Transactional public Venda cancelar(Long id) { Venda venda = buscarEntidade(id); venda.cancelar(); return venda; }
    private Venda buscarEntidade(Long id) { return vendaRepository.findOneById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Venda não encontrada")); }
    private void validarParticipantes(Cliente cliente, Colaborador vendedor) {
        if (cliente.getPessoa().getStatus() != Status.ATIVO) throw new IllegalArgumentException("Cliente deve estar ativo");
        if (vendedor.getPessoa().getStatus() != Status.ATIVO) throw new IllegalArgumentException("Vendedor deve estar ativo");
        if (vendedor.getFuncao() != FuncaoColaborador.VENDEDOR && vendedor.getFuncao() != FuncaoColaborador.GERENTE)
            throw new IllegalArgumentException("Somente vendedor ou gerente pode realizar vendas");
    }
}
