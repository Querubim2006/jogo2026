package com.curso.jogo2026.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "jogo",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_jogo_codigo_barras",
                columnNames = "codigo_barras"
        )
)
public class Jogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_barras", nullable = false, length = 50)
    private String codigoBarras;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "saldo_estoque", nullable = false, precision = 18, scale = 3)
    private BigDecimal saldoEstoque;

    @Column(name = "valor_unitario", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "data_lancamento", nullable = false)
    private LocalDate dataLancamento;

    @Column(name = "estoque_minimo", nullable = false, precision = 18, scale = 3)
    private BigDecimal estoqueMinimo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "genero_jogo_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_jogo_genero_jogo")
    )
    private GeneroJogo genero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "desenvolvedora_id",
            foreignKey = @ForeignKey(name = "fk_jogo_desenvolvedora")
    )
    private Desenvolvedora desenvolvedora;

    protected Jogo() {
    }

    public Jogo(
            String codigoBarras,
            String titulo,
            BigDecimal saldoEstoque,
            BigDecimal valorUnitario,
            LocalDate dataLancamento,
            BigDecimal estoqueMinimo
    ) {
        this.codigoBarras = validarTextoObrigatorio(
                codigoBarras,
                "Código de barras é obrigatório"
        );

        this.titulo = validarTextoObrigatorio(
                titulo,
                "Título do jogo é obrigatório"
        );

        this.saldoEstoque = validarNaoNegativo(
                saldoEstoque,
                "Saldo de estoque não pode ser negativo"
        );

        this.valorUnitario = validarNaoNegativo(
                valorUnitario,
                "Valor unitário não pode ser negativo"
        );

        this.dataLancamento = Objects.requireNonNull(
                dataLancamento,
                "Data de lançamento é obrigatória"
        );

        this.estoqueMinimo = validarNaoNegativo(
                estoqueMinimo,
                "Estoque mínimo não pode ser negativo"
        );

        this.status = Status.ATIVO;
    }

    public Jogo(
            String codigoBarras,
            String titulo,
            BigDecimal saldoEstoque,
            BigDecimal valorUnitario,
            LocalDate dataLancamento
    ) {
        this(
                codigoBarras,
                titulo,
                saldoEstoque,
                valorUnitario,
                dataLancamento,
                BigDecimal.ZERO
        );
    }

    public Long getId() {
        return id;
    }

    public BigDecimal calcularValorEstoque() {
        return saldoEstoque
                .multiply(valorUnitario)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void receberEstoque(BigDecimal quantidade) {
        validarPositivo(
                quantidade,
                "Quantidade recebida deve ser maior que zero"
        );

        this.saldoEstoque = saldoEstoque.add(quantidade);
    }

    public void retirarEstoque(BigDecimal quantidade) {
        validarPositivo(
                quantidade,
                "Quantidade retirada deve ser maior que zero"
        );

        if (saldoEstoque.compareTo(quantidade) < 0) {
            throw new IllegalArgumentException(
                    "Saldo de estoque insuficiente"
            );
        }

        this.saldoEstoque = saldoEstoque.subtract(quantidade);
    }

    public void alterarTitulo(String novoTitulo) {
        this.titulo = validarTextoObrigatorio(
                novoTitulo,
                "Título do jogo é obrigatório"
        );
    }

    public void alterarValorUnitario(BigDecimal novoValor) {
        this.valorUnitario = validarNaoNegativo(
                novoValor,
                "Valor unitário não pode ser negativo"
        );
    }

    public void alterarDados(
            String novoCodigoBarras,
            String novoTitulo,
            BigDecimal novoSaldoEstoque,
            BigDecimal novoValorUnitario,
            LocalDate novaDataLancamento,
            BigDecimal novoEstoqueMinimo
    ) {
        this.codigoBarras = validarTextoObrigatorio(
                novoCodigoBarras,
                "Código de barras é obrigatório"
        );

        alterarTitulo(novoTitulo);

        this.saldoEstoque = validarNaoNegativo(
                novoSaldoEstoque,
                "Saldo de estoque não pode ser negativo"
        );

        alterarValorUnitario(novoValorUnitario);

        this.dataLancamento = Objects.requireNonNull(
                novaDataLancamento,
                "Data de lançamento é obrigatória"
        );

        this.estoqueMinimo = validarNaoNegativo(
                novoEstoqueMinimo,
                "Estoque mínimo não pode ser negativo"
        );
    }

    public void ativar() {
        this.status = Status.ATIVO;
    }

    public void desativar() {
        this.status = Status.INATIVO;
    }

    public void associarAo(GeneroJogo genero) {
        Objects.requireNonNull(
                genero,
                "Gênero do jogo é obrigatório"
        );

        if (this.genero != null && this.genero != genero) {
            throw new IllegalStateException(
                    "O jogo indicado já pertence a outro gênero"
            );
        }

        this.genero = genero;
    }

    public void associarDesenvolvedora(Desenvolvedora desenvolvedora) {
        this.desenvolvedora = desenvolvedora;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public String getTitulo() {
        return titulo;
    }

    public BigDecimal getSaldoEstoque() {
        return saldoEstoque;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public BigDecimal getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public Status getStatus() {
        return status;
    }

    public GeneroJogo getGenero() {
        return genero;
    }

    public Desenvolvedora getDesenvolvedora() {
        return desenvolvedora;
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

    private static BigDecimal validarNaoNegativo(
            BigDecimal valor,
            String mensagem
    ) {
        Objects.requireNonNull(valor, mensagem);

        if (valor.signum() < 0) {
            throw new IllegalArgumentException(mensagem);
        }

        return valor;
    }

    private static void validarPositivo(
            BigDecimal valor,
            String mensagem
    ) {
        Objects.requireNonNull(valor, mensagem);

        if (valor.signum() <= 0) {
            throw new IllegalArgumentException(mensagem);
        }
    }
}