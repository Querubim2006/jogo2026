package com.curso.jogo2026.service;

import com.curso.jogo2026.domain.*;
import com.curso.jogo2026.exception.RecursoDuplicadoException;
import com.curso.jogo2026.exception.RecursoNaoEncontradoException;
import com.curso.jogo2026.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
public class ColaboradorService {
    private final ColaboradorRepository repository;
    private final PessoaRepository pessoaRepository;
    public ColaboradorService(ColaboradorRepository repository, PessoaRepository pessoaRepository) { this.repository = repository; this.pessoaRepository = pessoaRepository; }
    @Transactional public Colaborador cadastrar(Long pessoaId, String matricula, FuncaoColaborador funcao, LocalDate dataAdmissao) {
        Pessoa pessoa = pessoaRepository.findById(pessoaId).orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa não encontrada"));
        if (pessoa.getStatus() != Status.ATIVO) throw new IllegalArgumentException("Pessoa deve estar ativa");
        if (repository.existsByPessoaId(pessoaId)) throw new RecursoDuplicadoException("Pessoa já possui cadastro de colaborador");
        if (repository.existsByMatricula(matricula)) throw new RecursoDuplicadoException("Matrícula já cadastrada");
        return repository.save(new Colaborador(pessoa, matricula, funcao, dataAdmissao));
    }
    @Transactional(readOnly = true) public Colaborador buscarPorId(Long id) { return repository.findOneById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Colaborador não encontrado")); }
    @Transactional(readOnly = true) public Page<Colaborador> pesquisar(String nome, FuncaoColaborador funcao, int pagina, int tamanho, String ordenarPor, String direcao) {
        Pageable p = PessoaService.pageable(pagina, tamanho, ordenarPor, direcao, java.util.List.of("id", "matricula", "funcao", "dataAdmissao"));
        Specification<Colaborador> spec = (root, query, cb) -> cb.conjunction();
        if (nome != null && !nome.isBlank()) spec = spec.and((r,q,cb) -> cb.like(cb.lower(r.get("pessoa").get("nome")), "%" + nome.trim().toLowerCase(java.util.Locale.ROOT) + "%"));
        if (funcao != null) spec = spec.and((r,q,cb) -> cb.equal(r.get("funcao"), funcao));
        return repository.findAll(spec, p);
    }
    @Transactional public Colaborador alterar(Long id, String matricula, FuncaoColaborador funcao) {
        Colaborador c = buscarPorId(id);
        if (repository.existsByMatriculaAndIdNot(matricula, id)) throw new RecursoDuplicadoException("Matrícula já cadastrada");
        c.alterarDados(matricula, funcao); return c;
    }
}
