package com.curso.jogo2026.domain;


import com.curso.jogo2026.domain.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import static com.curso.jogo2026.domain.GeneroJogo.validarTextoObrigatorio;

@Entity
@Table(
        name = "desenvolvedora",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_desenvolvedora_cnpj",
                columnNames = "cnpj"
        )
)
public class Desenvolvedora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_fantasia", nullable = false, length = 150)
    private String nomeFantasia;

    @Column(nullable = false, length = 14)
    private String cnpj;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    protected Desenvolvedora() {
    }

    public Desenvolvedora(String nomeFantasia, String cnpj) {
        this.nomeFantasia = validarTextoObrigatorio(nomeFantasia, "Nome fantasia é obrigatório.");
        this.cnpj = validarCnpj(cnpj);
        this.status = Status.ATIVO;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public Status getStatus() {
        return status;
    }
    private String validarCnpj(String cnpj) {
        String cnpjLimpo = validarTextoObrigatorio(cnpj, "CNPJ é obrigatório").replaceAll("\\D", "");
        if (cnpjLimpo.length() != 14) {
            throw new IllegalArgumentException("O CNPJ deve conter exatamente 14 dígitos.");
        }
        return cnpjLimpo;
    }
}