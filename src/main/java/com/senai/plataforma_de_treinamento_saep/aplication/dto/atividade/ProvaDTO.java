package com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Prova;
import com.senai.plataforma_de_treinamento_saep.domain.enums.NivelDeDificuldade;

import java.util.Date;
import java.util.List;

public class ProvaDTO {

    public record ProvaRequestDTO(
        String descricao,
        Date dataProva,
        List<Long> alunoIds,
        Long unidadeCurricularId,
        NivelDeDificuldade nivelDeDificuldade
    ){
        public Prova toEntity(){
            return Prova.builder()
                    .descricao(this.descricao)
                    .dataProva(this.dataProva)
                    .qtdQuestoes(0)
                    .qtdAcertos(0)
                    .nivelDeDificuldade(this.nivelDeDificuldade)
                    .status(true)
                    .build();
        }
    }

    public record ProvaResponseDTO(
        Long idProva,
        String descricao,
        Date dataProva,
        List<Long> alunoIds,
        Long unidadeCurricularId,
        String nomeUnidadeCurricular,
        int qtdQuestoes,
        int qtdAcertos,
        NivelDeDificuldade nivelDeDificuldade,
        boolean status,
        List<QuestaoDTO> questoesDaProva
    ){}
}