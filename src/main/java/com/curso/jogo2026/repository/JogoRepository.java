package com.curso.jogo2026.repository;

import com.curso.jogo2026.domain.Jogo;
import com.curso.jogo2026.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JogoRepository extends JpaRepository<Jogo, Long> {
        Optional<Jogo> findByCodigoBarras(String codigoBarras);

    static boolean existsByCodigoBarras(String codigoBarras) {
        return false;
    }

    List<Jogo> findByGeneroId(Long generoId);
        List<Jogo> findByStatus(Status status);
}
