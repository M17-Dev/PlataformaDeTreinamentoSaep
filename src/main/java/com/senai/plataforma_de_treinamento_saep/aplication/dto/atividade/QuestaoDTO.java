package com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Resposta;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;

import java.util.Collections;
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
        List<Long> unidadeCurricularIds,
        boolean status
) {

    public static QuestaoDTO toDTO(Questao questao) {
        Long profId = (questao.getProfessorID() != null) ? questao.getProfessorID().getId() : null;

        List<RespostaDTO> respostasDTO = questao.getRespostas().stream()
                .map(resposta -> new RespostaDTO(
                        resposta.getId(),
                        resposta.getTexto(),
                        resposta.isCertoOuErrado()))
                .collect(Collectors.toList());

        List<Long> ucIds = questao.getUnidadeCurriculares().stream()
                .map(UnidadeCurricular::getId)
                .collect(Collectors.toList());

        return new QuestaoDTO(
                questao.getId(),
                questao.getTitulo(),
                questao.getIntroducao(),
                questao.getPergunta(),
                questao.getImagem(),
                profId,
                respostasDTO,
                ucIds,
                questao.isStatus()
        );
    }

    public Questao fromDTO() {
        Questao questao = new Questao();
        questao.setTitulo(this.titulo);
        questao.setIntroducao(this.introducao);
        questao.setPergunta(this.pergunta);
        questao.setImagem(this.imagem);

        if (this.respostas != null) {
            List<Resposta> novasRespostas = this.respostas.stream()
                    .map(dto -> {
                        Resposta resposta = new Resposta();
                        resposta.setTexto(dto.texto());
                        resposta.setCertoOuErrado(dto.certaOuErrada());
                        resposta.setQuestao(questao);
                        return resposta;
                    })
                    .collect(Collectors.toList());
            questao.setRespostas(novasRespostas);
        } else {
            questao.setRespostas(Collections.emptyList());
        }

        if (this.unidadeCurricularIds != null) {
            List<UnidadeCurricular> novasUnidadesCurriculares = this.unidadeCurricularIds.stream()
                    .map(dto -> {
                        UnidadeCurricular unidadeCurricular = new UnidadeCurricular();
                        unidadeCurricular.setNome(unidadeCurricular.getNome());
                        unidadeCurricular.setCurso(unidadeCurricular.getCurso());
                        unidadeCurricular.setQuestoes(unidadeCurricular.getQuestoes());
                        return unidadeCurricular;
                    })
                    .collect(Collectors.toList());
            questao.setUnidadeCurriculares(novasUnidadesCurriculares);
        } else {
            questao.setRespostas(Collections.emptyList());
        }

        questao.setStatus(true);
        return questao;
    }
}