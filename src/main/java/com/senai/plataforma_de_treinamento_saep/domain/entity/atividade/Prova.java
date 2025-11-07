package com.senai.plataforma_de_treinamento_saep.domain.entity.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.enums.NivelDeDificuldade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Inheritance (strategy = InheritanceType.JOINED)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Prova {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProva;

    private String descricao;
    private Date dataProva;

    @ManyToMany
    @JoinTable(
            name = "prova_aluno",
            joinColumns = @JoinColumn(name = "prova_id"),
            inverseJoinColumns = @JoinColumn(name = "aluno_id")
    )
    private List<Aluno> alunos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "unidade_curricular_id")
    private UnidadeCurricular unidadeCurricular;

    private int qtdQuestoes;
    private int qtdAcertos;

    @Enumerated(EnumType.STRING)
    private NivelDeDificuldade nivelDeDificuldade;

    private boolean status;
}