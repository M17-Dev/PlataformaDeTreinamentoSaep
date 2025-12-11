package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.usuario;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.AlunoDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.RetornoCriacaoUsuarioDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioUpdateDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.AlunoService;
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
class AlunoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AlunoService alunoService;

    @InjectMocks
    private AlunoController alunoController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(alunoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/aluno - Deve cadastrar Aluno (Com Curso e Listas)")
    void deveCadastrarAluno() throws Exception {
        AlunoDTO dtoEntrada = new AlunoDTO(
                null, "Aluno Novo", "000.111.222-33", "aluno@email.com", null,
                1L, Collections.emptyList(), Collections.emptyList(), true
        );

        RetornoCriacaoUsuarioDTO<AlunoDTO> retorno = new RetornoCriacaoUsuarioDTO<>(dtoEntrada, "senha123");

        when(alunoService.cadastrarAluno(any(AlunoDTO.class))).thenReturn(retorno);

        mockMvc.perform(post("/api/aluno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoEntrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.senhaProvisoria").value("senha123"))
                .andExpect(jsonPath("$.usuario.cursoId").value(1))
                .andExpect(jsonPath("$.usuario.nome").value("Aluno Novo"));
    }

    @Test
    @DisplayName("GET /api/aluno - Deve listar alunos")
    void deveListarAlunos() throws Exception {
        AlunoDTO dto = new AlunoDTO(
                1L, "Aluno Listado", "000", "email", null,
                1L, Collections.emptyList(), Collections.emptyList(), true
        );

        when(alunoService.listarAlunosAtivos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/aluno"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Aluno Listado"))
                .andExpect(jsonPath("$[0].cursoId").value(1));
    }

    @Test
    @DisplayName("PUT /api/aluno/{id} - Deve atualizar aluno")
    void deveAtualizarAluno() throws Exception {
        UsuarioUpdateDTO update = new UsuarioUpdateDTO("Nome Editado", "email@novo.com", null);

        AlunoDTO dtoRetorno = new AlunoDTO(
                1L, "Nome Editado", "000", "email@novo.com", null,
                1L, Collections.emptyList(), Collections.emptyList(), true
        );

        when(alunoService.atualizarAluno(eq(1L), any(UsuarioUpdateDTO.class))).thenReturn(dtoRetorno);

        mockMvc.perform(put("/api/aluno/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nome Editado"));
    }

    @Test
    @DisplayName("DELETE /api/aluno/{id} - Deve inativar aluno")
    void deveInativarAluno() throws Exception {
        when(alunoService.inativarAluno(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/aluno/{id}", 1L))
                .andExpect(status().isOk());
    }
}