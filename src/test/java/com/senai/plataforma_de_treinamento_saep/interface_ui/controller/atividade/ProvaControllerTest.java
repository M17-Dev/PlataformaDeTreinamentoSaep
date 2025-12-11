package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.atividade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.ProvaDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.atividade.ProvaService;
import com.senai.plataforma_de_treinamento_saep.domain.enums.NivelDeDificuldade;
import com.senai.plataforma_de_treinamento_saep.interface_ui.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProvaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProvaService provaService;

    @InjectMocks
    private ProvaController provaController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(provaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private ProvaDTO.ProvaResponseDTO criarResponse(Long id) {
        return new ProvaDTO.ProvaResponseDTO(
                id, "Prova Teste", LocalDate.now(), LocalDate.now(), Collections.emptyList(), 1L, "UC", 5, NivelDeDificuldade.MEDIO, Collections.emptyList(), true
        );
    }

    @Test
    @DisplayName("POST /api/prova - Deve cadastrar")
    void deveCadastrarProva() throws Exception {
        ProvaDTO.ProvaRequestDTO req = new ProvaDTO.ProvaRequestDTO(
                "Nova Prova", null, 1L, NivelDeDificuldade.MEDIO, Collections.emptyList()
        );

        ProvaDTO.ProvaResponseDTO res = criarResponse(1L);

        when(provaService.cadastrarProva(any())).thenReturn(res);

        mockMvc.perform(post("/api/prova")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProva").value(1));
    }

    @Test
    @DisplayName("GET /api/prova - Deve listar ativas")
    void deveListarAtivas() throws Exception {
        when(provaService.listarProvasAtivas()).thenReturn(List.of(criarResponse(1L)));

        mockMvc.perform(get("/api/prova"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descricao").value("Prova Teste"));
    }

    @Test
    @DisplayName("POST /api/prova/{id}/questoes/adicionar - Deve adicionar questão")
    void deveAdicionarQuestao() throws Exception {
        Long idProva = 1L;
        ProvaDTO.AdicionarQuestaoProvaDTO req = new ProvaDTO.AdicionarQuestaoProvaDTO(100L);

        when(provaService.adicionarQuestaoNaProva(eq(idProva), eq(100L))).thenReturn(criarResponse(idProva));

        mockMvc.perform(post("/api/prova/{id}/questoes/adicionar", idProva)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/prova/{id}/questoes/substituir - Deve substituir questão")
    void deveSubstituirQuestao() throws Exception {
        Long idProva = 1L;
        ProvaDTO.SubstituirQuestaoProvaDTO req = new ProvaDTO.SubstituirQuestaoProvaDTO(100L, 200L);

        when(provaService.substituirQuestao(eq(idProva), eq(100L), eq(200L)))
                .thenReturn(criarResponse(idProva));

        mockMvc.perform(put("/api/prova/{id}/questoes/substituir", idProva)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/prova/{id}/inativar - Deve inativar")
    void deveInativarProva() throws Exception {
        when(provaService.desativarProva(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/prova/{id}/inativar", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/prova/curso/{id} - Deve listar por curso")
    void deveListarPorCurso() throws Exception {
        when(provaService.listarProvasPeloIdDoCurso(1L)).thenReturn(List.of(criarResponse(1L)));

        mockMvc.perform(get("/api/prova/curso/{id}", 1L))
                .andExpect(status().isOk());
    }
}