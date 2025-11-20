package com.senai.plataforma_de_treinamento_saep.aplication.service.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.AuthDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.UsuarioRepository;
import com.senai.plataforma_de_treinamento_saep.infrastructure.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest req) {
        Usuario usuario = usuarios.findByCpf(req.cpf())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));

        if (!encoder.matches(req.senha(), usuario.getSenha())) {
            throw new BadCredentialsException("Credenciais inválidas");
        }

        String accessToken = jwt.generateAccessToken(usuario.getCpf(), usuario.getTipoDeUsuario().name());
        String refreshToken = jwt.generateRefreshToken(usuario.getCpf());

        return new AuthDTO.AuthResponse(accessToken, refreshToken);
    }

    public AuthDTO.AuthResponse refresh(String refreshToken) {
        if (!jwt.isValid(refreshToken)) {
            throw new BadCredentialsException("Refresh token inválido ou expirado");
        }

        String cpf = jwt.extractCpf(refreshToken);
        Usuario usuario = usuarios.findByCpf(cpf)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));

        String newAccess = jwt.generateAccessToken(usuario.getCpf(), usuario.getTipoDeUsuario().name());
        String newRefresh = jwt.generateRefreshToken(cpf);

        return new AuthDTO.AuthResponse(newAccess, newRefresh);
    }
}
