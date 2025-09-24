package com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Resposta;

public record RespostaDTO(
        Long id,
        String texto,
        boolean certaOuErrada
) {

    public static RespostaDTO toDTO(Resposta resposta) {
        return new RespostaDTO(
                resposta.getId(),
                resposta.getTexto(),
                resposta.isCertoOuErrado()
        );
    }

    public Resposta fromDTO() {
        Resposta resposta = new Resposta();
        resposta.setTexto(this.texto);
        resposta.setCertoOuErrado(this.certaOuErrada);
        return resposta;
    }
}