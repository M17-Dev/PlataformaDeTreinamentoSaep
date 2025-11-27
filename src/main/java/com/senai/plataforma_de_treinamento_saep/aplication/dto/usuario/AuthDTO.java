package com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario;

public class AuthDTO {
    public record LoginRequest(String cpf, String senha) {}

    public record TokenResponse(String token) {}

    public record AuthResponse(String accessToken, String refreshToken) {}

    public record RefreshRequest(String refreshToken) {}

    public record UserResponse(Long id, String nome, String cpf, String role) {}
}