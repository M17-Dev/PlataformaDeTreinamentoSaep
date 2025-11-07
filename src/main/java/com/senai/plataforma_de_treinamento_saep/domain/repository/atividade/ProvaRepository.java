package com.senai.plataforma_de_treinamento_saep.domain.repository.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Prova;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProvaRepository extends JpaRepository<Prova, Long> {
}
