package com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.reciclagem.UsuarioTampinhaDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;

// Adicione o campo UsuarioTampinhaDTO no final
public record ProfessorDTO(
        Long id,
        String nome,
        String cpf,
        String email,
        String senha,
        TipoDeUsuario tipoDeUsuario,
        boolean status,
        UsuarioTampinhaDTO reciclagem // Novo campo
) {

    // Método antigo (mantido para compatibilidade, passa null na reciclagem)
    public static ProfessorDTO toDTO(Professor professor) {
        return new ProfessorDTO(
                professor.getId(),
                professor.getNome(),
                professor.getCpf(),
                professor.getEmail(),
                professor.getSenha(),
                professor.getTipoDeUsuario(),
                professor.isStatus(),
                null // Reciclagem vazia
        );
    }

    // NOVO MÉTODO: Aceita o Professor e os dados da Reciclagem
    public static ProfessorDTO toDTO(Professor professor, UsuarioTampinhaDTO reciclagem) {
        return new ProfessorDTO(
                professor.getId(),
                professor.getNome(),
                professor.getCpf(),
                professor.getSenha(),
                professor.getTipoDeUsuario(),
                professor.isStatus(),
                reciclagem // Preenche os dados
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