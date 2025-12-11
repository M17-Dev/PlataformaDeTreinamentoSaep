package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.atividade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.RealizacaoProvaDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.atividade.ProvaRealizacaoService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.AlunoRepository;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RealizarProvaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProvaRealizacaoService service;

    @Mock
    private AlunoRepository alunoRepo;

    @InjectMocks
    private ProvaRealizadaController controller;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/prova-realizada/{id}/entregar - Deve entregar prova")
    void deveEntregarProva() throws Exception {
        Authentication authMock = mock(Authentication.class);
        when(authMock.getName()).thenReturn("123.456.789-00");

        Aluno alunoLogado = new Aluno();
        alunoLogado.setId(10L);
        alunoLogado.setCpf("123.456.789-00");

        when(alunoRepo.findByCpf("123.456.789-00")).thenReturn(Optional.of(alunoLogado));

        RealizacaoProvaDTO.EntregaProvaDTO dtoInput = new RealizacaoProvaDTO.EntregaProvaDTO(
                10L, Collections.emptyList()
        );

        RealizacaoProvaDTO.ResultadoProvaDTO resultado = new RealizacaoProvaDTO.ResultadoProvaDTO(
                500L, 10, 8, "Parabéns"
        );

        when(service.entregaProva(eq(1L), eq(10L), any())).thenReturn(resultado);

        mockMvc.perform(post("/api/prova-realizada/{id}/entregar", 1L)
                        .principal(authMock)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acertos").value(8))
                .andExpect(jsonPath("$.mensagem").value("Parabéns"));
    }
}