package com.curso.jogo2026.repository;

import com.curso.jogo2026.domain.Jogo;
import com.curso.jogo2026.domain.Status;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JogoRepository extends JpaRepository<Jogo, Long> {

    boolean existsByCodigoBarras(String codigoBarras);

    @EntityGraph(attributePaths = {"genero", "desenvolvedora"})
    Optional<Jogo> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"genero", "desenvolvedora"})
    List<Jogo> findAll();

    List<Jogo> findByGeneroId(Long generoId);

    List<Jogo> findByStatus(Status status);
}