package com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import com.senai.plataforma_de_treinamento_saep.domain.enums.NivelDeDificuldade;

import java.util.List;
import java.util.stream.Collectors;

public record QuestaoDTO(
        Long id,
        String titulo,
        String introducao,
        String pergunta,
        String imagem,
        Long professorId,
        List<RespostaDTO> respostas,
        Long unidadeCurricularId,
        NivelDeDificuldade nivelDeDificuldade,
        boolean status
) {

    public static QuestaoDTO toDTO(Questao questao) {
        Long profId = (questao.getProfessorId() != null) ? questao.getProfessorId().getId() : null;
        Long ucId = (questao.getUnidadeCurricular() != null) ? questao.getUnidadeCurricular().getId() : null;

        List<RespostaDTO> respostasDTO = questao.getRespostas().stream()
                .map(resposta -> new RespostaDTO(
                        resposta.getId(),
                        resposta.getTexto(),
                        resposta.isCertoOuErrado()))
                .collect(Collectors.toList());

        return new QuestaoDTO(
                questao.getId(),
                questao.getTitulo(),
                questao.getIntroducao(),
                questao.getPergunta(),
                questao.getImagem(),
                profId,
                respostasDTO,
                ucId,
                questao.getNivelDeDificuldade(),
                questao.isStatus()
        );
    }

    public Questao fromDTO() {
        Questao questao = new Questao();
        questao.setTitulo(this.titulo);
        questao.setIntroducao(this.introducao);
        questao.setPergunta(this.pergunta);
        questao.setImagem(this.imagem);
        questao.setNivelDeDificuldade(this.nivelDeDificuldade);
        questao.setStatus(true);
        return questao;
    }
}