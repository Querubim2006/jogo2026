package com.curso.jogo2026.api.mapper;

import com.curso.jogo2026.api.dto.DesenvolvedoraRequest;
import com.curso.jogo2026.api.dto.DesenvolvedoraResponse;
import com.curso.jogo2026.domain.Desenvolvedora;
import org.springframework.stereotype.Component;

@Component
public class DesenvolvedoraMapper {

    public Desenvolvedora toEntity(DesenvolvedoraRequest request) {
        return new Desenvolvedora(
                request.nomeFantasia(),
                request.cnpj()
        );
    }

    public DesenvolvedoraResponse toResponse(Desenvolvedora desenvolvedora) {
        return new DesenvolvedoraResponse(
                desenvolvedora.getId(),
                desenvolvedora.getNomeFantasia(),
                desenvolvedora.getCnpj(),
                desenvolvedora.getStatus()
        );
    }
}