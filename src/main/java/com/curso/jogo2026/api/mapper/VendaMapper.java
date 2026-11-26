package com.curso.jogo2026.api.mapper;

import com.curso.jogo2026.api.dto.*;
import com.curso.jogo2026.domain.*;
import org.springframework.stereotype.Component;
import java.util.List;

@Component public class VendaMapper {
    public VendaResponse toResponse(Venda v) {
        List<ItemVendaResponse> itens = v.getItens().stream().map(this::toItemResponse).toList();
        return new VendaResponse(v.getId(), v.getCliente().getId(), v.getCliente().getPessoa().getNome(),
                v.getVendedor().getId(), v.getVendedor().getPessoa().getNome(), v.getDataVenda(), v.getStatus(), itens, v.calcularTotal());
    }
    public VendaResumoResponse toResumo(Venda v) { return new VendaResumoResponse(v.getId(), v.getCliente().getId(), v.getCliente().getPessoa().getNome(), v.getVendedor().getId(), v.getVendedor().getPessoa().getNome(), v.getDataVenda(), v.getStatus()); }
    private ItemVendaResponse toItemResponse(ItemVenda i) { return new ItemVendaResponse(i.getId(), i.getProduto().getId(), i.getProduto().getTitulo(), i.getQuantidade(), i.getValorUnitario(), i.calcularSubtotal()); }
}
