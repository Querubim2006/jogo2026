package com.curso.jogo2026.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "fornecedor", uniqueConstraints = @UniqueConstraint(name = "uk_fornecedor_cnpj", columnNames = "cnpj"))
public class Fornecedor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "razao_social", nullable = false, length = 150) private String razaoSocial;
    @Column(nullable = false, length = 14) private String cnpj;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Status status;
    protected Fornecedor() { }
    public Fornecedor(String razaoSocial, String cnpj) {
        alterarDados(razaoSocial, cnpj);
        this.status = Status.ATIVO;
    }
    public void alterarDados(String razaoSocial, String cnpj) {
        if (razaoSocial == null || razaoSocial.isBlank()) throw new IllegalArgumentException("Razão social é obrigatória");
        if (!DocumentoFiscal.cnpjValido(cnpj)) throw new IllegalArgumentException("CNPJ inválido");
        this.razaoSocial = razaoSocial.trim();
        this.cnpj = cnpj;
    }
    public void ativar() { status = Status.ATIVO; }
    public void inativar() { status = Status.INATIVO; }
    public Long getId() { return id; }
    public String getRazaoSocial() { return razaoSocial; }
    public String getCnpj() { return cnpj; }
    public Status getStatus() { return status; }
}
