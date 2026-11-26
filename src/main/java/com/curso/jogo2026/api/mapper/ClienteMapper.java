package com.curso.jogo2026.api.mapper;

import com.curso.jogo2026.api.dto.ClienteResponse;
import com.curso.jogo2026.domain.Cliente;
import org.springframework.stereotype.Component;

@Component public class ClienteMapper {
    public ClienteResponse toResponse(Cliente c) { return new ClienteResponse(c.getId(), c.getPessoa().getId(), c.getPessoa().getNome(), c.getPessoa().getEmail(), c.getTelefone()); }
}
