package com.curso.jogo2026.repository;

import com.curso.jogo2026.domain.Jogo;
import com.curso.jogo2026.domain.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface JogoRepository
        extends JpaRepository<Jogo, Long>,
        JpaSpecificationExecutor<Jogo> {

    boolean existsByDesenvolvedoraId(Long desenvolvedoraId);

    boolean existsByCodigoBarras(String codigoBarras);

    boolean existsByGeneroId(Long generoId);

    @EntityGraph(attributePaths = {"genero", "desenvolvedora"})
    Optional<Jogo> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"genero", "desenvolvedora"})
    List<Jogo> findAll();

    @Override
    @EntityGraph(attributePaths = {"genero", "desenvolvedora"})
    Page<Jogo> findAll(
            Specification<Jogo> specification,
            Pageable pageable
    );

    List<Jogo> findByGeneroId(Long generoId);

    List<Jogo> findByStatus(Status status);
}