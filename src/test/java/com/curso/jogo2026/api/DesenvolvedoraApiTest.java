package com.curso.jogo2026.api;

import com.curso.jogo2026.repository.DesenvolvedoraRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DesenvolvedoraApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DesenvolvedoraRepository repository;

    @Test
    void deveCadastrarDesenvolvedora() throws Exception {
        mockMvc.perform(post("/api/desenvolvedoras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nomeFantasia": "Nintendo",
                                    "cnpj": "45678912000155"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nomeFantasia").value("Nintendo"))
                .andExpect(jsonPath("$.cnpj").value("45678912000155"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }
    @Test
    void deveRetornar400QuandoNomeForVazio() throws Exception {
        String json = """
            {
                "nomeFantasia": "",
                "cnpj": "98765432000188"
            }
            """;

        mockMvc.perform(post("/api/desenvolvedoras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.fields.nomeFantasia").exists());
    }
    @Test
    void deveRetornar404QuandoDesenvolvedoraNaoExistir() throws Exception {
        mockMvc.perform(get("/api/desenvolvedoras/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
    @Test
    void deveRetornar409QuandoCnpjJaExistir() throws Exception {
        String json = """
            {
                "nomeFantasia": "Nintendo",
                "cnpj": "45678912000155"
            }
            """;

        mockMvc.perform(post("/api/desenvolvedoras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/desenvolvedoras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }
}