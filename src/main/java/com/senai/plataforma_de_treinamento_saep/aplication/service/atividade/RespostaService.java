package com.senai.plataforma_de_treinamento_saep.aplication.service.atividade;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.RespostaDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Resposta;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.QuestaoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.RespostaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RespostaService {

    private final RespostaRepository respostaRepo;
    private final QuestaoRepository questaoRepo;

    @Transactional
    public RespostaDTO cadastrarResposta(RespostaDTO respostaDTO) {
        if(respostaDTO.idQuestao() == null) {
            throw new RuntimeException("A 'idQuestao' é obrigatória para a criação de uma resposta.");
        }
        int qtdRespostasCorretas = respostaRepo.countByQuestaoIdAndCertoOuErradoAndStatus(respostaDTO.idQuestao(), true, true);
        boolean respostaIsCerta = respostaDTO.certaOuErrada();

        if(qtdRespostasCorretas == 0 && !respostaIsCerta){
            throw new RuntimeException("A primeira resposta de uma questão deve ser correta (Este erro pode aparecer caso você tenha deletado a resposta correta).");
        } else if (qtdRespostasCorretas == 1 && respostaIsCerta) {
            throw new RuntimeException("Cada questão só pode ter uma resposta correta.");
        }

        Resposta resposta = respostaDTO.fromDTO();
        resposta.setStatus(true);
        Questao questao = buscarEValidarQuestaoParaAssociacao(respostaDTO.idQuestao());
        resposta.setQuestao(questao);

        return RespostaDTO.toDTO(respostaRepo.save(resposta));
    }

    public List<RespostaDTO> listarRespostas() {
        Sort sort = Sort.by(Sort.Direction.ASC, "questao.id");

        List<Resposta> respostasAtivas = respostaRepo.findByStatusTrue(sort);

        return respostasAtivas.stream()
                .map(RespostaDTO::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<RespostaDTO> buscarPorId(Long id) {
        return respostaRepo.findById(id).filter(Resposta::isStatus).map(RespostaDTO::toDTO);
    }

    @Transactional
    public Optional<RespostaDTO> atualizarResposta(RespostaDTO respostaDTO, Long id) {
        return respostaRepo.findById(id).map(resposta -> {
           atualizarInfosResposta(resposta, respostaDTO);
           return RespostaDTO.toDTO(respostaRepo.save(resposta));
        });
    }

    @Transactional
    public boolean deletarResposta(Long id) {
        return respostaRepo.findById(id).map(resposta -> {
            resposta.setStatus(false);
            respostaRepo.save(resposta);
            return true;
        }).orElse(false);
    }

    private void atualizarInfosResposta(Resposta resposta, RespostaDTO respostaAtualizada) {
        if(!respostaAtualizada.texto().isBlank()) {
            resposta.setTexto(respostaAtualizada.texto());
        }

        Long idNovaQuestao = respostaAtualizada.idQuestao();
        Long idQuestaoAtual = resposta.getQuestao().getId();

        if (idNovaQuestao != null && !Objects.equals(idNovaQuestao, idQuestaoAtual)) {
            Questao novaQuestao = buscarEValidarQuestaoParaAssociacao(idNovaQuestao);
            resposta.setQuestao(novaQuestao);
        }

        respostaRepo.save(resposta);
    }

    private Questao buscarEValidarQuestaoParaAssociacao(Long idQuestao) {
        // 1. Buscar a Questão
        Questao questao = questaoRepo.findById(idQuestao).filter(Questao::isStatus)
                .orElseThrow(() -> new RuntimeException("Questão não encontrada. ID: " + idQuestao));

        // 2. Contar respostasId ATIVAS (status == true)
        int respostasAtivas = respostaRepo.countByQuestaoIdAndStatus(idQuestao, true);

        // 3. Aplicar a regra de negócio
        if (respostasAtivas >= 5) {
            throw new RuntimeException("A Questão (ID: " + idQuestao + ") já possui o limite de 5 respostasId ativas.");
        }

        return questao;
    }
}
