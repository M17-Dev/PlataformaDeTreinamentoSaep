package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.usuario;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.ProfessorDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.RetornoCriacaoUsuarioDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioUpdateDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.ProfessorService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProfessorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProfessorService professorService;

    @InjectMocks
    private ProfessorController professorController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(professorController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/professor - Deve cadastrar")
    void deveCadastrarProfessor() throws Exception {
        ProfessorDTO dto = new ProfessorDTO(null, "Prof", "111", "email", null, true);
        RetornoCriacaoUsuarioDTO<ProfessorDTO> retorno = new RetornoCriacaoUsuarioDTO<>(dto, "senha123");

        when(professorService.cadastrarProfessor(any())).thenReturn(retorno);

        mockMvc.perform(post("/api/professor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.senhaProvisoria").value("senha123"));
    }

    @Test
    @DisplayName("GET /api/professor - Deve listar")
    void deveListarProfessores() throws Exception {
        ProfessorDTO dto = new ProfessorDTO(1L, "Prof", "111", "email", null, true);
        when(professorService.listarProfessoresAtivos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/professor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Prof"));
    }

    @Test
    @DisplayName("PUT /api/professor/{id} - Deve atualizar")
    void deveAtualizarProfessor() throws Exception {
        UsuarioUpdateDTO updateDTO = new UsuarioUpdateDTO("Nome Novo", "email", null);
        ProfessorDTO dtoRetorno = new ProfessorDTO(1L, "Nome Novo", "111", "email", null, true);

        when(professorService.atualizarProfessor(eq(1L), any())).thenReturn(dtoRetorno);

        mockMvc.perform(put("/api/professor/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nome Novo"));
    }

    @Test
    @DisplayName("DELETE /api/professor/{id} - Deve inativar")
    void deveInativarProfessor() throws Exception {
        when(professorService.inativarProfessor(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/professor/{id}", 1L))
                .andExpect(status().isOk());
    }
}