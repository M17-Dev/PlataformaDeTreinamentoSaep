package com.senai.plataforma_de_treinamento_saep.aplication.dto;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;

public record AlunoDTO(
        Long id,
        String nome,
        String cpf,
        String login,
        String senha
) {
    public static AlunoDTO toDTO(Aluno aluno) {
        return new AlunoDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getCpf(),
                aluno.getLogin(),
                aluno.getSenha()
        );
    }

    public Aluno fromDto(){
        Aluno aluno = new Aluno();
        aluno.setNome(nome);
        aluno.setCpf(cpf);
        aluno.setLogin(login);
        aluno.setSenha(senha);
        aluno.setTipoDeUsuario(TipoDeUsuario.ALUNO);
        aluno.setStatus(true);

        return aluno;
    }
}
