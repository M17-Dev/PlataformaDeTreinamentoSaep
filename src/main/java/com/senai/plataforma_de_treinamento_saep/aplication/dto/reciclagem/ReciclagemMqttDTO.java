package com.senai.plataforma_de_treinamento_saep.aplication.dto.reciclagem;

import lombok.Builder;

/**
 * DTO que representa o payload JSON esperado do dispositivo Wokwi (IoT).
 */
@Builder
public record ReciclagemMqttDTO(
        // PIN do usuário para identificar a quem adicionar as tampinhas
        String pin,
        // Quantidade de tampinhas detectadas pelo Wokwi
        Integer quantidadeTampinhas
) {
    // Adicione validações ou lógica de conversão se necessário
}