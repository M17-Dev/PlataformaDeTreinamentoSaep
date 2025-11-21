package com.senai.plataforma_de_treinamento_saep.domain.repository.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestaoRepository extends JpaRepository<Questao, Long> {
    List<Questao> findByStatusTrue();

    @Query("SELECT q FROM Questao q WHERE q.unidadeCurricular.curso.id = :cursoId")
    List<Questao> findByCursoId(@Param("cursoId") Long cursoId);
}
