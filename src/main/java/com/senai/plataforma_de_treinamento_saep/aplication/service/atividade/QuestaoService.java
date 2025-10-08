package com.senai.plataforma_de_treinamento_saep.aplication.service.atividade;


import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.QuestaoDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.QuestaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestaoService {

    private final QuestaoRepository questaoRepo;

    public void cadastrarQuestao (QuestaoDTO dto){
        questaoRepo.save(dto.fromDTO());
    }

    public List<QuestaoDTO> listarQuestoesAtivas() {
        return questaoRepo.findByStatusTrue()
                .stream()
                .map(
                        QuestaoDTO::toDTO
                )
                .collect(
                        Collectors.toList()
                );
    }

    public Optional<QuestaoDTO>buscarPorId(Long id) {
        return questaoRepo.findById(id)
                .map(
                        QuestaoDTO::toDTO
                );
    }



    public boolean atualizarQuestao(Long id, QuestaoDTO dto) {
        return questaoRepo.findById(id)
                .map(
                        questao -> {
                            atualizarInfos(questao, dto);
                            questaoRepo.save(questao);
                            return true;
                        }
                )
                .orElse(false);
    }

    public boolean inativarQuestao(Long id) {
        return questaoRepo.findById(id)
                .filter(
                        QuestaoDTO::isStatus
                )
                .map(
                        questao -> {
                            questao.setStatus(false);
                            questaoRepo.save(questao);
                            return true;
                        }
                )
                .orElse(false);
    }

    public boolean reativarQuestao(Long id) {
        return questaoRepo.findById(id)
                .filter(
                        questao -> !questao.isStatus()
                )
                .map(
                        questao -> {
                            questao.setStatus(true);
                            questaoRepo.save(questao);
                            return true;
                        }
                )
                .orElse(false);
    }

    private void atualizarInfos(Questao coord, QuestaoDTO dto) {
        if (dto.() != null && !dto.nome().isBlank()) {
            coord.setNome(dto.nome());
        }
        if (dto.senha() != null && !dto.senha().isBlank()) {
            coord.setSenha(dto.senha());
        }
    }

}
