package com.curso.jogo2026.repository;

import com.curso.jogo2026.domain.GeneroJogo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GeneroJogoRepository extends JpaRepository<GeneroJogo, Long> {
    boolean existsByNomeIgnoreCase(String nome);
    Optional<GeneroJogo> findByNomeIgnoreCase(String nome);
}
