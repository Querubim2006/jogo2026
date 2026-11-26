package com.curso.jogo2026.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Locale;

@Entity
@Table(name = "pessoa", uniqueConstraints = {
        @UniqueConstraint(name = "uk_pessoa_cpf", columnNames = "cpf"),
        @UniqueConstraint(name = "uk_pessoa_email", columnNames = "email")})
public class Pessoa {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150) private String nome;
    @Column(nullable = false, length = 180) private String email;
    @Column(nullable = false, length = 11) private String cpf;
    @Column(name = "data_cadastro", nullable = false) private LocalDate dataCadastro;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Status status;

    protected Pessoa() { }

    public Pessoa(String nome, String email, String cpf, LocalDate dataCadastro) {
        this.nome = texto(nome, "Nome é obrigatório");
        this.email = normalizarEmail(email);
        this.cpf = validarCpf(cpf);
        if (dataCadastro == null) throw new IllegalArgumentException("Data de cadastro é obrigatória");
        this.dataCadastro = dataCadastro;
        this.status = Status.ATIVO;
    }

    public void alterarDados(String nome, String email, String cpf) {
        this.nome = texto(nome, "Nome é obrigatório");
        this.email = normalizarEmail(email);
        this.cpf = validarCpf(cpf);
    }
    public void ativar() { status = Status.ATIVO; }
    public void inativar() { status = Status.INATIVO; }
    private static String validarCpf(String cpf) {
        String valor = texto(cpf, "CPF é obrigatório");
        if (!DocumentoFiscal.cpfValido(valor)) throw new IllegalArgumentException("CPF inválido");
        return valor;
    }
    private static String normalizarEmail(String email) {
        String valor = texto(email, "E-mail é obrigatório").toLowerCase(Locale.ROOT);
        if (!valor.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new IllegalArgumentException("E-mail inválido");
        return valor;
    }
    private static String texto(String texto, String mensagem) {
        if (texto == null || texto.isBlank()) throw new IllegalArgumentException(mensagem);
        return texto.trim();
    }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCpf() { return cpf; }
    public LocalDate getDataCadastro() { return dataCadastro; }
    public Status getStatus() { return status; }
}
