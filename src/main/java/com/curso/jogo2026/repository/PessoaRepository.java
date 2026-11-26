package com.curso.jogo2026.repository;

import com.curso.jogo2026.domain.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PessoaRepository extends JpaRepository<Pessoa, Long>, JpaSpecificationExecutor<Pessoa> {
    boolean existsByCpf(String cpf);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByCpfAndIdNot(String cpf, Long id);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
