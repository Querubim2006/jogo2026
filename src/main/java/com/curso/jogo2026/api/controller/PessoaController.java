package com.curso.jogo2026.api.controller;

import com.curso.jogo2026.api.dto.*;
import com.curso.jogo2026.api.error.ApiError;
import com.curso.jogo2026.api.mapper.PessoaMapper;
import com.curso.jogo2026.domain.Pessoa;
import com.curso.jogo2026.domain.Status;
import com.curso.jogo2026.dto.PaginaResponse;
import com.curso.jogo2026.service.PessoaService;
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

@RestController @RequestMapping("/api/pessoas") @Tag(name = "Pessoas", description = "Identidades civis, separadas dos papéis comerciais")
public class PessoaController {
    private final PessoaService service; private final PessoaMapper mapper;
    public PessoaController(PessoaService service, PessoaMapper mapper) { this.service = service; this.mapper = mapper; }

    @PostMapping @Operation(summary = "Cadastrar pessoa", description = "Cria identidade com CPF validado; não cria conta de acesso.")
    @ApiResponses({@ApiResponse(responseCode="201",description="Pessoa cadastrada"), @ApiResponse(responseCode="400",description="Dados ou CPF inválidos",content=@Content(schema=@Schema(implementation=ApiError.class))), @ApiResponse(responseCode="409",description="CPF ou e-mail já utilizado",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public ResponseEntity<PessoaResponse> cadastrar(@Valid @RequestBody PessoaRequest request) {
        Pessoa p = service.cadastrar(new Pessoa(request.nome(), request.email(), request.cpf(), java.time.LocalDate.now()));
        return ResponseEntity.created(URI.create("/api/pessoas/" + p.getId())).body(mapper.toResponse(p));
    }
    @GetMapping("/{id}") @Operation(summary="Consultar pessoa por ID")
    @ApiResponses({@ApiResponse(responseCode="200",description="Pessoa encontrada"),@ApiResponse(responseCode="404",description="Pessoa não encontrada",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public PessoaResponse buscar(@PathVariable Long id) { return mapper.toResponse(service.buscarPorId(id)); }
    @GetMapping @Operation(summary="Pesquisar pessoas",description="Pesquisa paginada por nome e status. Página inicia em zero; tamanho máximo 100.")
    @ApiResponses({@ApiResponse(responseCode="200",description="Página de pessoas"),@ApiResponse(responseCode="400",description="Filtro ou paginação inválidos",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public PaginaResponse<PessoaResponse> pesquisar(@RequestParam(required=false) String nome,@RequestParam(required=false) Status status,
            @RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="20") int tamanho,@RequestParam(defaultValue="id") String ordenarPor,@RequestParam(defaultValue="ASC") String direcao) {
        return PaginaResponse.de(service.pesquisar(nome,status,pagina,tamanho,ordenarPor,direcao),mapper::toResponse);
    }
    @PutMapping("/{id}") @Operation(summary="Alterar dados da pessoa")
    @ApiResponses({@ApiResponse(responseCode="200",description="Pessoa alterada"),@ApiResponse(responseCode="400",description="Dados inválidos",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="404",description="Pessoa não encontrada",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="409",description="CPF ou e-mail já utilizado",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public PessoaResponse alterar(@PathVariable Long id,@Valid @RequestBody PessoaRequest request) { return mapper.toResponse(service.alterar(id,request.nome(),request.email(),request.cpf())); }
    @PutMapping("/{id}/status") @Operation(summary="Alterar status da pessoa")
    @ApiResponses({@ApiResponse(responseCode="200",description="Status alterado"),@ApiResponse(responseCode="400",description="Status inválido",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="404",description="Pessoa não encontrada",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public PessoaResponse alterarStatus(@PathVariable Long id,@Valid @RequestBody StatusRequest request) { return mapper.toResponse(service.alterarStatus(id,request.status())); }
}
