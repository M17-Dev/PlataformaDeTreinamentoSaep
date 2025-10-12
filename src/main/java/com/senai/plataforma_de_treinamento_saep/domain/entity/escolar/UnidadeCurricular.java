package com.senai.plataforma_de_treinamento_saep.domain.entity.escolar;

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
    // outros campos da unidade curricular...

    // Adicione este campo na sua classe
    @ManyToMany(mappedBy = "unidadeCurriculars")
    private List<Questao> questoes = new ArrayList<>();

    // Muitas Unidades Curriculares pertencem a Um Curso
    @ManyToOne(fetch = FetchType.LAZY) // Opcional, mas bom para performance Miguel: nsei oque faz pergunta pra ia
    @JoinColumn(name = "curso_id")
    private Curso curso;
}