package com.curso.jogo2026.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

public class Jogo {
    private final String codigoBarras;
    private String descricao;
    private BigDecimal saldoEstoque;
    private BigDecimal valorUnitario;
    private final LocalDate dataCadastro;
    private Status status;
    private GeneroJogo genero;

    public Jogo(String codigoBarras, String descricao, BigDecimal saldoEstoque, BigDecimal valorUnitario, LocalDate dataCadastro) {
        this.codigoBarras = validarTextoObrigatorio(codigoBarras,"Código de barras é obrigatório");
        this.descricao = validarTextoObrigatorio(descricao,"Descrição é obrigatório");
        this.saldoEstoque = validarNaoNegativo(saldoEstoque,"Saldo de estoque não pode ser negativo");
        this.valorUnitario = validarNaoNegativo(valorUnitario,"Valor unitário não pode ser negativo");
        this.dataCadastro = Objects.requireNonNull(dataCadastro,"Data de cadastro é obrigatório");
        this.status = Status.ATIVO;
    }

    public BigDecimal calcularValorEstoque(){
        return saldoEstoque
                .multiply(valorUnitario)
                .setScale(2,RoundingMode.HALF_UP);
    }

    public void receberEstoque(BigDecimal quantidade){
        validarPositivo(quantidade,"Quantidade recebida deve ser maior que zero");
        this.saldoEstoque = saldoEstoque.add(quantidade);
    }

    public void retirarEstoque(BigDecimal quantidade){
        validarPositivo(quantidade,"Quantidade retirada deve ser maior que zero");
        if(saldoEstoque.compareTo(quantidade)<0){
            throw new IllegalArgumentException("Saldo de estoque insuficiente");
        }

        this.saldoEstoque = saldoEstoque.subtract(quantidade);
    }

    public void alterarDescricao(String novaDescricao){
        this.descricao = validarTextoObrigatorio(novaDescricao,"Descrição é obrigatório");
    }

    public void alterarValorUnitario(BigDecimal novoValor){
        this.valorUnitario = validarNaoNegativo(novoValor,"Valor unitário não deve ser negativo");
    }

    public void ativar(){
        this.status = Status.ATIVO;
    }

    public void desativar(){
        this.status = Status.INATIVO;
    }

    void associarAo(GeneroJogo genero){
        Objects.requireNonNull(genero,"Gênero do jogo é obrigatório");

        if(this.genero != null && this.genero != genero){
            throw new IllegalStateException("O jogo indicado já pertence a outro gênero");
        }

        this.genero = genero;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getSaldoEstoque() {
        return saldoEstoque;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public Status getStatus() {
        return status;
    }

    public GeneroJogo getGenero() {
        return genero;
    }

    private static String validarTextoObrigatorio(String texto,String mensagem){
        if (texto == null || texto.isBlank()){
            throw new IllegalArgumentException(mensagem);
        }
        return texto.trim();
    }

    private static BigDecimal validarNaoNegativo (BigDecimal valor, String mensagem){
        Objects.requireNonNull(valor, mensagem);
        if(valor.signum()<=0){
            throw new IllegalArgumentException(mensagem);
        }
        return valor;
    }

    private static void validarPositivo(BigDecimal valor,String mensagem){
        Objects.requireNonNull(valor,mensagem);
        if (valor.signum()<=0) {
            throw new IllegalArgumentException(mensagem);
        }
    }
}
