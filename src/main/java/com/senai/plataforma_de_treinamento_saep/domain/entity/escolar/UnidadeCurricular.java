package com.senai.plataforma_de_treinamento_saep.domain.entity.escolar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnidadeCurricular {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    private String nome;
    // outros campos da unidade curricular...

    // Muitas Unidades Curriculares pertencem a Um Curso
    @ManyToOne(fetch = FetchType.LAZY) // Opcional, mas bom para performance Miguel: nsei oque faz pergunta pra ia
    @JoinColumn(name = "curso_id")
    protected Curso curso;
}