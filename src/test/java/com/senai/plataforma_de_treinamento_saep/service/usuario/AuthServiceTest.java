package com.senai.plataforma_de_treinamento_saep.service.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.AuthDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.AuthService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.UsuarioRepository;
import com.senai.plataforma_de_treinamento_saep.infrastructure.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Test
    @DisplayName("Deve realizar login com sucesso e retornar tokens")
    void deveLogarComSucesso() {
        // Cenario
        String cpf = "123.456.789-00";
        String senha = "senha123";
        AuthDTO.LoginRequest request = new AuthDTO.LoginRequest(cpf, senha);

        Usuario usuarioMock = new Aluno();
        usuarioMock.setId(1L);
        usuarioMock.setCpf(cpf);
        usuarioMock.setSenha("senhaCodificada");
        usuarioMock.setTipoDeUsuario(TipoDeUsuario.ALUNO);

        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches(senha, usuarioMock.getSenha())).thenReturn(true);
        when(jwtService.generateAccessToken(any(), any(), any())).thenReturn("token_acesso_mock");
        when(jwtService.generateRefreshToken(any())).thenReturn("token_refresh_mock");

        // Execução
        AuthDTO.AuthResponse response = authService.login(request);

        // Verificação
        assertNotNull(response);
        assertEquals("token_acesso_mock", response.accessToken());
        assertEquals("token_refresh_mock", response.refreshToken());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não existe")
    void deveFalharUsuarioNaoEncontrado() {
        AuthDTO.LoginRequest request = new AuthDTO.LoginRequest("000.000.000-00", "123");

        when(usuarioRepository.findByCpf(any())).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha está incorreta")
    void deveFalharSenhaIncorreta() {
        String cpf = "123.456.789-00";
        AuthDTO.LoginRequest request = new AuthDTO.LoginRequest(cpf, "senhaErrada");

        Usuario usuarioMock = new Aluno();
        usuarioMock.setCpf(cpf);
        usuarioMock.setSenha("senhaCertaCodificada");

        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Deve atualizar token com refresh token válido")
    void deveAtualizarTokenComSucesso() {
        String refreshToken = "refresh_valid";
        String cpf = "123.456.789-00";

        Aluno alunoMock = new Aluno();
        alunoMock.setId(1L);
        alunoMock.setCpf(cpf);
        alunoMock.setTipoDeUsuario(TipoDeUsuario.ALUNO);

        when(jwtService.isValid(refreshToken)).thenReturn(true);
        when(jwtService.extractCpf(refreshToken)).thenReturn(cpf);
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(alunoMock));
        when(jwtService.generateAccessToken(any(), any(), any())).thenReturn("novo_access");
        when(jwtService.generateRefreshToken(any())).thenReturn("novo_refresh");

        AuthDTO.AuthResponse response = authService.refresh(refreshToken);

        assertNotNull(response);
        assertEquals("novo_access", response.accessToken());
    }
}
