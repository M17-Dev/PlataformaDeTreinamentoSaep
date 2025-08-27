package com.senai.plataforma_de_treinamento_saep.aplication.dto;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;

public record ProfessorDTO(
        Long id,
        String nome,
        String cpf,
        String login,
        String senha
) {
    public static ProfessorDTO toDTO(Professor professor) {
        return new ProfessorDTO(
                professor.getId(),
                professor.getNome(),
                professor.getCpf(),
                professor.getLogin(),
                professor.getSenha()
        );
    }

    public Professor fromDto(){
        Professor professor = new Professor();
        professor.setNome(nome);
        professor.setCpf(cpf);
        professor.setLogin(login);
        professor.setSenha(senha);
        professor.setTipoDeUsuario(TipoDeUsuario.PROFESSOR);
        professor.setStatus(true);

        return professor;
    }
}
