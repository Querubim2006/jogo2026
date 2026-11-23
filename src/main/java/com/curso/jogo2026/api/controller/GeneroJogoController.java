package com.curso.jogo2026.api.controller;

import com.curso.jogo2026.api.dto.GeneroJogoRequest;
import com.curso.jogo2026.api.dto.GeneroJogoResponse;
import com.curso.jogo2026.api.dto.StatusRequest;
import com.curso.jogo2026.api.mapper.GeneroJogoMapper;
import com.curso.jogo2026.domain.GeneroJogo;
import com.curso.jogo2026.domain.Status;
import com.curso.jogo2026.dto.PaginaResponse;
import com.curso.jogo2026.service.GeneroJogoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
    public PaginaResponse<GeneroJogoResponse> pesquisar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(defaultValue = "id") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direcao) {
        return PaginaResponse.de(
                service.pesquisar(nome, status, pagina, tamanho, ordenarPor, direcao),
                mapper::toResponse);
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

    @PutMapping("/{id}/status")
    public GeneroJogoResponse alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {
        return mapper.toResponse(service.alterarStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
