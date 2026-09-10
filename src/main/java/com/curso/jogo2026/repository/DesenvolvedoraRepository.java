package com.curso.jogo2026.repository;

import com.curso.jogo2026.domain.Desenvolvedora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesenvolvedoraRepository extends JpaRepository<Desenvolvedora, Long> {

    boolean existsByCnpj(String cnpj);

}
