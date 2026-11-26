package com.curso.jogo2026.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class PersistenciaJpaTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Transactional
    void devePersistirERelerGeneroEJogo() {
        GeneroJogo genero = new GeneroJogo("RPG");

        Jogo jogo = new Jogo(
                "7891000000019",
                "Elden Ring",
                new BigDecimal("10.000"),
                new BigDecimal("89.90"),
                LocalDate.of(2026, 3, 10)
        );

        genero.adicionarJogo(jogo);

        entityManager.persist(genero);
        entityManager.persist(jogo);
        entityManager.flush();

        Long jogoId = jogo.getId();

        entityManager.clear();

        Jogo recuperado = entityManager.find(Jogo.class, jogoId);

        assertEquals("RPG", recuperado.getGenero().getNome());
        assertEquals("Elden Ring", recuperado.getTitulo());
        assertEquals(
                0,
                recuperado.getEstoqueMinimo().compareTo(BigDecimal.ZERO)
        );
    }

    @Test
    @Transactional
    void deveRegistrarDezenoveChangeSets() {
        Number total = (Number) entityManager
                .createNativeQuery("select count(*) from databasechangelog")
                .getSingleResult();

        assertEquals(19L, total.longValue());
    }

    @Test
    @Transactional
    void deveRejeitarCodigoDeBarrasDuplicado() {
        GeneroJogo genero = new GeneroJogo("Ação");

        Jogo jogo1 = novoJogo("7891000000033");
        Jogo jogo2 = novoJogo("7891000000033");

        genero.adicionarJogo(jogo1);

        entityManager.persist(genero);
        entityManager.persist(jogo1);
        entityManager.flush();

        Long generoId = genero.getId();

        entityManager.clear();

        GeneroJogo generoSalvo = entityManager.find(
                GeneroJogo.class,
                generoId
        );

        jogo2.associarAo(generoSalvo);

        assertThrows(
                ConstraintViolationException.class,
                () -> {
                    entityManager.persist(jogo2);
                    entityManager.flush();
                }
        );
    }

    @Test
    @Transactional
    void deveRejeitarSaldoNegativo() {
        GeneroJogo genero = new GeneroJogo("Estratégia");

        entityManager.persist(genero);
        entityManager.flush();

        assertThrows(
                PersistenceException.class,
                () -> entityManager.createNativeQuery("""
                        insert into jogo (
                            codigo_barras,
                            titulo,
                            saldo_estoque,
                            valor_unitario,
                            data_lancamento,
                            status,
                            genero_jogo_id,
                            estoque_minimo
                        ) values (
                            '7891000000033',
                            'Elden Ring',
                            -1.000,
                            129.90,
                            '2026-06-01',
                            'ATIVO',
                            :generoId,
                            0
                        )
                        """)
                        .setParameter("generoId", genero.getId())
                        .executeUpdate()
        );
    }

    @Test
    @Transactional
    void bancoImpedeNomeDeGeneroDuplicadoIgnorandoCaixaEEspacos()
            throws Exception {
        String sufixo = UUID.randomUUID().toString();
        String nome = "Papelaria " + sufixo;
        jdbcTemplate.update(
                "INSERT INTO genero_jogo (nome, status) VALUES (?, 'ATIVO')",
                nome);

        assertThrows(DataIntegrityViolationException.class, () ->
                jdbcTemplate.update(
                        "INSERT INTO genero_jogo (nome, status) VALUES (?, 'ATIVO')",
                        "  " + nome.toUpperCase() + "  "));
    }

    private Jogo novoJogo(String codigoBarras) {
        return new Jogo(
                codigoBarras,
                "God of War Ragnarök",
                new BigDecimal("3.000"),
                new BigDecimal("146.99"),
                LocalDate.of(2022, 11, 9)
        );
    }
}
