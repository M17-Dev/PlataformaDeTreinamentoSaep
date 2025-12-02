package com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Prova;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.ProvaRealizada;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;

import java.util.Collections;
import java.util.List;

public record AlunoDTO(
        Long id,
        String nome,
        String cpf,
        String email,
        String senha,
        Long cursoId,
        List<Long> provas,
        List<ResumoProvaRealizadaDTO> provasRealizadas,
        boolean status
) {
    public record ResumoProvaRealizadaDTO(
            Long idProva,
            int acertos
    ) {}

    public static AlunoDTO toDTO(Aluno aluno) {
        Long curso = aluno.getCurso().getId();
        List<Long> listaIdProvas = aluno.getProvas().stream()
                .map(Prova::getIdProva)
                .toList();

        List<ResumoProvaRealizadaDTO> listaProvasRealizadas = Collections.emptyList();

        if (aluno.getProvasRealizadas() != null) {
            listaProvasRealizadas = aluno.getProvasRealizadas().stream()
                    .map(pr -> new ResumoProvaRealizadaDTO(
                            pr.getProva().getIdProva(),
                            pr.getQtdAcertos()
                    ))
                    .toList();
        }

        return new AlunoDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getCpf(),
                aluno.getEmail(),
                aluno.getSenha(),
                curso,
                listaIdProvas,
                listaProvasRealizadas,
                aluno.isStatus()
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