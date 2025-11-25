package com.senai.plataforma_de_treinamento_saep.domain.entity.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProvaRealizada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "prova_id")
    private Prova prova;

    private int qtdAcertos;

    @OneToMany(mappedBy = "provaRealizada", cascade = CascadeType.ALL)
    private List<RespostaDada> respostasDadas = new ArrayList<>();
}