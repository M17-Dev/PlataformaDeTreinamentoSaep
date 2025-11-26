package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.reciclagem;

import com.rafaelcosta.spring_mqttx.domain.annotation.MqttPayload;
import com.rafaelcosta.spring_mqttx.domain.annotation.MqttSubscriber;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.reciclagem.ReciclagemMqttDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.reciclagem.UsuarioTampinhaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Componente que atua como um Listener para mensagens MQTT.
 * Ele receberá o payload JSON enviado pelo dispositivo Wokwi.
 */
@Component
@RequiredArgsConstructor
public class MqttReciclagemController {

    private final UsuarioTampinhaService tampinhaService;

    
    @MqttSubscriber("tampinhas/login")
    public void receberDadosReciclagem(@MqttPayload ReciclagemMqttDTO dadosReciclagem) {
        try {
            System.out.println("MQTT Recebido no Tópico 'tampinhas/login': " + dadosReciclagem);

            tampinhaService.adicionarTampinhas(
                    dadosReciclagem.pin(),
                    dadosReciclagem.quantidadeTampinhas()
            );

        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem MQTT: " + e.getMessage());
        }
    }
}
