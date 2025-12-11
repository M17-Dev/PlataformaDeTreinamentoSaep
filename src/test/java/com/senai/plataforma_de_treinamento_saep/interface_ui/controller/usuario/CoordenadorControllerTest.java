package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.usuario;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.CoordenadorDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.RetornoCriacaoUsuarioDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioUpdateDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.CoordenadorService;
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
class CoordenadorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CoordenadorService coordenadorService;

    @InjectMocks
    private CoordenadorController coordenadorController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(coordenadorController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/coordenador - Deve cadastrar (Somente ADMIN na prática)")
    void deveCadastrarCoordenador() throws Exception {
        CoordenadorDTO dto = new CoordenadorDTO(null, "Coord", "111", "email", null, true);

        RetornoCriacaoUsuarioDTO<CoordenadorDTO> retorno = new RetornoCriacaoUsuarioDTO<>(dto, "senha123");

        when(coordenadorService.cadastrarCoordenador(any())).thenReturn(retorno);

        mockMvc.perform(post("/api/coordenador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.senhaProvisoria").value("senha123"));
    }

    @Test
    @DisplayName("GET /api/coordenador - Deve listar todos")
    void deveListarCoordenadores() throws Exception {
        CoordenadorDTO dto = new CoordenadorDTO(1L, "Coord", "111", "email", null, true);

        when(coordenadorService.listarCoordenadoresAtivos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/coordenador"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Coord"));
    }

    @Test
    @DisplayName("PUT /api/coordenador/{id} - Deve atualizar")
    void deveAtualizarCoordenador() throws Exception {
        UsuarioUpdateDTO updateDTO = new UsuarioUpdateDTO("Novo Nome", "novo@email", null);
        CoordenadorDTO dtoAtualizado = new CoordenadorDTO(1L, "Novo Nome", "111", "novo@email", null, true);

        when(coordenadorService.atualizarCoordenador(eq(1L), any())).thenReturn(dtoAtualizado);

        mockMvc.perform(put("/api/coordenador/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo Nome"));
    }

    @Test
    @DisplayName("DELETE /api/coordenador/{id} - Deve inativar")
    void deveInativarCoordenador() throws Exception {
        when(coordenadorService.inativarCoordenador(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/coordenador/{id}", 1L))
                .andExpect(status().isOk());
    }
}