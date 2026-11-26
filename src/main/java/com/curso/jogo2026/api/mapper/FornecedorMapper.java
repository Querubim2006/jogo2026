package com.curso.jogo2026.api.mapper;

import com.curso.jogo2026.api.dto.FornecedorResponse;
import com.curso.jogo2026.domain.Fornecedor;
import org.springframework.stereotype.Component;

@Component public class FornecedorMapper {
    public FornecedorResponse toResponse(Fornecedor f) { return new FornecedorResponse(f.getId(), f.getRazaoSocial(), f.getCnpj(), f.getStatus()); }
}
