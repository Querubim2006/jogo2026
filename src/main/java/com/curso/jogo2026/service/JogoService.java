package com.curso.jogo2026.service;

import com.curso.jogo2026.domain.Desenvolvedora;
import com.curso.jogo2026.domain.GeneroJogo;
import com.curso.jogo2026.domain.Jogo;
import com.curso.jogo2026.exception.RecursoDuplicadoException;
import com.curso.jogo2026.exception.RecursoNaoEncontradoException;
import com.curso.jogo2026.repository.DesenvolvedoraRepository;
import com.curso.jogo2026.repository.GeneroJogoRepository;
import com.curso.jogo2026.repository.JogoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JogoService {

    private final JogoRepository jogoRepository;
    private final GeneroJogoRepository generoRepository;
    private final DesenvolvedoraRepository desenvolvedoraRepository;

    public JogoService(
            JogoRepository jogoRepository,
            GeneroJogoRepository generoRepository,
            DesenvolvedoraRepository desenvolvedoraRepository
    ) {
        this.jogoRepository = jogoRepository;
        this.generoRepository = generoRepository;
        this.desenvolvedoraRepository = desenvolvedoraRepository;
    }

    @Transactional
    public Jogo cadastrar(
            Jogo jogo,
            Long generoId,
            Long desenvolvedoraId
    ) {
        if (jogoRepository.existsByCodigoBarras(jogo.getCodigoBarras())) {
            throw new RecursoDuplicadoException(
                    "Código de barras já cadastrado"
            );
        }

        GeneroJogo genero = generoRepository.findById(generoId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Gênero não encontrado"
                        )
                );

        genero.adicionarJogo(jogo);

        if (desenvolvedoraId != null) {
            Desenvolvedora desenvolvedora =
                    desenvolvedoraRepository.findById(desenvolvedoraId)
                            .orElseThrow(() ->
                                    new RecursoNaoEncontradoException(
                                            "Desenvolvedora não encontrada"
                                    )
                            );

            jogo.associarDesenvolvedora(desenvolvedora);
        }

        return jogoRepository.save(jogo);
    }

    @Transactional(readOnly = true)
    public Jogo buscarPorId(Long id) {
        return jogoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Jogo não encontrado com o ID: " + id
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<Jogo> listarTodos() {
        return jogoRepository.findAll();
    }

    @Transactional
    public Jogo alterar(
            Long id,
            Jogo jogo,
            Long generoId,
            Long desenvolvedoraId
    ) {
        Jogo jogoExistente = jogoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Jogo não encontrado com o ID: " + id
                        )
                );

        if (!jogoExistente.getCodigoBarras().equals(jogo.getCodigoBarras())
                && jogoRepository.existsByCodigoBarras(jogo.getCodigoBarras())) {
            throw new RecursoDuplicadoException(
                    "Código de barras já cadastrado"
            );
        }

        GeneroJogo genero = generoRepository.findById(generoId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Gênero não encontrado"
                        )
                );

        jogoExistente.alterarDados(
                jogo.getCodigoBarras(),
                jogo.getTitulo(),
                jogo.getSaldoEstoque(),
                jogo.getValorUnitario(),
                jogo.getDataLancamento(),
                jogo.getEstoqueMinimo()
        );

        jogoExistente.associarAo(genero);

        if (desenvolvedoraId != null) {
            Desenvolvedora desenvolvedora =
                    desenvolvedoraRepository.findById(desenvolvedoraId)
                            .orElseThrow(() ->
                                    new RecursoNaoEncontradoException(
                                            "Desenvolvedora não encontrada"
                                    )
                            );

            jogoExistente.associarDesenvolvedora(desenvolvedora);
        }

        return jogoExistente;
    }

    @Transactional
    public void excluir(Long id) {
        Jogo jogo = jogoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Jogo não encontrado com o ID: " + id
                        )
                );

        jogoRepository.delete(jogo);
    }
}