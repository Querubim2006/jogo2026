package com.curso.jogo2026.service;

import com.curso.jogo2026.domain.GeneroJogo;
import com.curso.jogo2026.exception.RecursoDuplicadoException;
import com.curso.jogo2026.exception.RecursoNaoEncontradoException;
import com.curso.jogo2026.repository.GeneroJogoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneroJogoService {
    private final GeneroJogoRepository repository;

    public GeneroJogoService(GeneroJogoRepository repository) {
        this.repository = repository;
    }
    @Transactional
    public GeneroJogo cadastrar(String nome) {
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new RecursoDuplicadoException("Nome do grupo já cadastrado");
        }
        return repository.save(new GeneroJogo(nome));
    }

    @Transactional(readOnly = true)
    public GeneroJogo buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Grupo de produto não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<GeneroJogo> listar() {
        return repository.findAll();
    }
}

