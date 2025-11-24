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

    /**
     * Assina o tópico 'topico/reciclagem/entrada' e recebe o payload.
     * O tópico deve ser o mesmo que o seu dispositivo no Wokwi está publicando.
     */
    @MqttSubscriber("topico/reciclagem/entrada")
    public void receberDadosReciclagem(@MqttPayload ReciclagemMqttDTO dadosReciclagem) {
        try {
            System.out.println("MQTT Recebido no Tópico 'topico/reciclagem/entrada': " + dadosReciclagem);

            // Chama o serviço para atualizar o saldo do usuário
            tampinhaService.adicionarTampinhas(
                    dadosReciclagem.pin(),
                    dadosReciclagem.quantidadeTampinhas()
            );

        } catch (Exception e) {
            // É importante logar erros no listener MQTT, pois eles não serão visíveis em uma requisição HTTP.
            System.err.println("Erro ao processar mensagem MQTT: " + e.getMessage());
            // Você pode optar por notificar o sistema ou re-tentar dependendo da regra de negócio
        }
    }
}