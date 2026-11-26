package com.curso.jogo2026.api.controller;

import com.curso.jogo2026.api.dto.JogoAtualizacaoRequest;
import com.curso.jogo2026.api.dto.JogoRequest;
import com.curso.jogo2026.api.dto.JogoResponse;
import com.curso.jogo2026.api.dto.MovimentacaoEstoqueRequest;
import com.curso.jogo2026.api.dto.StatusRequest;
import com.curso.jogo2026.api.error.ApiError;
import com.curso.jogo2026.api.mapper.JogoMapper;
import com.curso.jogo2026.domain.Jogo;
import com.curso.jogo2026.domain.Status;
import com.curso.jogo2026.dto.PaginaResponse;
import com.curso.jogo2026.service.JogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Jogos", description = "Cadastro, consulta e movimentação do estoque de jogos")
@RestController
@RequestMapping("/api/jogos")
public class JogoController {

    private final JogoService service;
    private final JogoMapper mapper;

    public JogoController(JogoService service, JogoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Cadastrar jogo", description = "Cria um jogo ativo associado a um gênero e, opcionalmente, a uma desenvolvedora e a um fornecedor.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Jogo cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Gênero, desenvolvedora ou fornecedor não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Código de barras já utilizado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public ResponseEntity<JogoResponse> cadastrar(@Valid @RequestBody JogoRequest request) {
        Jogo jogo = mapper.toEntity(request);
        Jogo cadastrado = service.cadastrar(jogo, request.generoId(), request.desenvolvedoraId(), request.fornecedorId());
        URI location = URI.create("/api/jogos/" + cadastrado.getId());
        return ResponseEntity.created(location).body(mapper.toResponse(cadastrado));
    }

    @Operation(summary = "Consultar jogo por ID", description = "Retorna os dados necessários para visualizar ou preencher a tela de alteração.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jogo encontrado"),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public JogoResponse buscarPorId(
            @Parameter(description = "Identificador do jogo", example = "10")
            @PathVariable Long id) {
        return mapper.toResponse(service.buscarPorId(id));
    }

    @Operation(summary = "Pesquisar jogos", description = "Combina filtros no banco e retorna uma página. A primeira página é 0, o tamanho máximo é 100 e a ordenação aceita campos simples permitidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de jogos"),
            @ApiResponse(responseCode = "400", description = "Filtro, página, tamanho ou ordenação inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public PaginaResponse<JogoResponse> pesquisar(
            @Parameter(description = "Trecho do título, sem diferenciar maiúsculas", example = "aventura")
            @RequestParam(required = false) String titulo,
            @Parameter(description = "Status do jogo", example = "ATIVO")
            @RequestParam(required = false) Status status,
            @Parameter(description = "Identificador exato do gênero", example = "1")
            @RequestParam(required = false) Long generoId,
            @Parameter(description = "Identificador exato da desenvolvedora", example = "1")
            @RequestParam(required = false) Long desenvolvedoraId,
            @Parameter(description = "Quando true, inclui apenas jogos abaixo do estoque mínimo", example = "true")
            @RequestParam(required = false) Boolean abaixoEstoqueMinimo,
            @Parameter(description = "Índice da página, iniciado em zero", example = "0")
            @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Quantidade por página, de 1 a 100", example = "20")
            @RequestParam(defaultValue = "20") int tamanho,
            @Parameter(description = "Campo permitido para ordenação, por exemplo id ou titulo", example = "titulo")
            @RequestParam(defaultValue = "id") String ordenarPor,
            @Parameter(description = "Sentido da ordenação: ASC ou DESC", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direcao) {
        Page<Jogo> resultado = service.pesquisar(
                titulo, status, generoId, desenvolvedoraId, abaixoEstoqueMinimo,
                pagina, tamanho, ordenarPor, direcao);
        return PaginaResponse.de(resultado, mapper::toResponse);
    }

    @Operation(summary = "Alterar jogo", description = "Substitui os campos editáveis. Código de barras, saldo e status mudam por operações próprias.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jogo alterado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Jogo, gênero, desenvolvedora ou fornecedor não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Conflito com uma restrição de integridade",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public JogoResponse alterar(
            @Parameter(description = "Identificador do jogo", example = "10")
            @PathVariable Long id,
            @Valid @RequestBody JogoAtualizacaoRequest request) {
        Jogo alterado = service.alterar(
                id, request.titulo(), request.valorUnitario(), request.dataLancamento(),
                request.estoqueMinimo(), request.generoId(), request.desenvolvedoraId(), request.fornecedorId());
        return mapper.toResponse(alterado);
    }

    @Operation(summary = "Alterar status do jogo", description = "Ativa ou inativa o jogo sem excluí-lo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status alterado"),
            @ApiResponse(responseCode = "400", description = "Status inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}/status")
    public JogoResponse alterarStatus(
            @Parameter(description = "Identificador do jogo", example = "10")
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest request) {
        return mapper.toResponse(service.alterarStatus(id, request.status()));
    }

    @Operation(summary = "Registrar entrada de estoque", description = "Soma uma quantidade positiva ao saldo atual; repetir a chamada repete o efeito.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrada registrada"),
            @ApiResponse(responseCode = "400", description = "Quantidade inválida",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/{id}/estoque/entradas")
    public JogoResponse registrarEntrada(
            @Parameter(description = "Identificador do jogo", example = "10")
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoEstoqueRequest request) {
        return mapper.toResponse(service.registrarEntradaEstoque(id, request.quantidade()));
    }

    @Operation(summary = "Registrar saída de estoque", description = "Subtrai uma quantidade positiva; não permite saldo negativo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saída registrada"),
            @ApiResponse(responseCode = "400", description = "Quantidade inválida ou saldo insuficiente",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/{id}/estoque/saidas")
    public JogoResponse registrarSaida(
            @Parameter(description = "Identificador do jogo", example = "10")
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoEstoqueRequest request) {
        return mapper.toResponse(service.registrarSaidaEstoque(id, request.quantidade()));
    }

    @Operation(summary = "Excluir jogo", description = "Remove fisicamente o jogo do cadastro.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Jogo excluído; resposta sem corpo"),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Exclusão conflita com uma restrição de integridade",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @Parameter(description = "Identificador do jogo", example = "10")
            @PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ativar jogo", description = "Rota de compatibilidade para ativar um jogo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jogo ativado"),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PatchMapping("/{id}/ativar")
    public JogoResponse ativar(
            @Parameter(description = "Identificador do jogo", example = "10")
            @PathVariable Long id) {
        return mapper.toResponse(service.ativar(id));
    }

    @Operation(summary = "Inativar jogo", description = "Rota de compatibilidade para inativar um jogo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jogo inativado"),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PatchMapping("/{id}/inativar")
    public JogoResponse inativar(
            @Parameter(description = "Identificador do jogo", example = "10")
            @PathVariable Long id) {
        return mapper.toResponse(service.inativar(id));
    }
}
