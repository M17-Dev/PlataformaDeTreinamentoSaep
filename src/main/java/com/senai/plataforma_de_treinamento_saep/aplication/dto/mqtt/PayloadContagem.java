package com.senai.plataforma_de_treinamento_saep.aplication.dto.mqtt;

public record PayloadContagem(
        String pin, // O PIN é necessário para saber qual conta atualizar
        int tampas // A quantidade de tampas recicladas
) {}