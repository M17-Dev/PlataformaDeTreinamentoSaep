package com.senai.plataforma_de_treinamento_saep.domain.repository.escolar;

import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnidadeCurricularRepository extends JpaRepository<UnidadeCurricular,Long> {
    List<UnidadeCurricular> findByStatusTrue();
    List<UnidadeCurricular> findByCursoIdAndStatusTrue(Long idCurso);
}
