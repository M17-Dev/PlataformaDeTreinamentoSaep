package com.senai.plataforma_de_treinamento_saep.aplication.dto.escolar;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;

import java.util.List;

public record UnidadeCurricularDTO(
        Long id,
        String nome,
        String fraseUc,
        Long cursoId,
        String nomeCurso,
        List<Long> questoesId,
        boolean status
) {

    public static UnidadeCurricularDTO toDTO(UnidadeCurricular unidadeCurricular) {
        Long idDoCurso = (unidadeCurricular.getCurso() != null) ? unidadeCurricular.getCurso().getId() : null;
        String nomeDoCurso = (unidadeCurricular.getCurso() != null) ? unidadeCurricular.getCurso().getNome() : null;

        List<Long> questoesId = unidadeCurricular.getQuestoes().stream()
                .map(Questao::getId)
                .toList();

        return new UnidadeCurricularDTO(
                unidadeCurricular.getId(),
                unidadeCurricular.getNome(),
                unidadeCurricular.getFraseDaUc(),
                idDoCurso,
                nomeDoCurso,
                questoesId,
                unidadeCurricular.isStatus()
        );
    }

    public UnidadeCurricular fromDTO() {
        UnidadeCurricular unidadeCurricular = new UnidadeCurricular();
        unidadeCurricular.setNome(nome);
        unidadeCurricular.setStatus(true);
        return unidadeCurricular;
    }
}