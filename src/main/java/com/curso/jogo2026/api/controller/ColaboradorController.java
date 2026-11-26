package com.curso.jogo2026.api.controller;

import com.curso.jogo2026.api.dto.*;
import com.curso.jogo2026.api.error.ApiError;
import com.curso.jogo2026.api.mapper.ColaboradorMapper;
import com.curso.jogo2026.domain.*;
import com.curso.jogo2026.dto.PaginaResponse;
import com.curso.jogo2026.service.ColaboradorService;
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

@RestController @RequestMapping("/api/colaboradores") @Tag(name="Colaboradores",description="Vínculos profissionais e funções de negócio")
public class ColaboradorController {
    private final ColaboradorService service; private final ColaboradorMapper mapper;
    public ColaboradorController(ColaboradorService service,ColaboradorMapper mapper){this.service=service;this.mapper=mapper;}
    @PostMapping @Operation(summary="Cadastrar colaborador")
    @ApiResponses({@ApiResponse(responseCode="201",description="Colaborador cadastrado"),@ApiResponse(responseCode="400",description="Pessoa inativa ou dados inválidos",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="404",description="Pessoa não encontrada",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="409",description="Pessoa já vinculada ou matrícula duplicada",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public ResponseEntity<ColaboradorResponse> cadastrar(@Valid @RequestBody ColaboradorRequest request){Colaborador c=service.cadastrar(request.pessoaId(),request.matricula(),request.funcao(),request.dataAdmissao());return ResponseEntity.created(URI.create("/api/colaboradores/"+c.getId())).body(mapper.toResponse(c));}
    @GetMapping("/{id}") @Operation(summary="Consultar colaborador por ID")
    @ApiResponses({@ApiResponse(responseCode="200",description="Colaborador encontrado"),@ApiResponse(responseCode="404",description="Colaborador não encontrado",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public ColaboradorResponse buscar(@PathVariable Long id){return mapper.toResponse(service.buscarPorId(id));}
    @GetMapping @Operation(summary="Pesquisar colaboradores",description="Pesquisa paginada por nome e função.")
    @ApiResponses({@ApiResponse(responseCode="200",description="Página de colaboradores"),@ApiResponse(responseCode="400",description="Filtro ou paginação inválidos",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public PaginaResponse<ColaboradorResponse> pesquisar(@RequestParam(required=false) String nome,@RequestParam(required=false) FuncaoColaborador funcao,@RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="20") int tamanho,@RequestParam(defaultValue="id") String ordenarPor,@RequestParam(defaultValue="ASC") String direcao){return PaginaResponse.de(service.pesquisar(nome,funcao,pagina,tamanho,ordenarPor,direcao),mapper::toResponse);}
    @PutMapping("/{id}") @Operation(summary="Alterar matrícula e função")
    @ApiResponses({@ApiResponse(responseCode="200",description="Colaborador alterado"),@ApiResponse(responseCode="400",description="Dados inválidos",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="404",description="Colaborador não encontrado",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="409",description="Matrícula já utilizada",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public ColaboradorResponse alterar(@PathVariable Long id,@Valid @RequestBody ColaboradorAtualizacaoRequest request){return mapper.toResponse(service.alterar(id,request.matricula(),request.funcao()));}
}
