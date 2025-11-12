package com.senai.plataforma_de_treinamento_saep.domain.repository.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Resposta;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RespostaRepository extends JpaRepository<Resposta, Long> {
    int countByQuestaoIdAndStatus(Long idQuestao, Boolean status);
    List<Resposta> findByStatusTrue(Sort sort);
}
