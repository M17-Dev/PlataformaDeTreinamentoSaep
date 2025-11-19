package com.senai.plataforma_de_treinamento_saep.aplication.service.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.mqtt.PayloadContagem;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.mqtt.PayloadLogin;
import com.senai.plataforma_de_treinamento_saep.aplication.service.reciclagem.ContaReciclagemService;
import com.senai.plataforma_de_treinamento_saep.infrastructure.mqtt.MqttGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.integration.mqtt.support.MqttHeaders;

@Service
@RequiredArgsConstructor
public class MqttHandlerService {

    private final ContaReciclagemService reciclagemService;
    private final MqttGateway mqttGateway;
    private final ObjectMapper objectMapper; // Para ler o JSON

    // Este método é acionado automaticamente quando chega mensagem no mqttInputChannel
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<String> message) {
        String payload = message.getPayload();
        String topico = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);

        System.out.println("MQTT Recebido [" + topico + "]: " + payload);

        try {
            if ("tampinhas/login".equals(topico)) {
                processarLogin(payload);
            } else if ("tampinhas/contagem".equals(topico)) {
                processarContagem(payload);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processarLogin(String json) throws Exception {
        PayloadLogin dados = objectMapper.readValue(json, PayloadLogin.class);

        boolean sucesso = reciclagemService.validarLoginMaquina(dados.pin(), dados.senha());

        // Resposta Simples (Se o ESP32 espera apenas a string OK/ERRO)
        String respostaLogin = sucesso ? "OK" : "ERRO";
        mqttGateway.enviarResposta(respostaLogin, "tampinhas/login/resposta");
    }

    private void processarContagem(String json) throws Exception {
        PayloadContagem dados = objectMapper.readValue(json, PayloadContagem.class);

        boolean sucesso = reciclagemService.adicionarTampinhas(dados.pin(), dados.tampas());

        // Responde para o tópico que o ESP32 está ouvindo
        String resposta = sucesso ? "OK" : "ERRO";
        mqttGateway.enviarResposta(resposta, "tampinhas/contagem/resposta");
    }
}