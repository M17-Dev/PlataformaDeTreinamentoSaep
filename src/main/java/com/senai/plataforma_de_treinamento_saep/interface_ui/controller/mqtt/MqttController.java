package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.mqtt;

import com.rafaelcosta.spring_mqttx.domain.annotation.MqttPayload;
import com.rafaelcosta.spring_mqttx.domain.annotation.MqttPublisher;
import com.rafaelcosta.spring_mqttx.domain.annotation.MqttSubscriber;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.mqtt.ContagemMqttDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.mqtt.LoginMqttDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.reciclagem.UsuarioReciclagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MqttController {

    private final UsuarioReciclagemService reciclagemService;

    // --- 1. RECEBE LOGIN ---
    @MqttSubscriber("tampinhas/login")
    @MqttPublisher("tampinhas/login/resposta") // Já responde automaticamente neste tópico!
    public String validarLogin(@MqttPayload LoginMqttDTO dto) {
        System.out.println("Recebido Login: " + dto.pin());

        boolean valido = reciclagemService.validarLogin(dto.pin(), dto.senha());

        return valido ? "OK" : "ERRO"; // O que retornar aqui vai para o tópico de resposta
    }

    // --- 2. RECEBE CONTAGEM ---
    @MqttSubscriber("tampinhas/contagem")
    @MqttPublisher("tampinhas/contagem/resposta")
    public String receberContagem(@MqttPayload ContagemMqttDTO dto) {
        System.out.println("Recebido Contagem: " + dto.tampas() + " tampas");

        try {
            reciclagemService.adicionarTampinhas(dto.pin(), dto.tampas());
            return "OK";
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return "ERRO";
        }
    }
}