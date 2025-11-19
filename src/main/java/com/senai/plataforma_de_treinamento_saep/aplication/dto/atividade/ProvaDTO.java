package com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Prova;
import com.senai.plataforma_de_treinamento_saep.domain.enums.NivelDeDificuldade;

import java.time.LocalDate;
import java.util.List;

public class ProvaDTO {

    public record ProvaRequestDTO(
        String descricao,
        List<Long> alunosId,
        Long unidadeCurricularId,
        NivelDeDificuldade nivelDeDificuldade,
        List<Long> questoesId
    ){
        public Prova toEntity(){
            return Prova.builder()
                    .descricao(this.descricao)
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
        LocalDate dataCriacao,
        LocalDate dataUltimaAtualizacao,
        List<Long> alunoIds,
        Long unidadeCurricularId,
        String nomeUnidadeCurricular,
        int qtdQuestoes,
        int qtdAcertos,
        NivelDeDificuldade nivelDeDificuldade,
        List<QuestaoDTO> questoesDaProva,
        boolean status
    ){}

    public record AtualizarProvaDTO(
            String descricao
    ){}

    public record AdicionarQuestaoProvaDTO(
            Long idQuestaoASerAdicionada
    ){}

    public record SubstituirQuestaoProvaDTO(
        Long idQuestaoASerAtualizada,
        Long idNovaQuestao
    ){}
}