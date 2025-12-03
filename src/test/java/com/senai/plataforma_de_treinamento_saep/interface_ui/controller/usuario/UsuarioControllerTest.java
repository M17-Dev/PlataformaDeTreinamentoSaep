package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.usuario;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.RetornoCriacaoUsuarioDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioUpdateDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.UsuarioService;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/usuario - Deve cadastrar e retornar 201 Created")
    void deveCadastrarUsuario() throws Exception {
        UsuarioDTO dto = new UsuarioDTO(null, "Teste", "123", "email", null, TipoDeUsuario.ALUNO, true);

        RetornoCriacaoUsuarioDTO<UsuarioDTO> retorno = new RetornoCriacaoUsuarioDTO<>(dto, "senha123");
        when(usuarioService.cadastrarUsuario(any(UsuarioDTO.class))).thenReturn(retorno);

        mockMvc.perform(post("/api/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated()) // Espera 201
                .andExpect(jsonPath("$.senhaProvisoria").value("senha123"));
    }

    @Test
    @DisplayName("POST /api/usuario - Deve retornar 400 quando Service lança RegraDeNegocioException")
    void deveTratarErroRegraDeNegocio() throws Exception {
        UsuarioDTO dto = new UsuarioDTO(null, "Teste", "123", "email", null, TipoDeUsuario.ALUNO, true);

        when(usuarioService.cadastrarUsuario(any())).thenThrow(new RegraDeNegocioException("CPF duplicado"));

        mockMvc.perform(post("/api/usuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()) // Espera 400
                .andExpect(jsonPath("$.title").value("Violação de Regra de Negócio"))
                .andExpect(jsonPath("$.detail").value("CPF duplicado"));
    }

    @Test
    @DisplayName("GET /api/usuario - Deve retornar lista 200 OK")
    void deveListarUsuarios() throws Exception {
        when(usuarioService.listarUsuariosAtivos()).thenReturn(List.of(new UsuarioDTO(1L, "Ana", "123", "email", null, TipoDeUsuario.ALUNO, true)));

        mockMvc.perform(get("/api/usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Ana"));
    }

    @Test
    @DisplayName("GET /api/usuario/{id} - Deve retornar 200 OK se existir")
    void deveBuscarPorIdSucesso() throws Exception {
        UsuarioDTO dto = new UsuarioDTO(1L, "Ana", "123", "email", null, TipoDeUsuario.ALUNO, true);
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/usuario/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana"));
    }

    @Test
    @DisplayName("GET /api/usuario/{id} - Deve retornar 404 se não existir")
    void deveBuscarPorIdNaoEncontrado() throws Exception {
        when(usuarioService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuario/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/usuario/{id} - Deve atualizar e retornar 200 OK")
    void deveAtualizarUsuario() throws Exception {
        UsuarioUpdateDTO updateDto = new UsuarioUpdateDTO("Nome Novo", "email", "senha");
        UsuarioDTO dtoRetorno = new UsuarioDTO(1L, "Nome Novo", "123", "email", null, TipoDeUsuario.ALUNO, true);

        when(usuarioService.atualizarUsuario(eq(1L), any())).thenReturn(dtoRetorno);

        mockMvc.perform(put("/api/usuario/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nome Novo"));
    }

    @Test
    @DisplayName("DELETE /api/usuario/{id} - Deve inativar e retornar 200 OK")
    void deveInativarUsuarioSucesso() throws Exception {
        when(usuarioService.inativarUsuario(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/usuario/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/usuario/{id} - Deve retornar 404 se falhar")
    void deveInativarUsuarioFalha() throws Exception {
        when(usuarioService.inativarUsuario(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/usuario/{id}", 99L))
                .andExpect(status().isNotFound());
    }
}