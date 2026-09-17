package com.curso.jogo2026.service;

import com.curso.jogo2026.domain.GeneroJogo;
import com.curso.jogo2026.domain.Jogo;
import com.curso.jogo2026.exception.RecursoNaoEncontradoException;
import com.curso.jogo2026.repository.GeneroJogoRepository;
import com.curso.jogo2026.repository.JogoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class JogoServiceTest {

    @Autowired
    private JogoService jogoService;

    @Autowired
    private GeneroJogoRepository generoRepository;

    @Autowired
    private JogoRepository jogoRepository;

    @Test
    void deveCadastrarJogoComGenero() {
        GeneroJogo genero = generoRepository.save(
                new GeneroJogo("Ação")
        );

        Jogo jogo = new Jogo(
                "7891000000099",
                "Jogo Teste",
                new BigDecimal("100.000"),
                new BigDecimal("150.00"),
                LocalDate.now()
        );

        Jogo cadastrado = jogoService.cadastrar(
                jogo,
                genero.getId(),
                null
        );

        assertNotNull(cadastrado.getId());
        assertEquals(
                genero.getId(),
                cadastrado.getGenero().getId()
        );
    }

    @Test
    void deveFazerRollbackAoTentarCadastrarComGeneroInexistente() {
        Jogo jogo = new Jogo(
                "7891000000088",
                "Jogo Rollback",
                new BigDecimal("100.000"),
                new BigDecimal("150.00"),
                LocalDate.now()
        );

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> jogoService.cadastrar(
                        jogo,
                        Long.MAX_VALUE,
                        null
                )
        );

        assertFalse(
                jogoRepository.existsByCodigoBarras(
                        jogo.getCodigoBarras()
                )
        );
    }
}