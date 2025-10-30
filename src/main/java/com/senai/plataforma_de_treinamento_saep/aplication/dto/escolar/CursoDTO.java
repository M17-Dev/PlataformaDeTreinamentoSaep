package com.senai.plataforma_de_treinamento_saep.aplication.dto.escolar;

import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.Curso;

public record CursoDTO(
        Long id,
        String nome,
        boolean status
) {

    public static CursoDTO toDTO(Curso curso) {
        return new CursoDTO(
                curso.getId(),
                curso.getNome(),
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