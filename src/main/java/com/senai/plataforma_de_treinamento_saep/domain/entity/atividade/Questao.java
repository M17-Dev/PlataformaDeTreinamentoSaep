package com.senai.plataforma_de_treinamento_saep.domain.entity.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import com.senai.plataforma_de_treinamento_saep.domain.enums.NivelDeDificuldade;
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

    private String titulo;
    private String introducao;
    private String pergunta;
    private String imagem;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professorId;

    @OneToMany(
            mappedBy = "questao",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Resposta> respostas = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "unidade_curricular")
    private UnidadeCurricular unidadeCurricular;

    @Enumerated(EnumType.STRING)
    private NivelDeDificuldade nivelDeDificuldade;

    private boolean status;
}