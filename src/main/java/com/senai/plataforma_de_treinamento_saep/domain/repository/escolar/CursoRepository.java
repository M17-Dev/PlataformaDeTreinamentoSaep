package com.senai.plataforma_de_treinamento_saep.domain.repository.escolar;

import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long> {
}
