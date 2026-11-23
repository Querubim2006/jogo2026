
package com.curso.jogo2026.api;

import com.jayway.jsonpath.JsonPath;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AulaOitoApiTest {

    @Autowired
    private MockMvc mockMvc;

    // Cria um gênero com nome único para cada teste
    private Long criarGenero() throws Exception {

        String nomeUnico = "Genero Aula Oito " + UUID.randomUUID();

        String json = """
                {
                    "nome": "%s"
                }
                """.formatted(nomeUnico);

        MvcResult resultado = mockMvc.perform(post("/api/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        String resposta = resultado.getResponse().getContentAsString();

        return ((Number) JsonPath.read(resposta, "$.id")).longValue();
    }

    // Cria uma desenvolvedora
    private Long criarDesenvolvedora() throws Exception {

        String json = """
                {
                    "nomeFantasia": "Desenvolvedora Aula Oito",
                    "cnpj": "12345678000199"
                }
                """;

        String resposta = mockMvc.perform(post("/api/desenvolvedoras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return ((Number) JsonPath.read(resposta, "$.id")).longValue();
    }

    // Cria um jogo
    private Long criarJogo(
            Long generoId,
            Long desenvolvedoraId,
            String saldo
    ) throws Exception {

        String json = """
                {
                    "codigoBarras": "JOGO-AULA8-%s",
                    "titulo": "Jogo Aula Oito",
                    "saldoEstoque": %s,
                    "valorUnitario": 199.90,
                    "dataLancamento": "2026-09-10",
                    "estoqueMinimo": 2,
                    "generoId": %d,
                    "desenvolvedoraId": %s
                }
                """.formatted(
                UUID.randomUUID(),
                saldo,
                generoId,
                desenvolvedoraId == null
                        ? "null"
                        : desenvolvedoraId.toString()
        );

        String resposta = mockMvc.perform(post("/api/jogos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return ((Number) JsonPath.read(resposta, "$.id")).longValue();
    }

    // Testa entrada de estoque
    @Test
    void deveRegistrarEntradaDeEstoque() throws Exception {

        Long jogoId = criarJogo(criarGenero(), null, "10");

        String json = """
                {
                    "quantidade": 5
                }
                """;

        mockMvc.perform(post(
                        "/api/jogos/" + jogoId + "/estoque/entradas"
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoEstoque").value(15));
    }

    // Testa saída de estoque
    @Test
    void deveRegistrarSaidaDeEstoque() throws Exception {

        Long jogoId = criarJogo(criarGenero(), null, "10");

        String json = """
                {
                    "quantidade": 3
                }
                """;

        mockMvc.perform(post(
                        "/api/jogos/" + jogoId + "/estoque/saidas"
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoEstoque").value(7));
    }

    // Não permite saída maior que o saldo disponível
    @Test
    void naoDevePermitirSaidaMaiorQueSaldo() throws Exception {

        Long jogoId = criarJogo(criarGenero(), null, "10");

        String json = """
                {
                    "quantidade": 20
                }
                """;

        mockMvc.perform(post(
                        "/api/jogos/" + jogoId + "/estoque/saidas"
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/jogos/" + jogoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoEstoque").value(10));
    }

    // Não permite quantidade negativa na entrada
    @Test
    void naoDevePermitirQuantidadeNegativaNaEntrada() throws Exception {

        Long jogoId = criarJogo(criarGenero(), null, "10");

        String json = """
                {
                    "quantidade": -5
                }
                """;

        mockMvc.perform(post(
                        "/api/jogos/" + jogoId + "/estoque/entradas"
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // Testa ativação de jogo
    @Test
    void deveAtivarJogo() throws Exception {

        Long jogoId = criarJogo(criarGenero(), null, "10");

        mockMvc.perform(patch("/api/jogos/" + jogoId + "/inativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INATIVO"));

        mockMvc.perform(patch("/api/jogos/" + jogoId + "/ativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    // Testa inativação de jogo
    @Test
    void deveInativarJogo() throws Exception {

        Long jogoId = criarJogo(criarGenero(), null, "10");

        mockMvc.perform(patch("/api/jogos/" + jogoId + "/inativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INATIVO"));
    }

    // Não permite excluir desenvolvedora associada a um jogo
    @Test
    void naoDeveExcluirDesenvolvedoraAssociadaAUmJogo()
            throws Exception {

        Long generoId = criarGenero();
        Long desenvolvedoraId = criarDesenvolvedora();

        criarJogo(generoId, desenvolvedoraId, "10");

        mockMvc.perform(delete(
                        "/api/desenvolvedoras/" + desenvolvedoraId
                ))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    // PUT não deve alterar o saldo do estoque
    @Test
    void putNaoDeveAlterarSaldoEstoque() throws Exception {

        Long jogoId = criarJogo(criarGenero(), null, "10");

        Long novoGeneroId = criarGenero();

        String json = """
                {
                    "titulo": "Jogo Atualizado",
                    "valorUnitario": 249.90,
                    "dataLancamento": "2026-09-10",
                    "estoqueMinimo": 3,
                    "generoId": %d,
                    "desenvolvedoraId": null
                }
                """.formatted(novoGeneroId);

        mockMvc.perform(put("/api/jogos/" + jogoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Jogo Atualizado"))
                .andExpect(jsonPath("$.saldoEstoque").value(10));
    }

    @Test
    void devePesquisarJogosComFiltrosEPaginacao() throws Exception {
        Long generoId = criarGenero();
        criarJogo(generoId, null, "1");
        criarJogo(generoId, null, "10");

        mockMvc.perform(get("/api/jogos")
                        .param("titulo", "jogo aula oito")
                        .param("abaixoEstoqueMinimo", "true")
                        .param("pagina", "0")
                        .param("tamanho", "1")
                        .param("ordenarPor", "titulo")
                        .param("direcao", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo.length()").value(1))
                .andExpect(jsonPath("$.pagina").value(0))
                .andExpect(jsonPath("$.tamanho").value(1))
                .andExpect(jsonPath("$.totalElementos").value(1));
    }

    @Test
    void naoDeveAceitarOrdenacaoPorAssociacaoOuTamanhoMaiorQue100()
            throws Exception {
        mockMvc.perform(get("/api/jogos").param("ordenarPor", "genero.nome"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/jogos").param("tamanho", "101"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAlterarStatusDoJogoPorPut() throws Exception {
        Long jogoId = criarJogo(criarGenero(), null, "5");

        mockMvc.perform(put("/api/jogos/{id}/status", jogoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INATIVO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INATIVO"));
    }

    @Test
    void statusForaDoEnumRetorna400() throws Exception {
        Long jogoId = criarJogo(criarGenero(), null, "5");

        mockMvc.perform(put("/api/jogos/{id}/status", jogoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"PAUSADO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar204AoExcluirJogoE404NaConsultaSeguinte()
            throws Exception {
        Long jogoId = criarJogo(criarGenero(), null, "5");

        mockMvc.perform(delete("/api/jogos/{id}", jogoId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        mockMvc.perform(get("/api/jogos/{id}", jogoId))
                .andExpect(status().isNotFound());
    }

    @Test
    void naoDeveExcluirGeneroQuePossuiJogo() throws Exception {
        Long generoId = criarGenero();
        criarJogo(generoId, null, "5");

        mockMvc.perform(delete("/api/generos/{id}", generoId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void putDeJogoInexistenteRetorna404() throws Exception {
        mockMvc.perform(put("/api/jogos/{id}", 999999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "titulo": "Jogo atualizado",
                                  "valorUnitario": 10.00,
                                  "dataLancamento": "2026-09-10",
                                  "estoqueMinimo": 1,
                                  "generoId": 1,
                                  "desenvolvedoraId": null
                                }
                                """))
                .andExpect(status().isNotFound());
    }
}
