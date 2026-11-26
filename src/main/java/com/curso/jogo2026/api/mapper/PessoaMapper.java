package com.curso.jogo2026.api.mapper;

import com.curso.jogo2026.api.dto.PessoaResponse;
import com.curso.jogo2026.domain.Pessoa;
import org.springframework.stereotype.Component;

@Component public class PessoaMapper {
    public PessoaResponse toResponse(Pessoa p) { return new PessoaResponse(p.getId(), p.getNome(), p.getEmail(), p.getCpf(), p.getDataCadastro(), p.getStatus()); }
}
