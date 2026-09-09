package com.curso.jogo2026.service;

import com.curso.jogo2026.domain.GeneroJogo;
import com.curso.jogo2026.domain.Jogo;
import com.curso.jogo2026.exception.RecursoDuplicadoException;
import com.curso.jogo2026.exception.RecursoNaoEncontradoException;
import com.curso.jogo2026.repository.GeneroJogoRepository;
import com.curso.jogo2026.repository.JogoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class JogoService {

    private final JogoRepository jogoRepository;
    private final GeneroJogoRepository generoRepository;

    public JogoService(JogoRepository jogoRepository, GeneroJogoRepository generoRepository) {
        this.jogoRepository = jogoRepository;
        this.generoRepository = generoRepository;
    }

    @Transactional
    public Jogo cadastrar(Jogo jogo, Long generoId) {
        if (JogoRepository.existsByCodigoBarras(jogo.getCodigoBarras())) {
            throw new RecursoDuplicadoException("Código de barras já cadastrado");
        }
        GeneroJogo genero = generoRepository.findById(generoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Grupo de produto não encontrado"));

        genero.adicionarJogo(jogo);
        return jogoRepository.save(jogo);
    }
}
