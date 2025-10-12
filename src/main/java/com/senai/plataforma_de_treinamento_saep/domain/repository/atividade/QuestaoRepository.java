package com.senai.plataforma_de_treinamento_saep.domain.repository.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestaoRepository extends JpaRepository<Questao, Long> {
    List<Questao> findByStatusTrue();

}
