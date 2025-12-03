package com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import com.senai.plataforma_de_treinamento_saep.domain.enums.NivelDeDificuldade;

import java.util.List;

public record QuestaoDTO(
        Long id,
        String introducao,
        String pergunta,
        String imagem,
        Long usuarioId,
        List<RespostaDTO> respostas,
        Long unidadeCurricularId,
        NivelDeDificuldade nivelDeDificuldade,
        boolean status
) {

    public static QuestaoDTO toDTO(Questao questao) {
        Long usuarioId = (questao.getUsuario() != null) ? questao.getUsuario().getId() : null;
        Long ucId = (questao.getUnidadeCurricular() != null) ? questao.getUnidadeCurricular().getId() : null;

        List<RespostaDTO> respostasDTO = questao.getRespostas().stream()
                .map(RespostaDTO::toDTO)
                .toList();

        return new QuestaoDTO(
                questao.getId(),
                tratarString(questao.getIntroducao()),
                tratarString(questao.getPergunta()),
                tratarString(questao.getImagem()),
                usuarioId,
                respostasDTO,
                ucId,
                questao.getNivelDeDificuldade(),
                questao.isStatus()
        );
    }

    public static QuestaoDTO toDTO(Questao questao, List<RespostaDTO> respostasPersonalizadas) {
        Long usuarioId = (questao.getUsuario() != null) ? questao.getUsuario().getId() : null;
        Long ucId = (questao.getUnidadeCurricular() != null) ? questao.getUnidadeCurricular().getId() : null;

        return new QuestaoDTO(
                questao.getId(),
                tratarString(questao.getIntroducao()),
                tratarString(questao.getPergunta()),
                tratarString(questao.getImagem()),
                usuarioId,
                respostasPersonalizadas,
                ucId,
                questao.getNivelDeDificuldade(),
                questao.isStatus()
        );
    }

    public Questao fromDTO() {
        Questao questao = new Questao();
        questao.setIntroducao(tratarString(this.introducao));
        questao.setPergunta(tratarString(this.pergunta));
        questao.setImagem(tratarString(this.imagem));
        questao.setNivelDeDificuldade(this.nivelDeDificuldade);
        questao.setStatus(true);
        return questao;
    }

    private static String tratarString(String texto) {
        return texto != null ? texto : "";
    }
}