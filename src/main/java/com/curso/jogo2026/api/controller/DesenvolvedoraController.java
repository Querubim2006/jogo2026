package com.curso.jogo2026.api.controller;

import com.curso.jogo2026.api.dto.DesenvolvedoraRequest;
import com.curso.jogo2026.api.dto.DesenvolvedoraResponse;
import com.curso.jogo2026.api.error.ApiError;
import com.curso.jogo2026.api.mapper.DesenvolvedoraMapper;
import com.curso.jogo2026.domain.Desenvolvedora;
import com.curso.jogo2026.service.DesenvolvedoraService;
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
import java.util.List;

@RestController
@RequestMapping("/api/desenvolvedoras")
@Tag(name = "Desenvolvedoras", description = "Cadastro e consulta de desenvolvedoras de jogos")
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

    @Operation(summary = "Cadastrar desenvolvedora", description = "Cadastra uma desenvolvedora para associar aos jogos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Desenvolvedora cadastrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "CNPJ já utilizado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
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

    @Operation(summary = "Consultar desenvolvedora por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Desenvolvedora encontrada"),
            @ApiResponse(responseCode = "404", description = "Desenvolvedora não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public DesenvolvedoraResponse buscarPorId(
            @Parameter(description = "Identificador da desenvolvedora", example = "1")
            @PathVariable Long id
    ) {
        return mapper.toResponse(
                service.buscarPorId(id)
        );
    }

    @Operation(summary = "Listar desenvolvedoras", description = "Retorna as desenvolvedoras cadastradas.")
    @ApiResponse(responseCode = "200", description = "Lista de desenvolvedoras")
    @GetMapping
    public List<DesenvolvedoraResponse> listar() {
        return service.listarTodas()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Operation(summary = "Alterar desenvolvedora", description = "Atualiza nome de divulgação e CNPJ.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Desenvolvedora alterada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Desenvolvedora não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "CNPJ já utilizado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public DesenvolvedoraResponse alterar(
            @Parameter(description = "Identificador da desenvolvedora", example = "1")
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

    @Operation(summary = "Excluir desenvolvedora", description = "Exclui somente quando nenhum jogo estiver associado.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Desenvolvedora excluída; resposta sem corpo"),
            @ApiResponse(responseCode = "404", description = "Desenvolvedora não encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Desenvolvedora ainda possui jogos associados",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @Parameter(description = "Identificador da desenvolvedora", example = "1")
            @PathVariable Long id
    ) {
        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
