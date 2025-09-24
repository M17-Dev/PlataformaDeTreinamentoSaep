package com.senai.plataforma_de_treinamento_saep.aplication.dto.escolar;

import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;

public record UnidadeCurricularDTO(
        Long id,
        String nome,
        Long cursoId,
        String nomeCurso //passa o nome do curso na unidade curricular pra poupar tempo
) {

    public static UnidadeCurricularDTO toDTO(UnidadeCurricular unidadeCurricular) {
        Long idDoCurso = (unidadeCurricular.getCurso() != null) ? unidadeCurricular.getCurso().getId() : null;
        String nomeDoCurso = (unidadeCurricular.getCurso() != null) ? unidadeCurricular.getCurso().getNome() : null;

        return new UnidadeCurricularDTO(
                unidadeCurricular.getId(),
                unidadeCurricular.getNome(),
                idDoCurso,
                nomeDoCurso
        );
    }

    public UnidadeCurricular fromDTO() {
        UnidadeCurricular unidadeCurricular = new UnidadeCurricular();
        unidadeCurricular.setNome(this.nome);
        return unidadeCurricular;
    }
}