package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.atividade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.QuestaoDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.RespostaDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.atividade.QuestaoService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import com.senai.plataforma_de_treinamento_saep.domain.enums.NivelDeDificuldade;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.UsuarioRepository;
import com.senai.plataforma_de_treinamento_saep.interface_ui.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class QuestaoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuestaoService questaoService;

    @Mock
    private UsuarioRepository usuarioRepo;

    @InjectMocks
    private QuestaoController questaoController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(questaoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private QuestaoDTO criarDTO() {
        return new QuestaoDTO(
                null, "Intro", "Pergunta", "img", 1L, List.of(new RespostaDTO(null, "R", null, true)), 1L, NivelDeDificuldade.MEDIO, true
        );
    }

    @Test
    @DisplayName("POST /api/questao - Deve cadastrar (Simulando Usuário Logado)")
    void deveCadastrarQuestao() throws Exception {
        Authentication authMock = mock(Authentication.class);
        when(authMock.getName()).thenReturn("123");

        Usuario usuarioMock = new Professor();
        usuarioMock.setId(10L);
        usuarioMock.setCpf("123");

        when(usuarioRepo.findByCpf("123")).thenReturn(Optional.of(usuarioMock));

        QuestaoDTO dto = criarDTO();
        QuestaoDTO dtoSalvo = new QuestaoDTO(100L, "Intro", "Pergunta", "img", 10L, Collections.emptyList(), 1L, NivelDeDificuldade.MEDIO, true);

        when(questaoService.cadastrarQuestao(any(), eq(10L))).thenReturn(dtoSalvo);

        mockMvc.perform(post("/api/questao")
                        .principal(authMock)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    @DisplayName("GET /api/questao - Deve listar")
    void deveListarQuestoes() throws Exception {
        QuestaoDTO dto = criarDTO();
        when(questaoService.listarQuestoesAtivas()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/questao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].introducao").value("Intro"));
    }

    @Test
    @DisplayName("PUT /api/questao/{id} - Deve atualizar")
    void deveAtualizarQuestao() throws Exception {
        Authentication authMock = mock(Authentication.class);
        when(authMock.getName()).thenReturn("123");

        Usuario usuarioMock = new Professor();
        usuarioMock.setId(10L);
        usuarioMock.setCpf("123");

        when(usuarioRepo.findByCpf("123")).thenReturn(Optional.of(usuarioMock));

        QuestaoDTO dto = criarDTO();
        when(questaoService.atualizarQuestao(eq(1L), any(), eq(10L))).thenReturn(dto);

        mockMvc.perform(put("/api/questao/{id}", 1L)
                        .principal(authMock)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/questao/{id} - Deve inativar")
    void deveInativarQuestao() throws Exception {
        when(questaoService.inativarQuestao(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/questao/{id}", 1L))
                .andExpect(status().isOk());
    }
}