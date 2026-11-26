package com.curso.jogo2026.api.controller;

import com.curso.jogo2026.api.dto.*;
import com.curso.jogo2026.api.error.ApiError;
import com.curso.jogo2026.api.mapper.ClienteMapper;
import com.curso.jogo2026.domain.Cliente;
import com.curso.jogo2026.dto.PaginaResponse;
import com.curso.jogo2026.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController @RequestMapping("/api/clientes") @Tag(name="Clientes",description="Papel comercial associado a uma pessoa")
public class ClienteController {
    private final ClienteService service; private final ClienteMapper mapper;
    public ClienteController(ClienteService service,ClienteMapper mapper){this.service=service;this.mapper=mapper;}
    @PostMapping @Operation(summary="Associar papel de cliente")
    @ApiResponses({@ApiResponse(responseCode="201",description="Cliente cadastrado"),@ApiResponse(responseCode="400",description="Pessoa inativa ou dados inválidos",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="404",description="Pessoa não encontrada",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="409",description="Pessoa já possui papel de cliente",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public ResponseEntity<ClienteResponse> cadastrar(@Valid @RequestBody ClienteRequest request){Cliente c=service.cadastrar(request.pessoaId(),request.telefone());return ResponseEntity.created(URI.create("/api/clientes/"+c.getId())).body(mapper.toResponse(c));}
    @GetMapping("/{id}") @Operation(summary="Consultar cliente por ID")
    @ApiResponses({@ApiResponse(responseCode="200",description="Cliente encontrado"),@ApiResponse(responseCode="404",description="Cliente não encontrado",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public ClienteResponse buscar(@PathVariable Long id){return mapper.toResponse(service.buscarPorId(id));}
    @GetMapping @Operation(summary="Pesquisar clientes",description="Pesquisa paginada pelo nome da pessoa.")
    @ApiResponses({@ApiResponse(responseCode="200",description="Página de clientes"),@ApiResponse(responseCode="400",description="Paginação inválida",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public PaginaResponse<ClienteResponse> pesquisar(@RequestParam(required=false) String nome,@RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="20") int tamanho,@RequestParam(defaultValue="id") String ordenarPor,@RequestParam(defaultValue="ASC") String direcao){return PaginaResponse.de(service.pesquisar(nome,pagina,tamanho,ordenarPor,direcao),mapper::toResponse);}
    @PutMapping("/{id}") @Operation(summary="Alterar telefone do cliente")
    @ApiResponses({@ApiResponse(responseCode="200",description="Cliente alterado"),@ApiResponse(responseCode="400",description="Dados inválidos",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="404",description="Cliente não encontrado",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public ClienteResponse alterar(@PathVariable Long id,@Valid @RequestBody ClienteAtualizacaoRequest request){return mapper.toResponse(service.alterar(id,request.telefone()));}
}
