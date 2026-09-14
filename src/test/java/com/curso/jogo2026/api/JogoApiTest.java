package com.curso.jogo2026.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JogoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornar400QuandoDadosForemInvalidos() throws Exception {
        String json = """
                {
                    "codigoBarras": "",
                    "titulo": "",
                    "saldoEstoque": -1,
                    "valorUnitario": -10,
                    "dataLancamento": null,
                    "estoqueMinimo": -1,
                    "generoId": null,
                    "desenvolvedoraId": null
                }
                """;

        mockMvc.perform(post("/api/jogos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
    @Test
    void deveCadastrarJogo() throws Exception {
        String generoJson = """
            {
                "nome": "RPG Teste"
            }
            """;

        String generoResponse = mockMvc.perform(post("/api/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(generoJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String generoId = JsonPath.read(generoResponse, "$.id").toString();

        String jogoJson = """
            {
                "codigoBarras": "JOGO-TESTE-001",
                "titulo": "Jogo Teste",
                "saldoEstoque": 10,
                "valorUnitario": 199.90,
                "dataLancamento": "2026-09-10",
                "estoqueMinimo": 2,
                "generoId": %s,
                "desenvolvedoraId": null
            }
            """.formatted(generoId);

        mockMvc.perform(post("/api/jogos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jogoJson))
                .andExpect(status().isCreated());
    }
    @Test
    void deveRetornar404QuandoGeneroNaoExistir() throws Exception {
        String jogoJson = """
            {
                "codigoBarras": "JOGO-TESTE-404",
                "titulo": "Jogo Teste 404",
                "saldoEstoque": 10,
                "valorUnitario": 199.90,
                "dataLancamento": "2026-09-10",
                "estoqueMinimo": 2,
                "generoId": 999999,
                "desenvolvedoraId": null
            }
            """;

        mockMvc.perform(post("/api/jogos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jogoJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
    @Test
    void deveRetornar404QuandoDesenvolvedoraNaoExistir() throws Exception {
        String jogoJson = """
            {
                "codigoBarras": "JOGO-TESTE-DEV-404",
                "titulo": "Jogo Teste Desenvolvedora",
                "saldoEstoque": 10,
                "valorUnitario": 199.90,
                "dataLancamento": "2026-09-10",
                "estoqueMinimo": 2,
                "generoId": 1,
                "desenvolvedoraId": 999999
            }
            """;

        mockMvc.perform(post("/api/jogos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jogoJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
    @Test
    void deveRetornar409QuandoCodigoDeBarrasJaExistir() throws Exception {
        String generoJson = """
            {
                "nome": "Aventura Teste"
            }
            """;

        String generoResponse = mockMvc.perform(post("/api/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(generoJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String generoId = JsonPath.read(generoResponse, "$.id").toString();

        String jogoJson = """
            {
                "codigoBarras": "JOGO-DUPLICADO-001",
                "titulo": "Jogo Duplicado",
                "saldoEstoque": 10,
                "valorUnitario": 199.90,
                "dataLancamento": "2026-09-10",
                "estoqueMinimo": 2,
                "generoId": %s,
                "desenvolvedoraId": null
            }
            """.formatted(generoId);

        mockMvc.perform(post("/api/jogos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jogoJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/jogos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jogoJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }
    @Test
    void deveBuscarJogoPorId() throws Exception {
        String generoJson = """
            {
                "nome": "RPG Busca Teste"
            }
            """;

        String generoResponse = mockMvc.perform(post("/api/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(generoJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String generoId = JsonPath.read(generoResponse, "$.id").toString();

        String jogoJson = """
            {
                "codigoBarras": "JOGO-BUSCA-001",
                "titulo": "Jogo Busca Teste",
                "saldoEstoque": 10,
                "valorUnitario": 199.90,
                "dataLancamento": "2026-09-10",
                "estoqueMinimo": 2,
                "generoId": %s,
                "desenvolvedoraId": null
            }
            """.formatted(generoId);

        String jogoResponse = mockMvc.perform(post("/api/jogos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jogoJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String jogoId = JsonPath.read(jogoResponse, "$.id").toString();

        mockMvc.perform(get("/api/jogos/" + jogoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Integer.parseInt(jogoId)))
                .andExpect(jsonPath("$.titulo").value("Jogo Busca Teste"))
                .andExpect(jsonPath("$.codigoBarras").value("JOGO-BUSCA-001"));
    }
    @Test
    void deveRetornar404QuandoJogoNaoExistir() throws Exception {
        mockMvc.perform(get("/api/jogos/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
    @Test
    void deveListarJogos() throws Exception {
        mockMvc.perform(get("/api/jogos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}