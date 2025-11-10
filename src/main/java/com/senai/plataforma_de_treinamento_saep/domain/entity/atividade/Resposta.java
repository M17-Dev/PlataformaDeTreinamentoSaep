package com.senai.plataforma_de_treinamento_saep.domain.entity.atividade;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    // Adicione os campos da resposta, como o texto e se é a correta
    protected String texto;
    protected boolean certoOuErrado;
    protected boolean status;

    // 1. Muitas Respostas pertencem a Uma Questão.
    // 2. @JoinColumn cria a chave estrangeira na tabela Resposta.
    @ManyToOne
    @JoinColumn(name = "questao_id") // Esta é a coluna que ligará a Resposta à Questão
    protected Questao questao;
}