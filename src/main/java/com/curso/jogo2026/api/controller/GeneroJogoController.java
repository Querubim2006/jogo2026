package com.curso.jogo2026.api.controller;

import com.curso.jogo2026.api.dto.GeneroJogoRequest;
import com.curso.jogo2026.api.dto.GeneroJogoResponse;
import com.curso.jogo2026.api.dto.StatusRequest;
import com.curso.jogo2026.api.error.ApiError;
import com.curso.jogo2026.api.mapper.GeneroJogoMapper;
import com.curso.jogo2026.domain.GeneroJogo;
import com.curso.jogo2026.domain.Status;
import com.curso.jogo2026.dto.PaginaResponse;
import com.curso.jogo2026.service.GeneroJogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Gêneros", description = "Cadastro, consulta e organização dos gêneros de jogos")
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

    @Operation(summary = "Cadastrar gênero", description = "Cria um gênero ativo para classificar jogos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Gênero cadastrado"),
            @ApiResponse(responseCode = "400", description = "Nome inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Nome já utilizado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<GeneroJogoResponse> cadastrar(
            @Valid @RequestBody GeneroJogoRequest request
    ) {
        GeneroJogo genero = service.cadastrar(request.nome());
        URI location = URI.create("/api/generos/" + genero.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(genero));
    }

    @Operation(summary = "Consultar gênero por ID", description = "Retorna um gênero pelo identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Gênero encontrado"),
            @ApiResponse(responseCode = "404", description = "Gênero não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public GeneroJogoResponse buscarPorId(
            @Parameter(description = "Identificador do gênero", example = "1")
            @PathVariable Long id
    ) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @Operation(summary = "Pesquisar gêneros", description = "Combina filtros de nome e status e retorna uma página ordenada. A paginação começa em zero e o tamanho máximo é 100.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de gêneros"),
            @ApiResponse(responseCode = "400", description = "Página, tamanho, filtro ou ordenação inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public PaginaResponse<GeneroJogoResponse> pesquisar(
            @Parameter(description = "Trecho do nome, sem diferenciar maiúsculas", example = "ação")
            @RequestParam(required = false) String nome,
            @Parameter(description = "Status do gênero", example = "ATIVO")
            @RequestParam(required = false) Status status,
            @Parameter(description = "Índice da página, iniciado em zero", example = "0")
            @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Quantidade por página, de 1 a 100", example = "20")
            @RequestParam(defaultValue = "20") int tamanho,
            @Parameter(description = "Campo permitido para ordenação: id, nome ou status", example = "nome")
            @RequestParam(defaultValue = "id") String ordenarPor,
            @Parameter(description = "Sentido da ordenação: ASC ou DESC", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direcao) {
        return PaginaResponse.de(
                service.pesquisar(nome, status, pagina, tamanho, ordenarPor, direcao),
                mapper::toResponse);
    }

    @Operation(summary = "Alterar gênero", description = "Substitui o nome editável do gênero.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Gênero alterado"),
            @ApiResponse(responseCode = "400", description = "Nome inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Gênero não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Nome já utilizado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public GeneroJogoResponse alterar(
            @Parameter(description = "Identificador do gênero", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody GeneroJogoRequest request
    ) {
        GeneroJogo genero = service.alterar(id, request.nome());
        return mapper.toResponse(genero);
    }

    @Operation(summary = "Alterar status do gênero", description = "Ativa ou inativa o gênero sem removê-lo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status alterado"),
            @ApiResponse(responseCode = "400", description = "Status inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Gênero não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}/status")
    public GeneroJogoResponse alterarStatus(
            @Parameter(description = "Identificador do gênero", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {
        return mapper.toResponse(service.alterarStatus(id, request.status()));
    }

    @Operation(summary = "Excluir gênero", description = "Remove o gênero somente quando nenhum jogo estiver associado.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Gênero excluído; resposta sem corpo"),
            @ApiResponse(responseCode = "404", description = "Gênero não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Gênero ainda possui jogos associados",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @Parameter(description = "Identificador do gênero", example = "1")
            @PathVariable Long id
    ) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
