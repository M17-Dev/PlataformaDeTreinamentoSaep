package com.senai.plataforma_de_treinamento_saep.aplication.dto.escolar;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.AlunoDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.Curso;

import java.util.List;

public record CursoDTO(
        Long id,
        String nome,
        List<UnidadeCurricularDTO> unidadesCurriculares,
        List<AlunoDTO> alunos,
        boolean status
) {

    public static CursoDTO toDTO(Curso curso) {
        List<UnidadeCurricularDTO> listaUnidadesCurriculares = curso.getUnidadesCurriculares().stream()
                .map(UnidadeCurricularDTO::toDTO)
                .toList();

        List<AlunoDTO> listaAlunos = curso.getAlunos().stream()
                .map(AlunoDTO::toDTO)
                .toList();

        return new CursoDTO(
                curso.getId(),
                curso.getNome(),
                listaUnidadesCurriculares,
                listaAlunos,
                curso.isStatus()
        );
    }

    public Curso fromDTO() {
        Curso curso = new Curso();
        curso.setNome(this.nome);
        curso.setStatus(true);
        return curso;
    }
}