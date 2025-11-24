package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.AuthDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.AuthService;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthService auth;

    private final UsuarioRepository usuarios;

    @PostMapping("/login")
    public ResponseEntity<AuthDTO.AuthResponse> login(@RequestBody AuthDTO.LoginRequest req) {
        var tokens = auth.login(req);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthDTO.AuthResponse> refresh(@RequestBody AuthDTO.RefreshRequest req) {
        var newToken = auth.refresh(req.refreshToken());
        return ResponseEntity.ok(newToken);
    }

    @GetMapping("/me")
    public AuthDTO.UserResponse me(Authentication auth) {
        var usuario = usuarios.findByCpf(auth.getName())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
        return new AuthDTO.UserResponse(usuario.getNome(), usuario.getCpf(), usuario.getTipoDeUsuario().name());
    }
}
