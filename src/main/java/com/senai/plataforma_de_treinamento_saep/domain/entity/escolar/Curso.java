package com.senai.plataforma_de_treinamento_saep.domain.entity.escolar;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private boolean status;

    @OneToMany(
            mappedBy = "curso",
            cascade = CascadeType.ALL
    )
    private List<UnidadeCurricular> unidadesCurriculares = new ArrayList<>();

    @OneToMany(mappedBy = "curso")
    private List<Aluno> alunos = new ArrayList<>();
}