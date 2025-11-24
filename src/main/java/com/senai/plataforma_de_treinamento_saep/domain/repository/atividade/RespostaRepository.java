package com.senai.plataforma_de_treinamento_saep.domain.repository.atividade;

import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Resposta;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RespostaRepository extends JpaRepository<Resposta, Long> {
    int countByQuestaoIdAndStatus(Long idQuestao, Boolean status);
    int countByQuestaoIdAndCertoOuErradoAndStatus(Long idQuestao, boolean certoOuErrado, boolean status);
    List<Resposta> findByStatusTrue(Sort sort);

    @Query("SELECT r FROM Resposta r WHERE r.questao.id = :questaoId")
    List<Resposta> findByQuestaoId(@Param("questaoId") Long questaoId);
}
