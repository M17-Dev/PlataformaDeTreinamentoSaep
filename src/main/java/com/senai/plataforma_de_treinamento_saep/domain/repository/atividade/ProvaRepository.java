package com.senai.plataforma_de_treinamento_saep.domain.repository.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Prova;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProvaRepository extends JpaRepository<Prova, Long> {
    List<Prova> findByStatusTrue();

    @Query("SELECT p FROM Prova p WHERE p.unidadeCurricular.curso.id = :cursoId")
    List<Prova> findByCursoId(@Param("cursoId") Long cursoId);
}
