package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.escolar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.escolar.CursoDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.escolar.CursoService;
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
class CursoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CursoService cursoService;

    @InjectMocks
    private CursoController cursoController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cursoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/curso - Deve cadastrar")
    void deveCadastrarCurso() throws Exception {
        CursoDTO dto = new CursoDTO(null, "Eletrotécnica", Collections.emptyList(), Collections.emptyList(), true);
        CursoDTO dtoSalvo = new CursoDTO(1L, "Eletrotécnica", Collections.emptyList(), Collections.emptyList(), true);

        when(cursoService.cadastrarCurso(any(CursoDTO.class))).thenReturn(dtoSalvo);

        mockMvc.perform(post("/api/curso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Eletrotécnica"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/curso - Deve listar")
    void deveListarCursos() throws Exception {
        CursoDTO dto = new CursoDTO(1L, "Mecânica", Collections.emptyList(), Collections.emptyList(), true);

        when(cursoService.listarCursosAtivos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/curso"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Mecânica"));
    }

    @Test
    @DisplayName("GET /api/curso/{id} - Deve buscar por ID")
    void deveBuscarPorId() throws Exception {
        CursoDTO dto = new CursoDTO(1L, "TI", Collections.emptyList(), Collections.emptyList(), true);

        when(cursoService.buscarPorId(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/curso/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("TI"));
    }

    @Test
    @DisplayName("GET /api/curso/{id} - Deve retornar 404 se não achar")
    void deveRetornar404() throws Exception {
        when(cursoService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/curso/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/curso/{id} - Deve atualizar")
    void deveAtualizarCurso() throws Exception {
        CursoDTO dtoInput = new CursoDTO(null, "Novo Nome", null, null, true);
        CursoDTO dtoSaida = new CursoDTO(1L, "Novo Nome", Collections.emptyList(), Collections.emptyList(), true);

        when(cursoService.atualizarCurso(eq(1L), any())).thenReturn(dtoSaida);

        mockMvc.perform(put("/api/curso/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo Nome"));
    }

    @Test
    @DisplayName("DELETE /api/curso/{id} - Deve inativar")
    void deveInativarCurso() throws Exception {
        when(cursoService.inativarCurso(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/curso/{id}", 1L))
                .andExpect(status().isOk());
    }
}