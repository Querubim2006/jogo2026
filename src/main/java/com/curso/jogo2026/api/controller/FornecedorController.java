package com.curso.jogo2026.api.controller;

import com.curso.jogo2026.api.dto.FornecedorRequest;
import com.curso.jogo2026.api.dto.FornecedorResponse;
import com.curso.jogo2026.api.error.ApiError;
import com.curso.jogo2026.api.mapper.FornecedorMapper;
import com.curso.jogo2026.domain.Fornecedor;
import com.curso.jogo2026.service.FornecedorService;
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
import java.util.List;

@RestController @RequestMapping("/api/fornecedores") @Tag(name="Fornecedores",description="Empresas que fornecem jogos para a loja; distintas das desenvolvedoras")
public class FornecedorController {
    private final FornecedorService service; private final FornecedorMapper mapper;
    public FornecedorController(FornecedorService service,FornecedorMapper mapper){this.service=service;this.mapper=mapper;}
    @PostMapping @Operation(summary="Cadastrar fornecedor",description="Valida os dígitos verificadores do CNPJ; não consulta cadastro externo.")
    @ApiResponses({@ApiResponse(responseCode="201",description="Fornecedor cadastrado"),@ApiResponse(responseCode="400",description="Dados ou CNPJ inválidos",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="409",description="CNPJ já cadastrado",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public ResponseEntity<FornecedorResponse> cadastrar(@Valid @RequestBody FornecedorRequest request){Fornecedor f=service.cadastrar(new Fornecedor(request.razaoSocial(),request.cnpj()));return ResponseEntity.created(URI.create("/api/fornecedores/"+f.getId())).body(mapper.toResponse(f));}
    @GetMapping("/{id}") @Operation(summary="Consultar fornecedor por ID")
    @ApiResponses({@ApiResponse(responseCode="200",description="Fornecedor encontrado"),@ApiResponse(responseCode="404",description="Fornecedor não encontrado",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public FornecedorResponse buscar(@PathVariable Long id){return mapper.toResponse(service.buscarPorId(id));}
    @GetMapping @Operation(summary="Listar fornecedores") @ApiResponse(responseCode="200",description="Lista de fornecedores")
    public List<FornecedorResponse> listar(){return service.listar().stream().map(mapper::toResponse).toList();}
    @PutMapping("/{id}") @Operation(summary="Alterar fornecedor")
    @ApiResponses({@ApiResponse(responseCode="200",description="Fornecedor alterado"),@ApiResponse(responseCode="400",description="Dados inválidos",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="404",description="Fornecedor não encontrado",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="409",description="CNPJ já utilizado",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public FornecedorResponse alterar(@PathVariable Long id,@Valid @RequestBody FornecedorRequest request){return mapper.toResponse(service.alterar(id,request.razaoSocial(),request.cnpj()));}
    @DeleteMapping("/{id}") @Operation(summary="Excluir fornecedor",description="Só é possível excluir quando não estiver associado a jogos.")
    @ApiResponses({@ApiResponse(responseCode="204",description="Fornecedor excluído"),@ApiResponse(responseCode="404",description="Fornecedor não encontrado",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="409",description="Fornecedor associado a jogos",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public ResponseEntity<Void> excluir(@PathVariable Long id){service.excluir(id);return ResponseEntity.noContent().build();}
}
