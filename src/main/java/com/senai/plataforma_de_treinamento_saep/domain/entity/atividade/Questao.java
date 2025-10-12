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
    private String titulo;
    private String introducao;
    private String Pergunta;
    private String imagem;
    private boolean acertouOuErrouQuestao;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professorID;


    // --- RELACIONAMENTOS CORRIGIDOS ---

    // 1. Relacionamento com Resposta (OneToMany)
    // Uma Questao tem muitas Respostas.
    @OneToMany(
            mappedBy = "questao",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Resposta> respostas = new ArrayList<>();


    // 2. Relacionamento com UnidadeCurricular (ManyToMany)
    // Uma Questao pode estar em muitas Unidades Curriculares.
    @ManyToMany
    @JoinTable(
            name = "questao_unidade_curricular",
            joinColumns = @JoinColumn(name = "questao_id"),
            inverseJoinColumns = @JoinColumn(name = "unidade_curricular_id")
    )
    private List<UnidadeCurricular> unidadeCurriculares = new ArrayList<>();

    private boolean status;
}