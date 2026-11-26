package com.curso.jogo2026.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.curso.jogo2026.domain.Jogo;
import com.curso.jogo2026.domain.Status;
import com.curso.jogo2026.exception.RecursoDuplicadoException;
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

    public void alterarNome(String novoNome) {
        this.nome = validarTextoObrigatorio(
                novoNome,
                "Nome do gênero é obrigatório"
        );
    }

    public void adicionarJogo(Jogo jogo) {
        Objects.requireNonNull(jogo, "Jogo é obrigatório");

        validarInclusao(jogo);

        if (jogo.getGenero() != this) {
            jogo.associarAo(this);
        }

        adicionarInternamente(jogo);
    }

    void validarInclusao(Jogo jogo) {
        boolean codigoDuplicado = jogos.stream()
                .anyMatch(existente -> existente != jogo
                        && existente.getCodigoBarras()
                                .equals(jogo.getCodigoBarras()));
        if (codigoDuplicado) {
            throw new RecursoDuplicadoException(
                    "Código de barras já utilizado no gênero");
        }
    }

    void adicionarInternamente(Jogo jogo) {
        if (!jogos.contains(jogo)) {
            jogos.add(jogo);
        }
    }

    void removerInternamente(Jogo jogo) {
        jogos.remove(jogo);
    }

    public void ativar() {
        this.status = Status.ATIVO;
    }

    public void inativar() {
        this.status = Status.INATIVO;
    }

    public void alterarStatus(Status novoStatus) {
        Objects.requireNonNull(novoStatus, "Status é obrigatório");
        if (novoStatus == Status.ATIVO) {
            ativar();
        } else {
            inativar();
        }
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

    public static String validarTextoObrigatorio(
            String texto,
            String mensagem
    ) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }

        return texto.trim();
    }
}
