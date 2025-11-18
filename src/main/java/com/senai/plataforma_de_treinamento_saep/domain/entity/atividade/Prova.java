package com.senai.plataforma_de_treinamento_saep.domain.entity.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.enums.NivelDeDificuldade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private LocalDate dataCriacao;
    private LocalDate dataUltimaAtualizacao;

    @ManyToMany
    @JoinTable(
            name = "prova_aluno",
            joinColumns = @JoinColumn(name = "prova_id"),
            inverseJoinColumns = @JoinColumn(name = "aluno_id")
    )
    @Builder.Default
    private List<Aluno> alunos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "unidade_curricular_id")
    private UnidadeCurricular unidadeCurricular;

    @ManyToMany
    @JoinTable(
            name = "prova_questao",
            joinColumns = @JoinColumn(name = "prova_id"),
            inverseJoinColumns = @JoinColumn(name = "questao_id")
    )
    @Builder.Default
    private List<Questao> questoes = new ArrayList<>();

    private int qtdQuestoes;
    private int qtdAcertos;

    @Enumerated(EnumType.STRING)
    private NivelDeDificuldade nivelDeDificuldade;

    private boolean status;

    @PrePersist
    public void preencherDataProva() {
        this.dataCriacao = LocalDate.now();
        this.dataUltimaAtualizacao = LocalDate.now();
    }

    @PreUpdate
    public void preencherDataAtualizacao() {
        this.dataUltimaAtualizacao = LocalDate.now();
    }
}