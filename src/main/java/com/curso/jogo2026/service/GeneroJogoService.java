package com.curso.jogo2026.service;

import com.curso.jogo2026.domain.GeneroJogo;
import com.curso.jogo2026.exception.RecursoDuplicadoException;
import com.curso.jogo2026.exception.RecursoNaoEncontradoException;
import com.curso.jogo2026.repository.GeneroJogoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new RecursoDuplicadoException(
                    "Nome do gênero já cadastrado"
            );
        }

        return repository.save(new GeneroJogo(nome));
    }

    @Transactional(readOnly = true)
    public GeneroJogo buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Gênero não encontrado"
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<GeneroJogo> listar() {
        return repository.findAll();
    }

    @Transactional
    public GeneroJogo alterar(Long id, String nome) {
        GeneroJogo genero = repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Gênero não encontrado"
                        )
                );

        if (!genero.getNome().equalsIgnoreCase(nome)
                && repository.existsByNomeIgnoreCase(nome)) {
            throw new RecursoDuplicadoException(
                    "Nome do gênero já cadastrado"
            );
        }

        genero.alterarNome(nome);

        return genero;
    }

    @Transactional
    public void excluir(Long id) {
        GeneroJogo genero = repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Gênero não encontrado"
                        )
                );

        if (!genero.getJogos().isEmpty()) {
            throw new RecursoDuplicadoException(
                    "Não é possível excluir o gênero porque existem jogos associados"
            );
        }

        repository.delete(genero);
    }
}