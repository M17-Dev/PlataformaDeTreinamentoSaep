package com.senai.plataforma_de_treinamento_saep.domain.repository.usuario;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    List<Aluno> findByStatusTrue();
}
