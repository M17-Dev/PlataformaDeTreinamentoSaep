package com.senai.plataforma_de_treinamento_saep.aplication.dto;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;

public record ProfessorDTO(
        Long id,
        String nome,
        String cpf,
        String matricula
) {
    public static ProfessorDTO toDTO(Professor professor) {
        return new ProfessorDTO(
                professor.getId(),
                professor.getNome(),
                professor.getCpf(),
                professor.getMatricula()
        );
    }

    public Professor fromDto(){
        Professor professor = new Professor();
        professor.setNome(nome);
        professor.setCpf(cpf);
        professor.setMatricula(matricula);
        professor.setTipoDeUsuario(TipoDeUsuario.PROFESSOR);
        professor.setStatus(true);

        return professor;
    }
}
