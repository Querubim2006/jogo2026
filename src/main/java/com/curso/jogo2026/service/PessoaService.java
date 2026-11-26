package com.curso.jogo2026.service;

import com.curso.jogo2026.domain.Pessoa;
import com.curso.jogo2026.domain.Status;
import com.curso.jogo2026.exception.RecursoDuplicadoException;
import com.curso.jogo2026.exception.RecursoNaoEncontradoException;
import com.curso.jogo2026.repository.PessoaRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PessoaService {
    private final PessoaRepository repository;
    public PessoaService(PessoaRepository repository) { this.repository = repository; }

    @Transactional public Pessoa cadastrar(Pessoa pessoa) {
        validarUnicidade(pessoa.getCpf(), pessoa.getEmail(), null);
        return repository.save(pessoa);
    }
    @Transactional(readOnly = true) public Pessoa buscarPorId(Long id) { return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa não encontrada")); }
    @Transactional(readOnly = true) public Page<Pessoa> pesquisar(String nome, Status status, int pagina, int tamanho, String ordenarPor, String direcao) {
        Pageable pageable = pageable(pagina, tamanho, ordenarPor, direcao, java.util.List.of("id", "nome", "email", "cpf", "dataCadastro", "status"));
        Specification<Pessoa> spec = (root, query, cb) -> cb.conjunction();
        if (nome != null && !nome.isBlank()) spec = spec.and((r,q,cb) -> cb.like(cb.lower(r.get("nome")), "%" + nome.trim().toLowerCase(java.util.Locale.ROOT) + "%"));
        if (status != null) spec = spec.and((r,q,cb) -> cb.equal(r.get("status"), status));
        return repository.findAll(spec, pageable);
    }
    @Transactional public Pessoa alterar(Long id, String nome, String email, String cpf) {
        Pessoa pessoa = buscarPorId(id);
        validarUnicidade(cpf, email == null ? null : email.trim().toLowerCase(java.util.Locale.ROOT), id);
        pessoa.alterarDados(nome, email, cpf);
        return pessoa;
    }
    @Transactional public Pessoa alterarStatus(Long id, Status status) {
        Pessoa pessoa = buscarPorId(id);
        if (status == null) throw new IllegalArgumentException("Status é obrigatório");
        if (status == Status.ATIVO) pessoa.ativar(); else pessoa.inativar();
        return pessoa;
    }
    private void validarUnicidade(String cpf, String email, Long id) {
        boolean cpfExiste = id == null ? repository.existsByCpf(cpf) : repository.existsByCpfAndIdNot(cpf, id);
        boolean emailExiste = id == null ? repository.existsByEmailIgnoreCase(email) : repository.existsByEmailIgnoreCaseAndIdNot(email, id);
        if (cpfExiste) throw new RecursoDuplicadoException("CPF já cadastrado");
        if (emailExiste) throw new RecursoDuplicadoException("E-mail já cadastrado");
    }
    static Pageable pageable(int pagina, int tamanho, String ordenarPor, String direcao, java.util.List<String> permitidos) {
        if (pagina < 0) throw new IllegalArgumentException("A página não pode ser negativa");
        if (tamanho < 1 || tamanho > 100) throw new IllegalArgumentException("O tamanho da página deve estar entre 1 e 100");
        String campo = ordenarPor == null || ordenarPor.isBlank() ? "id" : ordenarPor;
        if (!permitidos.contains(campo)) throw new IllegalArgumentException("Campo de ordenação inválido: " + campo);
        Sort.Direction sentido;
        try { sentido = Sort.Direction.fromString(direcao == null || direcao.isBlank() ? "ASC" : direcao); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("A direção deve ser ASC ou DESC"); }
        Sort sort = Sort.by(sentido, campo);
        if (!campo.equals("id")) sort = sort.and(Sort.by(sentido, "id"));
        return PageRequest.of(pagina, tamanho, sort);
    }
}
