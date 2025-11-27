package com.senai.plataforma_de_treinamento_saep.domain.entity.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
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

    private String introducao;
    private String pergunta;
    private String imagem;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

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

    @ManyToMany(mappedBy = "questoes")
    private List<Prova> provas = new ArrayList<>();

    private boolean status;
}