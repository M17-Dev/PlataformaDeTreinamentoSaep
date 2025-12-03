package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.usuario;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.AuthDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.AuthService;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.UsuarioRepository;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // StandaloneSetup permite testar o controller isolado dos filtros de segurança (JwtFilter)
        // Isso evita erro 403 Forbidden durante testes unitários simples
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve retornar 200 e tokens")
    void deveRetornar200NoLogin() throws Exception {
        AuthDTO.LoginRequest loginRequest = new AuthDTO.LoginRequest("123", "senha");
        AuthDTO.AuthResponse authResponse = new AuthDTO.AuthResponse("tokenA", "tokenB");

        when(authService.login(any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("tokenA"))
                .andExpect(jsonPath("$.refreshToken").value("tokenB"));
    }
}