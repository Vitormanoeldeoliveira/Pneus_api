package com.mosca.pneus;

import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RodizioFluxoTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void crudEMontagemSemDto() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Rodízio API"))
                .andExpect(jsonPath("$.paths['/api/pneus']").exists())
                .andExpect(jsonPath("$.paths['/api/equipamentos']").exists())
                .andExpect(jsonPath("$.paths['/api/movimentacoes']").exists());

        int equipamentoId = id(mockMvc.perform(post("/api/equipamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "CAR-9",
                                  "nome": "Pá",
                                  "tipo": "CARREGADEIRA",
                                  "quantidadePosicoes": 2,
                                  "status": "ATIVO"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn());
        int pneuId = id(mockMvc.perform(post("/api/pneus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "codigo": "PN-91",
                                  "marca": "Pirelli",
                                  "modelo": "FH",
                                  "medida": "275/80R22.5",
                                  "sulcoMm": 15.0,
                                  "km": 0,
                                  "status": "ESTOQUE"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn());

        mockMvc.perform(get("/api/pneus/estoque"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.codigo == 'PN-91')].codigo").value(Matchers.hasItem("PN-91")));

        mockMvc.perform(post("/api/equipamentos/" + equipamentoId + "/posicoes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pneuId\":" + pneuId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pneus[?(@.codigo == 'PN-91')].posicao").value(Matchers.hasItem(1)));

        mockMvc.perform(get("/api/pneus/estoque"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.codigo == 'PN-91')].codigo").isEmpty());

        mockMvc.perform(delete("/api/equipamentos/" + equipamentoId + "/posicoes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pneus[?(@.codigo == 'PN-91')]").isEmpty());

        mockMvc.perform(get("/api/pneus/" + pneuId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ESTOQUE"))
                .andExpect(jsonPath("$.equipamento").value(Matchers.nullValue()));

        mockMvc.perform(get("/api/movimentacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.pneu.codigo == 'PN-91')].origem").value(Matchers.hasItem("estoque")))
                .andExpect(jsonPath("$[?(@.pneu.codigo == 'PN-91')].destino").value(Matchers.hasItem("CAR-9, posição 1")))
                .andExpect(jsonPath("$[?(@.pneu.codigo == 'PN-91')].destino").value(Matchers.hasItem("estoque")));
    }

    private int id(MvcResult result) throws Exception {
        return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
    }
}
