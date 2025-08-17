package com.senai.plataforma_de_treinamento_saep.domain.repository;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    List<Aluno> findByStatusTrue();
}
