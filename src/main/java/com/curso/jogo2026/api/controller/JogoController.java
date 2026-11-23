
package com.curso.jogo2026.api.controller;

import com.curso.jogo2026.api.dto.JogoAtualizacaoRequest;
import com.curso.jogo2026.api.dto.JogoRequest;
import com.curso.jogo2026.api.dto.JogoResponse;
import com.curso.jogo2026.api.dto.MovimentacaoEstoqueRequest;
import com.curso.jogo2026.api.dto.StatusRequest;
import com.curso.jogo2026.api.mapper.JogoMapper;
import com.curso.jogo2026.domain.Jogo;
import com.curso.jogo2026.domain.Status;
import com.curso.jogo2026.dto.PaginaResponse;
import com.curso.jogo2026.service.JogoService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
    public PaginaResponse<JogoResponse> pesquisar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Long generoId,
            @RequestParam(required = false) Long desenvolvedoraId,
            @RequestParam(required = false) Boolean abaixoEstoqueMinimo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(defaultValue = "id") String ordenarPor,
            @RequestParam(defaultValue = "ASC") String direcao
    ) {
        org.springframework.data.domain.Page<Jogo> resultado = service.pesquisar(
                titulo,
                status,
                generoId,
                desenvolvedoraId,
                abaixoEstoqueMinimo,
                pagina,
                tamanho,
                ordenarPor,
                direcao
        );

        return PaginaResponse.de(resultado, mapper::toResponse);
    }

    @PutMapping("/{id}")
    public JogoResponse alterar(
            @PathVariable Long id,
            @Valid @RequestBody JogoAtualizacaoRequest request
    ) {
        Jogo alterado = service.alterar(
                id,
                request.titulo(),
                request.valorUnitario(),
                request.dataLancamento(),
                request.estoqueMinimo(),
                request.generoId(),
                request.desenvolvedoraId()
        );

        return mapper.toResponse(alterado);
    }

    @PutMapping("/{id}/status")
    public JogoResponse alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {
        return mapper.toResponse(service.alterarStatus(id, request.status()));
    }

    @PostMapping("/{id}/estoque/entradas")
    public JogoResponse registrarEntrada(
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoEstoqueRequest request
    ) {
        Jogo jogo = service.registrarEntradaEstoque(
                id,
                request.quantidade()
        );

        return mapper.toResponse(jogo);
    }

    @PostMapping("/{id}/estoque/saidas")
    public JogoResponse registrarSaida(
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoEstoqueRequest request
    ) {
        Jogo jogo = service.registrarSaidaEstoque(
                id,
                request.quantidade()
        );

        return mapper.toResponse(jogo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        service.excluir(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    public JogoResponse ativar(
            @PathVariable Long id
    ) {
        Jogo jogo = service.ativar(id);

        return mapper.toResponse(jogo);
    }

    @PatchMapping("/{id}/inativar")
    public JogoResponse inativar(
            @PathVariable Long id
    ) {
        Jogo jogo = service.inativar(id);

        return mapper.toResponse(jogo);
    }
}
