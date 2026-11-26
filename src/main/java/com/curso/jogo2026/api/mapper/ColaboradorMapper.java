package com.curso.jogo2026.api.mapper;

import com.curso.jogo2026.api.dto.ColaboradorResponse;
import com.curso.jogo2026.domain.Colaborador;
import org.springframework.stereotype.Component;

@Component public class ColaboradorMapper {
    public ColaboradorResponse toResponse(Colaborador c) { return new ColaboradorResponse(c.getId(), c.getPessoa().getId(), c.getPessoa().getNome(), c.getPessoa().getEmail(), c.getMatricula(), c.getFuncao(), c.getDataAdmissao()); }
}
