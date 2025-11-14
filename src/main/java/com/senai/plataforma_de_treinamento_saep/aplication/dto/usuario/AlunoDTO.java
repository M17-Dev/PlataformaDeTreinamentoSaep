package com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Prova;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;

import java.util.List;

public record AlunoDTO(
        Long id,
        String nome,
        String cpf,
        String senha,
        Long cursoId,
        List<Long> provas,
        boolean status
) {
    public static AlunoDTO toDTO(Aluno aluno) {
        Long curso = aluno.getCurso().getId();
        List<Long> listaIdProvas = aluno.getProvas().stream()
                .map(Prova::getIdProva)
                .toList();

        return new AlunoDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getCpf(),
                aluno.getSenha(),
                curso,
                listaIdProvas,
                aluno.isStatus()
        );
    }

    public Aluno fromDto(){
        Aluno aluno = new Aluno();
        aluno.setNome(nome);
        aluno.setCpf(cpf);
        aluno.setSenha(senha);
        aluno.setTipoDeUsuario(TipoDeUsuario.ALUNO);
        aluno.setStatus(true);

        return aluno;
    }
}