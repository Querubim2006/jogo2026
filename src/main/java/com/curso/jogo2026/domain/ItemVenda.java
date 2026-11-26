package com.curso.jogo2026.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Entity
@Table(name = "item_venda", uniqueConstraints = @UniqueConstraint(name = "uk_item_venda_produto", columnNames = {"venda_id", "produto_id"}))
public class ItemVenda {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venda_id", nullable = false, foreignKey = @ForeignKey(name = "fk_item_venda_venda"))
    private Venda venda;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false, foreignKey = @ForeignKey(name = "fk_item_venda_jogo"))
    private Jogo produto;
    @Column(nullable = false, precision = 18, scale = 3) private BigDecimal quantidade;
    @Column(name = "valor_unitario", nullable = false, precision = 18, scale = 2) private BigDecimal valorUnitario;
    protected ItemVenda() { }
    ItemVenda(Venda venda, Jogo produto, BigDecimal quantidade, BigDecimal valorUnitario) {
        this.venda = Objects.requireNonNull(venda, "Venda é obrigatória");
        this.produto = Objects.requireNonNull(produto, "Jogo é obrigatório");
        if (quantidade == null || quantidade.signum() <= 0) throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        if (quantidade.scale() > 3) throw new IllegalArgumentException("Quantidade aceita até três casas decimais");
        if (valorUnitario == null || valorUnitario.signum() < 0) throw new IllegalArgumentException("Preço unitário não pode ser negativo");
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }
    public BigDecimal calcularSubtotal() { return quantidade.multiply(valorUnitario).setScale(2, RoundingMode.HALF_UP); }
    public Long getId() { return id; }
    public Venda getVenda() { return venda; }
    public Jogo getProduto() { return produto; }
    public BigDecimal getQuantidade() { return quantidade; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
}
