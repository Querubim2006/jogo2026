package com.curso.jogo2026.api.controller;

import com.curso.jogo2026.api.dto.GeneroJogoRequest;
import com.curso.jogo2026.api.dto.GeneroJogoResponse;
import com.curso.jogo2026.api.mapper.GeneroJogoMapper;
import com.curso.jogo2026.domain.GeneroJogo;
import com.curso.jogo2026.service.GeneroJogoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/generos")
public class GeneroJogoController {

    private final GeneroJogoService service;
    private final GeneroJogoMapper mapper;

    public GeneroJogoController(
            GeneroJogoService service,
            GeneroJogoMapper mapper
    ) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<GeneroJogoResponse> cadastrar(
            @Valid @RequestBody GeneroJogoRequest request
    ) {
        GeneroJogo genero = service.cadastrar(request.nome());

        URI location = URI.create("/api/generos/" + genero.getId());

        return ResponseEntity
                .created(location)
                .body(mapper.toResponse(genero));
    }

    @GetMapping("/{id}")
    public GeneroJogoResponse buscarPorId(
            @PathVariable Long id
    ) {
        return mapper.toResponse(
                service.buscarPorId(id)
        );
    }

    @GetMapping
    public List<GeneroJogoResponse> listar() {
        return service.listar()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @PutMapping("/{id}")
    public GeneroJogoResponse alterar(
            @PathVariable Long id,
            @Valid @RequestBody GeneroJogoRequest request
    ) {
        GeneroJogo genero = service.alterar(
                id,
                request.nome()
        );

        return mapper.toResponse(genero);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}