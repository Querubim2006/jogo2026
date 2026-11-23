package com.curso.jogo2026.service;

import com.curso.jogo2026.domain.GeneroJogo;
import com.curso.jogo2026.domain.Status;
import com.curso.jogo2026.exception.RecursoDuplicadoException;
import com.curso.jogo2026.exception.RecursoEmUsoException;
import com.curso.jogo2026.exception.RecursoNaoEncontradoException;
import com.curso.jogo2026.repository.GeneroJogoRepository;
import com.curso.jogo2026.repository.JogoRepository;
import com.curso.jogo2026.specification.GeneroJogoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class GeneroJogoService {

    private final GeneroJogoRepository repository;
    private final JogoRepository jogoRepository;

    public GeneroJogoService(
            GeneroJogoRepository repository,
            JogoRepository jogoRepository
    ) {
        this.repository = repository;
        this.jogoRepository = jogoRepository;
    }

    @Transactional
    public GeneroJogo cadastrar(String nome) {
        String nomeNormalizado = normalizar(nome);
        if (repository.existsByNomeIgnoreCase(nomeNormalizado)) {
            throw new RecursoDuplicadoException(
                    "Nome do gênero já cadastrado"
            );
        }

        return repository.save(new GeneroJogo(nomeNormalizado));
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

    @Transactional(readOnly = true)
    public Page<GeneroJogo> pesquisar(
            String nome,
            Status status,
            int pagina,
            int tamanho,
            String ordenarPor,
            String direcao) {
        if (pagina < 0) {
            throw new IllegalArgumentException("A página não pode ser negativa");
        }
        if (tamanho < 1 || tamanho > 100) {
            throw new IllegalArgumentException(
                    "O tamanho da página deve estar entre 1 e 100");
        }

        Set<String> camposPermitidos = Set.of("id", "nome", "status");
        String campo = ordenarPor == null || ordenarPor.isBlank()
                ? "id" : ordenarPor;
        if (!camposPermitidos.contains(campo)) {
            throw new IllegalArgumentException("Campo de ordenação inválido: " + campo);
        }

        Sort.Direction sentido;
        try {
            sentido = Sort.Direction.fromString(
                    direcao == null || direcao.isBlank() ? "ASC" : direcao);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("A direção deve ser ASC ou DESC");
        }
        Sort sort = Sort.by(sentido, campo);
        if (!campo.equals("id")) {
            sort = sort.and(Sort.by(sentido, "id"));
        }

        Specification<GeneroJogo> filtros = GeneroJogoSpecification
                .nomeContem(nome)
                .and(GeneroJogoSpecification.statusIgual(status));
        Pageable pageable = PageRequest.of(pagina, tamanho, sort);
        return repository.findAll(filtros, pageable);
    }

    @Transactional
    public GeneroJogo alterar(Long id, String nome) {
        String nomeNormalizado = normalizar(nome);
        GeneroJogo genero = repository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Gênero não encontrado"
                        )
                );

        if (repository.existsByNomeIgnoreCaseAndIdNot(nomeNormalizado, id)) {
            throw new RecursoDuplicadoException(
                    "Nome do gênero já cadastrado"
            );
        }

        genero.alterarNome(nomeNormalizado);

        return genero;
    }

    @Transactional
    public GeneroJogo alterarStatus(Long id, Status status) {
        GeneroJogo genero = buscarPorId(id);
        genero.alterarStatus(status);
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

        if (jogoRepository.existsByGeneroId(id)) {
            throw new RecursoEmUsoException(
                    "Gênero não pode ser excluído porque possui jogos"
            );
        }

        repository.delete(genero);
        repository.flush();
    }

    private String normalizar(String nome) {
        if (nome == null || nome.isBlank()) {
            return nome;
        }
        return nome.trim();
    }
}
