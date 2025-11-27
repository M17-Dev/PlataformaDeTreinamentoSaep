package com.senai.plataforma_de_treinamento_saep.domain.repository.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Prova;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;
import com.senai.plataforma_de_treinamento_saep.domain.enums.NivelDeDificuldade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvaRepository extends JpaRepository<Prova, Long> {
    List<Prova> findByStatusTrue();

    @Query("SELECT p FROM Prova p WHERE p.unidadeCurricular.curso.id = :cursoId")
    List<Prova> findByCursoId(@Param("cursoId") Long cursoId);

    @Query("SELECT COUNT(q) FROM Prova p JOIN p.questoes q WHERE p.idProva = :provaId AND q.status = true")
    int countActiveQuestionsByProvaId(@Param("provaId") Long provaId);

    Optional<Prova> findFirstByUnidadeCurricularAndNivelDeDificuldadeAndStatusTrueAndQtdQuestoesLessThanOrderByIdProvaAsc(
            UnidadeCurricular uc,
            NivelDeDificuldade nivel,
            int limite
    );
}
