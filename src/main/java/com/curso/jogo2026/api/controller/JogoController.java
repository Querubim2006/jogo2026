package com.curso.jogo2026.api.controller;

import com.curso.jogo2026.api.dto.JogoRequest;
import com.curso.jogo2026.api.dto.JogoResponse;
import com.curso.jogo2026.api.mapper.JogoMapper;
import com.curso.jogo2026.domain.Jogo;
import com.curso.jogo2026.service.JogoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/jogos")
public class JogoController {

    private final JogoService service;
    private final JogoMapper mapper;

    public JogoController(
            JogoService service,
            JogoMapper mapper
    ) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<JogoResponse> cadastrar(
            @Valid @RequestBody JogoRequest request
    ) {
        Jogo jogo = mapper.toEntity(request);

        Jogo cadastrado = service.cadastrar(
                jogo,
                request.generoId(),
                request.desenvolvedoraId()
        );

        URI location = URI.create(
                "/api/jogos/" + cadastrado.getId()
        );

        return ResponseEntity
                .created(location)
                .body(mapper.toResponse(cadastrado));
    }

    @GetMapping("/{id}")
    public JogoResponse buscarPorId(
            @PathVariable Long id
    ) {
        return mapper.toResponse(
                service.buscarPorId(id)
        );
    }

    @GetMapping
    public List<JogoResponse> listar() {
        return service.listarTodos()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @PutMapping("/{id}")
    public JogoResponse alterar(
            @PathVariable Long id,
            @Valid @RequestBody JogoRequest request
    ) {
        Jogo jogo = mapper.toEntity(request);

        Jogo alterado = service.alterar(
                id,
                jogo,
                request.generoId(),
                request.desenvolvedoraId()
        );

        return mapper.toResponse(alterado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}