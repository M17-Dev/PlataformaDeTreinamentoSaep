package com.senai.plataforma_de_treinamento_saep.aplication.dto.mqtt;

// Usa o 'record' (Java 16+) para criar DTOs imutáveis e concisos
public record PayloadLogin(
        String pin,
        String senha
) {}