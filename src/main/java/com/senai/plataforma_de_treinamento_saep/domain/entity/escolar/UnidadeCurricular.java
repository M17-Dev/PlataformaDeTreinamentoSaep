package com.senai.plataforma_de_treinamento_saep.domain.entity.escolar;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Prova;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnidadeCurricular {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String fraseDaUc;

    @OneToMany(mappedBy = "unidadeCurricular")
    private List<Questao> questoes = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @OneToMany(mappedBy = "unidadeCurricular")
    private List<Prova> provas;

    private boolean status;
}