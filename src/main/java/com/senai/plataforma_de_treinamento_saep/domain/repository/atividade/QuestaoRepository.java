package com.senai.plataforma_de_treinamento_saep.domain.repository.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;
import com.senai.plataforma_de_treinamento_saep.domain.enums.NivelDeDificuldade;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestaoRepository extends JpaRepository<Questao, Long> {
    List<Questao> findByStatusTrue();

    @Query("SELECT q FROM Questao q WHERE q.unidadeCurricular.curso.id = :cursoId")
    List<Questao> findByCursoId(@Param("cursoId") Long cursoId);

    List<Questao> findByUnidadeCurricularAndNivelDeDificuldadeAndStatusTrueAndProvasIsEmpty(
            UnidadeCurricular uc,
            NivelDeDificuldade nivel,
            Pageable pageable // Importante para limitar a quantidade (LIMIT)
    );}
