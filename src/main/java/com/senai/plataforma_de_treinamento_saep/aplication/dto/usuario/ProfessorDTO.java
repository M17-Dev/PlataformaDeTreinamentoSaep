package com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.reciclagem.UsuarioReciclagemDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;

public record ProfessorDTO(
        Long id,
        String nome,
        String cpf,
        String email,
        String senha,
        TipoDeUsuario tipoDeUsuario,
        boolean status,
        UsuarioReciclagemDTO reciclagem
) {

    // Método padrão (reciclagem null)
    public static ProfessorDTO toDTO(Professor professor) {
        return new ProfessorDTO(
                professor.getId(),
                professor.getNome(),
                professor.getCpf(),
                professor.getEmail(),
                professor.getSenha(),
                professor.getTipoDeUsuario(),
                professor.isStatus(),
                null
        );
    }

    // Método com reciclagem
    public static ProfessorDTO toDTO(Professor professor, UsuarioReciclagemDTO reciclagem) {
        return new ProfessorDTO(
                professor.getId(),
                professor.getNome(),
                professor.getCpf(),
                professor.getEmail(),
                professor.getSenha(),
                professor.getTipoDeUsuario(),
                professor.isStatus(),
                reciclagem
        );
    }

    public Professor fromDto() {
        Professor professor = new Professor();
        professor.setNome(nome);
        professor.setCpf(cpf);
        professor.setEmail(email);
        professor.setSenha(senha);
        professor.setTipoDeUsuario(TipoDeUsuario.PROFESSOR);
        professor.setStatus(true);
        return professor;
    }
}