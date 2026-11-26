package com.curso.jogo2026.service;

import com.curso.jogo2026.domain.*;
import com.curso.jogo2026.exception.RecursoDuplicadoException;
import com.curso.jogo2026.exception.RecursoNaoEncontradoException;
import com.curso.jogo2026.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {
    private final ClienteRepository repository;
    private final PessoaRepository pessoaRepository;
    public ClienteService(ClienteRepository repository, PessoaRepository pessoaRepository) { this.repository = repository; this.pessoaRepository = pessoaRepository; }
    @Transactional public Cliente cadastrar(Long pessoaId, String telefone) {
        Pessoa pessoa = pessoaRepository.findById(pessoaId).orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa não encontrada"));
        if (pessoa.getStatus() != Status.ATIVO) throw new IllegalArgumentException("Pessoa deve estar ativa");
        if (repository.existsByPessoaId(pessoaId)) throw new RecursoDuplicadoException("Pessoa já possui cadastro de cliente");
        return repository.save(new Cliente(pessoa, telefone));
    }
    @Transactional(readOnly = true) public Cliente buscarPorId(Long id) { return repository.findOneById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado")); }
    @Transactional(readOnly = true) public Page<Cliente> pesquisar(String nome, int pagina, int tamanho, String ordenarPor, String direcao) {
        Pageable p = PessoaService.pageable(pagina, tamanho, ordenarPor, direcao, java.util.List.of("id", "pessoa.nome", "pessoa.email"));
        Specification<Cliente> spec = (root, query, cb) -> cb.conjunction();
        if (nome != null && !nome.isBlank()) spec = spec.and((r,q,cb) -> cb.like(cb.lower(r.get("pessoa").get("nome")), "%" + nome.trim().toLowerCase(java.util.Locale.ROOT) + "%"));
        return repository.findAll(spec, p);
    }
    @Transactional public Cliente alterar(Long id, String telefone) { Cliente cliente = buscarPorId(id); cliente.alterarDados(telefone); return cliente; }
}
