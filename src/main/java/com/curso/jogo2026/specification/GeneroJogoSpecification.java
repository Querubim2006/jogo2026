package com.curso.jogo2026.specification;

import com.curso.jogo2026.domain.GeneroJogo;
import com.curso.jogo2026.domain.Status;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class GeneroJogoSpecification {

    private GeneroJogoSpecification() {
    }

    public static Specification<GeneroJogo> nomeContem(String nome) {
        return (root, query, cb) -> {
            if (nome == null || nome.isBlank()) {
                return cb.conjunction();
            }
            String trecho = "%" + nome.trim().toLowerCase(Locale.ROOT) + "%";
            return cb.like(cb.lower(root.get("nome")), trecho);
        };
    }

    public static Specification<GeneroJogo> statusIgual(Status status) {
        return (root, query, cb) -> status == null
                ? cb.conjunction()
                : cb.equal(root.get("status"), status);
    }
}
