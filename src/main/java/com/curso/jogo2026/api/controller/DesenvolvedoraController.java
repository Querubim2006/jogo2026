package com.curso.jogo2026.api.controller;

import com.curso.jogo2026.api.dto.DesenvolvedoraRequest;
import com.curso.jogo2026.api.dto.DesenvolvedoraResponse;
import com.curso.jogo2026.api.mapper.DesenvolvedoraMapper;
import com.curso.jogo2026.domain.Desenvolvedora;
import com.curso.jogo2026.service.DesenvolvedoraService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/desenvolvedoras")
public class DesenvolvedoraController {

    private final DesenvolvedoraService service;
    private final DesenvolvedoraMapper mapper;

    public DesenvolvedoraController(
            DesenvolvedoraService service,
            DesenvolvedoraMapper mapper
    ) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<DesenvolvedoraResponse> cadastrar(
            @Valid @RequestBody DesenvolvedoraRequest request
    ) {
        Desenvolvedora desenvolvedora = mapper.toEntity(request);

        Desenvolvedora cadastrada = service.salvar(desenvolvedora);

        URI location = URI.create(
                "/api/desenvolvedoras/" + cadastrada.getId()
        );

        return ResponseEntity
                .created(location)
                .body(mapper.toResponse(cadastrada));
    }

    @GetMapping("/{id}")
    public DesenvolvedoraResponse buscarPorId(
            @PathVariable Long id
    ) {
        return mapper.toResponse(
                service.buscarPorId(id)
        );
    }

    @GetMapping
    public List<DesenvolvedoraResponse> listar() {
        return service.listarTodas()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @PutMapping("/{id}")
    public DesenvolvedoraResponse alterar(
            @PathVariable Long id,
            @Valid @RequestBody DesenvolvedoraRequest request
    ) {
        Desenvolvedora alterada = service.alterar(
                id,
                request.nomeFantasia(),
                request.cnpj()
        );

        return mapper.toResponse(alterada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}