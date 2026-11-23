package com.curso.jogo2026.api;

import com.curso.jogo2026.repository.GeneroJogoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GeneroJogoApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GeneroJogoRepository repository;

    @Test
    void deveCadastrarGenero() throws Exception {
        mockMvc.perform(post("/api/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "RPG Teste 3"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nome").value("RPG Teste 3"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    void deveRetornar400QuandoNomeForVazio() throws Exception {
        mockMvc.perform(post("/api/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.nome").exists());
    }

    @Test
    void deveRetornar404QuandoGeneroNaoExistir() throws Exception {
        mockMvc.perform(get("/api/generos/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void devePesquisarGenerosComFiltroEPaginacao() throws Exception {
        mockMvc.perform(post("/api/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Corrida de Teste\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/generos")
                        .param("nome", "corrida")
                        .param("status", "ATIVO")
                        .param("pagina", "0")
                        .param("tamanho", "5")
                        .param("ordenarPor", "nome"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo.length()").value(1))
                .andExpect(jsonPath("$.pagina").value(0))
                .andExpect(jsonPath("$.tamanho").value(5));
    }

    @Test
    void deveAlterarStatusDoGeneroPorPut() throws Exception {
        String resposta = mockMvc.perform(post("/api/generos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Gênero para status\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Number id = com.jayway.jsonpath.JsonPath.read(resposta, "$.id");

        mockMvc.perform(put("/api/generos/{id}/status", id.longValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INATIVO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INATIVO"));
    }
}
