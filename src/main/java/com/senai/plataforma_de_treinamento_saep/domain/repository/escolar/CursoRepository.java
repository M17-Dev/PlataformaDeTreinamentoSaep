package com.senai.plataforma_de_treinamento_saep.domain.repository.escolar;

import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByStatusTrue();
}
