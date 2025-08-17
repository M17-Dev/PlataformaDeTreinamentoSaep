package com.senai.plataforma_de_treinamento_saep.domain.repository;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    List<Professor> findByStatusTrue();
}
