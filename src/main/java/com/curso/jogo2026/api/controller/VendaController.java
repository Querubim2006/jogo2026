package com.curso.jogo2026.api.controller;

import com.curso.jogo2026.api.dto.*;
import com.curso.jogo2026.api.error.ApiError;
import com.curso.jogo2026.api.mapper.VendaMapper;
import com.curso.jogo2026.domain.StatusVenda;
import com.curso.jogo2026.domain.Venda;
import com.curso.jogo2026.dto.PaginaResponse;
import com.curso.jogo2026.service.VendaService;
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
import java.time.LocalDate;

@RestController @RequestMapping("/api/vendas") @Tag(name="Vendas",description="Vendas abertas, itens com preço histórico e finalização transacional")
public class VendaController {
    private final VendaService service; private final VendaMapper mapper;
    public VendaController(VendaService service,VendaMapper mapper){this.service=service;this.mapper=mapper;}
    @PostMapping @Operation(summary="Abrir venda",description="Cria venda aberta para cliente e vendedor ativos.")
    @ApiResponses({@ApiResponse(responseCode="201",description="Venda aberta"),@ApiResponse(responseCode="400",description="Participante inativo ou função não permitida",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="404",description="Cliente ou colaborador não encontrado",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public ResponseEntity<VendaResponse> cadastrar(@Valid @RequestBody VendaRequest request){Venda v=service.cadastrar(request.clienteId(),request.vendedorId());return ResponseEntity.created(URI.create("/api/vendas/"+v.getId())).body(mapper.toResponse(v));}
    @GetMapping("/{id}") @Operation(summary="Consultar venda",description="Retorna cabeçalho, itens, preços históricos e total calculado.")
    @ApiResponses({@ApiResponse(responseCode="200",description="Venda encontrada"),@ApiResponse(responseCode="404",description="Venda não encontrada",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public VendaResponse buscar(@PathVariable Long id){return mapper.toResponse(service.buscarPorId(id));}
    @GetMapping @Operation(summary="Pesquisar vendas",description="Filtra por status, cliente e intervalo de datas; a página não carrega a coleção de itens.")
    @ApiResponses({@ApiResponse(responseCode="200",description="Página de resumos de venda"),@ApiResponse(responseCode="400",description="Intervalo, ordenação ou paginação inválidos",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public PaginaResponse<VendaResumoResponse> pesquisar(@RequestParam(required=false) StatusVenda status,
            @RequestParam(required=false) Long clienteId,@Parameter(description="Data inicial inclusiva",example="2026-09-01") @RequestParam(required=false) LocalDate dataInicial,
            @Parameter(description="Data final inclusiva",example="2026-09-30") @RequestParam(required=false) LocalDate dataFinal,
            @RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="20") int tamanho,
            @RequestParam(defaultValue="id") String ordenarPor,@RequestParam(defaultValue="ASC") String direcao){
        return PaginaResponse.de(service.pesquisar(status,clienteId,dataInicial,dataFinal,pagina,tamanho,ordenarPor,direcao),mapper::toResumo);
    }
    @PostMapping("/{id}/itens") @Operation(summary="Adicionar jogo à venda",description="Registra quantidade e copia o preço vigente. Só funciona enquanto aberta; um jogo não pode se repetir nos itens.")
    @ApiResponses({@ApiResponse(responseCode="200",description="Item adicionado"),@ApiResponse(responseCode="400",description="Venda fechada, jogo inativo, repetido ou quantidade inválida",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="404",description="Venda ou jogo não encontrado",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public VendaResponse adicionarItem(@PathVariable Long id,@Valid @RequestBody ItemVendaRequest request){return mapper.toResponse(service.adicionarItem(id,request.produtoId(),request.quantidade()));}
    @DeleteMapping("/{vendaId}/itens/{itemId}") @Operation(summary="Remover item de venda aberta")
    @ApiResponses({@ApiResponse(responseCode="200",description="Item removido"),@ApiResponse(responseCode="400",description="Venda fechada ou item não pertence à venda",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="404",description="Venda não encontrada",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public VendaResponse removerItem(@PathVariable Long vendaId,@PathVariable Long itemId){return mapper.toResponse(service.removerItem(vendaId,itemId));}
    @PostMapping("/{id}/finalizacao") @Operation(summary="Finalizar venda",description="Baixa todos os saldos e finaliza em uma transação. Repetir a chamada não repete a baixa.")
    @ApiResponses({@ApiResponse(responseCode="200",description="Venda finalizada"),@ApiResponse(responseCode="400",description="Venda vazia, já encerrada ou saldo insuficiente; toda a baixa é revertida",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="404",description="Venda não encontrada",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public VendaResponse finalizar(@PathVariable Long id){return mapper.toResponse(service.finalizar(id));}
    @PostMapping("/{id}/cancelamento") @Operation(summary="Cancelar venda aberta",description="Cancela sem movimentar estoque; venda finalizada não pode ser estornada nesta aula.")
    @ApiResponses({@ApiResponse(responseCode="200",description="Venda cancelada"),@ApiResponse(responseCode="400",description="Venda já encerrada",content=@Content(schema=@Schema(implementation=ApiError.class))),@ApiResponse(responseCode="404",description="Venda não encontrada",content=@Content(schema=@Schema(implementation=ApiError.class)))})
    public VendaResponse cancelar(@PathVariable Long id){return mapper.toResponse(service.cancelar(id));}
}
