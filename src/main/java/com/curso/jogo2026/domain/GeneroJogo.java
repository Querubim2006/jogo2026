package com.curso.jogo2026.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "genero_jogo")
public class GeneroJogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @OneToMany(mappedBy = "genero", fetch = FetchType.LAZY)
    private List<Jogo> jogos = new ArrayList<>();

    protected GeneroJogo() {
    }

    public GeneroJogo(String nome) {
        this.nome = validarTextoObrigatorio(
                nome,
                "Nome do gênero é obrigatório"
        );
        this.status = Status.ATIVO;
    }

    public Long getId() {
        return id;
    }

    public void adicionarJogo(Jogo jogo) {
        Objects.requireNonNull(jogo, "Jogo é obrigatório");

        // Valida se já existe algum jogo na lista com o mesmo código de barras (comparando Strings)
        boolean codigoJaUtilizado = jogos.stream()
                .map(Jogo::getCodigoBarras) // Obtém o código de barras de cada item
                .filter(Objects::nonNull)
                .anyMatch(codigo -> codigo.equalsIgnoreCase(jogo.getCodigoBarras()));

        if (codigoJaUtilizado) {
            throw new IllegalArgumentException("Código de barras já utilizado no gênero");
        }

        jogo.associarAo(this);

        if (!jogos.contains(jogo)) {
            jogos.add(jogo);
        }
    }

    public void ativar() {
        this.status = Status.ATIVO;
    }

    public void inativar() {
        this.status = Status.INATIVO;
    }

    public String getNome() {
        return nome;
    }

    public Status getStatus() {
        return status;
    }

    public List<Jogo> getJogos() {
        return List.copyOf(jogos);
    }

    private static String validarTextoObrigatorio(
            String texto,
            String mensagem
    ) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }

        return texto.trim();
    }
}