package com.curso.jogo2026.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional @SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class AulaDezApiTest {
    @Autowired MockMvc mockMvc;
    @Test void rejeitaCpfComDigitoVerificadorInvalidoNaApi() throws Exception {
        mockMvc.perform(post("/api/pessoas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Pessoa Teste\",\"email\":\"pessoa@example.com\",\"cpf\":\"52998224724\"}"))
                .andExpect(status().isBadRequest());
    }
    @Test void rejeitaCnpjComDigitoVerificadorInvalidoNaApi() throws Exception {
        mockMvc.perform(post("/api/fornecedores").contentType(MediaType.APPLICATION_JSON)
                .content("{\"razaoSocial\":\"Fornecedor Teste\",\"cnpj\":\"11222333000182\"}"))
                .andExpect(status().isBadRequest());
    }
}
