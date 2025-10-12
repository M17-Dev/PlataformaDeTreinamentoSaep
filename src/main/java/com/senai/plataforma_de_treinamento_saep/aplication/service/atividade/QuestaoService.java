package com.senai.plataforma_de_treinamento_saep.aplication.service.atividade;


import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.QuestaoDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
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

    public Questao cadastrarQuestao(QuestaoDTO dto){
        return questaoRepo.save(dto.fromDTO());
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

    public Optional<QuestaoDTO> buscarPorId(Long id) {
        return questaoRepo.findById(id)
                .map(
                        QuestaoDTO::toDTO
                );
    }

    public QuestaoDTO atualizarQuestao(Long id, QuestaoDTO dto) {
        return questaoRepo.findById(id)
                .map(
                        questao -> {
                            atualizarInfos(questao, dto);
                            Questao questaoAtualizada =  questaoRepo.save(questao);
                            return QuestaoDTO.toDTO(questaoAtualizada);
                        }
                )
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Questão com o ID:" + id + " não encontrada."));
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

    private void atualizarInfos(Questao questao, QuestaoDTO dto) {
        if (dto.titulo() != null && !dto.titulo().isBlank()) {
            questao.setTitulo(dto.titulo());
        }
        if (dto.introducao() != null && !dto.introducao().isBlank()) {
            questao.setIntroducao(dto.introducao());
        }
        if (dto.pergunta() != null && !dto.pergunta().isBlank()) {
            questao.setPergunta(dto.pergunta());
        }
        if (dto.imagem() != null && !dto.imagem().isBlank()){
            questao.setImagem(dto.imagem());
        }
        if (dto.respostas() != null && !dto.respostas().isEmpty()){
            questao.setPergunta(dto.pergunta());
        }
        if (dto.respostas() != null && !dto.respostas().isEmpty()){
            questao.setPergunta(dto.pergunta());
        }
    }
}
