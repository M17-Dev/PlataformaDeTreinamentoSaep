package com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.reciclagem.UsuarioReciclagemDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Prova;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;

import java.util.List;

public record AlunoDTO(
        Long id,
        String nome,
        String cpf,
        String email,
        String senha,
        Long cursoId,
        List<Long> provas,
        boolean status,
        UsuarioReciclagemDTO reciclagem
) {
    public static AlunoDTO toDTO(Aluno aluno) {
        Long curso = (aluno.getCurso() != null) ? aluno.getCurso().getId() : null;
        List<Long> listaIdProvas = aluno.getProvas().stream()
                .map(Prova::getIdProva)
                .toList();

        return new AlunoDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getCpf(),
                aluno.getEmail(),
                aluno.getSenha(),
                curso,
                listaIdProvas,
                aluno.isStatus(),
                null
        );
    }

    public static AlunoDTO toDTO(Aluno aluno, UsuarioReciclagemDTO reciclagem) {
        Long curso = (aluno.getCurso() != null) ? aluno.getCurso().getId() : null;
        List<Long> listaIdProvas = aluno.getProvas().stream()
                .map(Prova::getIdProva)
                .toList();

        return new AlunoDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getCpf(),
                aluno.getEmail(),
                aluno.getSenha(),
                curso,
                listaIdProvas,
                aluno.isStatus(),
                reciclagem
        );
    }

    public Aluno fromDto(){
        Aluno aluno = new Aluno();
        aluno.setNome(nome);
        aluno.setCpf(cpf);
        aluno.setEmail(email);
        aluno.setSenha(senha);
        aluno.setTipoDeUsuario(TipoDeUsuario.ALUNO);
        aluno.setStatus(true);
        return aluno;
    }
}