package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.escolar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.escolar.UnidadeCurricularDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.escolar.UnidadeCurricularService;
import com.senai.plataforma_de_treinamento_saep.domain.exception.RegraDeNegocioException;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UnidadeCurricularControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UnidadeCurricularService ucService;

    @InjectMocks
    private UnidadeCurricularController ucController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ucController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/unidade-curricular - Deve cadastrar")
    void deveCadastrarUC() throws Exception {
        UnidadeCurricularDTO dto = new UnidadeCurricularDTO(
                null, "Banco de Dados", "SQL", 1L, null, Collections.emptyList(), null, true
        );

        UnidadeCurricularDTO dtoSalvo = new UnidadeCurricularDTO(
                1L, "Banco de Dados", "SQL", 1L, "Curso TI", Collections.emptyList(), Collections.emptyList(), true
        );

        when(ucService.cadastrarUnidadeCurricular(any())).thenReturn(dtoSalvo);

        mockMvc.perform(post("/api/unidade-curricular")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Banco de Dados"))
                .andExpect(jsonPath("$.nomeCurso").value("Curso TI"));
    }

    @Test
    @DisplayName("POST /api/unidade-curricular - Deve retornar 400 se CursoId for nulo")
    void deveTratarErroRegraNegocio() throws Exception {
        UnidadeCurricularDTO dto = new UnidadeCurricularDTO(null, "Sem Curso", "", null, null, null, null, true);

        when(ucService.cadastrarUnidadeCurricular(any()))
                .thenThrow(new RegraDeNegocioException("Uma UC deve pertencer a um Curso."));

        mockMvc.perform(post("/api/unidade-curricular")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Uma UC deve pertencer a um Curso."));
    }

    @Test
    @DisplayName("GET /api/unidade-curricular - Deve listar ativas")
    void deveListarUCs() throws Exception {
        UnidadeCurricularDTO dto = new UnidadeCurricularDTO(1L, "Web", "", 1L, "TI", Collections.emptyList(), null, true);

        when(ucService.listarUnidadesCurricularesAtivas()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/unidade-curricular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Web"));
    }

    @Test
    @DisplayName("GET /api/unidade-curricular/curso/{idCurso} - Deve listar por curso")
    void deveListarPorCurso() throws Exception {
        UnidadeCurricularDTO dto = new UnidadeCurricularDTO(1L, "Web", "", 1L, "TI", Collections.emptyList(), null, true);

        when(ucService.listarUnidadesCurricularesDoCurso(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/unidade-curricular/curso/{idCurso}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Web"));
    }

    @Test
    @DisplayName("PUT /api/unidade-curricular/{id} - Deve atualizar")
    void deveAtualizarUC() throws Exception {
        UnidadeCurricularDTO dtoUpdate = new UnidadeCurricularDTO(1L, "Novo Nome", "", 1L, null, null, null, true);
        UnidadeCurricularDTO dtoRetorno = new UnidadeCurricularDTO(1L, "Novo Nome", "", 1L, "TI", Collections.emptyList(), null, true);

        when(ucService.atualizarUnidadeCurricular(eq(1L), any())).thenReturn(dtoRetorno);

        mockMvc.perform(put("/api/unidade-curricular/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo Nome"));
    }

    @Test
    @DisplayName("DELETE /api/unidade-curricular/{id} - Deve inativar")
    void deveInativarUC() throws Exception {
        when(ucService.inativarUnidadeCurricular(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/unidade-curricular/{id}", 1L))
                .andExpect(status().isOk());
    }
}