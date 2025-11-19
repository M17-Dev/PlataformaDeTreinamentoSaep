package com.senai.plataforma_de_treinamento_saep.infrastructure.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGateway {
    // Função que vamos chamar para responder o ESP32
    void enviarResposta(String payload, @Header(MqttHeaders.TOPIC) String topico);
}