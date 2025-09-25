// Copie e cole este código inteiro no seu arquivo Questao.java

package com.senai.plataforma_de_treinamento_saep.domain.entity.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Questao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    //Para os textos da questão em sí
    protected String titulo;
    protected String introducao;
    protected String Pergunta;
    protected String imagem;
    protected boolean acertouOuErrouQuestao;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    protected Professor professorID;


    // --- RELACIONAMENTOS CORRIGIDOS ---

    // 1. Relacionamento com Resposta (OneToMany)
    // Uma Questao tem muitas Respostas.
    @OneToMany(
            mappedBy = "questao",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    protected List<Resposta> respostas = new ArrayList<>();


    // 2. Relacionamento com UnidadeCurricular (ManyToMany)
    // Uma Questao pode estar em muitas Unidades Curriculares.
    @ManyToMany
    @JoinTable(
            name = "questao_unidade_curricular",
            joinColumns = @JoinColumn(name = "questao_id"),
            inverseJoinColumns = @JoinColumn(name = "unidade_curricular_id")
    )
    protected List<UnidadeCurricular> unidadeCurriculars = new ArrayList<>();
}