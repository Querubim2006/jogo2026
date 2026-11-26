package com.curso.jogo2026.service;

import com.curso.jogo2026.domain.Fornecedor;
import com.curso.jogo2026.exception.RecursoDuplicadoException;
import com.curso.jogo2026.exception.RecursoEmUsoException;
import com.curso.jogo2026.exception.RecursoNaoEncontradoException;
import com.curso.jogo2026.repository.FornecedorRepository;
import com.curso.jogo2026.repository.JogoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class FornecedorService {
    private final FornecedorRepository repository;
    private final JogoRepository jogoRepository;
    public FornecedorService(FornecedorRepository repository, JogoRepository jogoRepository) { this.repository = repository; this.jogoRepository = jogoRepository; }
    @Transactional public Fornecedor cadastrar(Fornecedor f) { if (repository.existsByCnpj(f.getCnpj())) throw new RecursoDuplicadoException("CNPJ já cadastrado"); return repository.save(f); }
    @Transactional(readOnly = true) public Fornecedor buscarPorId(Long id) { return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado")); }
    @Transactional(readOnly = true) public List<Fornecedor> listar() { return repository.findAll(); }
    @Transactional public Fornecedor alterar(Long id, String razaoSocial, String cnpj) { Fornecedor f = buscarPorId(id); if (repository.existsByCnpjAndIdNot(cnpj, id)) throw new RecursoDuplicadoException("CNPJ já cadastrado"); f.alterarDados(razaoSocial, cnpj); return f; }
    @Transactional public void excluir(Long id) { Fornecedor f = buscarPorId(id); if (jogoRepository.existsByFornecedorId(id)) throw new RecursoEmUsoException("Fornecedor está associado a jogos"); repository.delete(f); }
}
