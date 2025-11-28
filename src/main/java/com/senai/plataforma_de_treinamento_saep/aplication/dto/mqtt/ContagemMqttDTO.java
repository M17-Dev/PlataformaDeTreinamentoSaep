package com.senai.plataforma_de_treinamento_saep.aplication.dto.mqtt;

import lombok.Builder;

@Builder
public record ContagemMqttDTO(String pin, int tampas) {}