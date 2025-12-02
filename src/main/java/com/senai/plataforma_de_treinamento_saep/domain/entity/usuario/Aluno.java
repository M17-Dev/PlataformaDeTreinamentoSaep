package com.senai.plataforma_de_treinamento_saep.domain.entity.usuario;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Prova;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.ProvaRealizada;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.Curso;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Aluno extends Usuario{

    @ManyToMany(mappedBy = "alunos")
    private List<Prova> provas = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @OneToMany(mappedBy = "aluno", fetch = FetchType.LAZY)
    private List<ProvaRealizada> provasRealizadas = new ArrayList<>();
}