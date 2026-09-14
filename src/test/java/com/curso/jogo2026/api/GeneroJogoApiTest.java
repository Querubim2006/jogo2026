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
}