
package com.curso.jogo2026.specification;

import com.curso.jogo2026.domain.Jogo;
import com.curso.jogo2026.domain.Status;

import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public class JogoSpecification {

    public static Specification<Jogo> tituloContem(String titulo) {
        return (root, query, cb) -> {
            if (titulo == null || titulo.isBlank()) {
                return cb.conjunction();
            }

            return cb.like(
                    cb.lower(root.get("titulo")),
                    "%" + titulo.trim().toLowerCase(Locale.ROOT) + "%"
            );
        };
    }

    public static Specification<Jogo> statusIgual(Status status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Jogo> porGenero(Long generoId) {
        return (root, query, cb) -> {
            if (generoId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("genero").get("id"),
                    generoId
            );
        };
    }

    public static Specification<Jogo> porDesenvolvedora(
            Long desenvolvedoraId) {
        return (root, query, cb) -> {
            if (desenvolvedoraId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("desenvolvedora").get("id"),
                    desenvolvedoraId
            );
        };
    }

    public static Specification<Jogo> abaixoEstoqueMinimo(
            Boolean abaixo) {
        return (root, query, cb) -> {
            if (!Boolean.TRUE.equals(abaixo)) {
                return cb.conjunction();
            }

            return cb.lessThan(
                    root.get("saldoEstoque"),
                    root.get("estoqueMinimo")
            );
        };
    }
}
