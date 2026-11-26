package com.curso.jogo2026.repository;

import com.curso.jogo2026.domain.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    boolean existsByCnpj(String cnpj);
    boolean existsByCnpjAndIdNot(String cnpj, Long id);
}
