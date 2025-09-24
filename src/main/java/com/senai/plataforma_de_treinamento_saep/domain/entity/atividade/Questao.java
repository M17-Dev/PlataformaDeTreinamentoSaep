package com.senai.plataforma_de_treinamento_saep.domain.entity.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList; // Importe o ArrayList
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


    // 1. A Questão tem uma LISTA de Respostas.
    // 2. mappedBy="questao" diz que a entidade Resposta é quem "manda" na relação.
    // 3. Cascade e orphanRemoval gerenciam o ciclo de vida das respostas junto com a questão.
    @OneToMany(
            mappedBy = "questao",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    protected List<Resposta> respostas = new ArrayList<>();


    @OneToMany(mappedBy = "questao")
    protected List<UnidadeCurricular> unidadeCurriculars;
}