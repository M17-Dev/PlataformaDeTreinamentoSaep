package com.senai.plataforma_de_treinamento_saep.aplication.service.atividade;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.RespostaDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Resposta;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.QuestaoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.RespostaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RespostaService {

    private final RespostaRepository respostaRepo;
    private final QuestaoRepository questaoRepo;

    public RespostaDTO cadastrarResposta(RespostaDTO respostaDTO) {
        Resposta resposta = respostaDTO.fromDTO();
        associarRelacionamentos(respostaDTO, resposta);
        respostaRepo.save(resposta);
        return RespostaDTO.toDTO(respostaRepo.save(resposta));
    }

    public List<RespostaDTO> listarRespostas() {
        // 1. Crie o objeto de ordenação.
        // Queremos ordenar pelo campo "id" da entidade "questao" em ordem Ascendente (ASC).
        Sort sort = Sort.by(Sort.Direction.ASC, "questao.id");

        // 2. Busque todos os registros usando a ordenação
        List<Resposta> respostasOrdenadas = respostaRepo.findAll(sort);

        return respostasOrdenadas.stream()
                .map(RespostaDTO::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<RespostaDTO> buscarPorId(Long id) {
        return respostaRepo.findById(id).map(RespostaDTO::toDTO);
    }

    public Optional<RespostaDTO> atualizarResposta(RespostaDTO respostaDTO) {
        return respostaRepo.findById(respostaDTO.id()).map(resposta -> {
           atualizarInfosResposta(resposta, respostaDTO);
           return RespostaDTO.toDTO(respostaRepo.save(resposta));
        });
    }

    public void atualizarInfosResposta(Resposta resposta, RespostaDTO respostaAtualizada) {
        associarRelacionamentos(respostaAtualizada, resposta);
        if(!respostaAtualizada.texto().isBlank()) {
            resposta.setTexto(respostaAtualizada.texto());
        }
        if(respostaAtualizada.certaOuErrada() != null) {
            resposta.setCertoOuErrado(respostaAtualizada.certaOuErrada());
        }
    }

    private void associarRelacionamentos(RespostaDTO respostaDTO, Resposta resposta) {
        if(respostaRepo.findById(resposta.getId()).isPresent()) {
            boolean questaoIgual = Objects.equals(respostaDTO.idQuestao(), resposta.getQuestao().getId());

            if(!questaoIgual) {
                Questao questao = questaoRepo.findById(respostaDTO.idQuestao())
                        .orElseThrow(() -> new RuntimeException("Questão não encontrada."));
                resposta.setQuestao(questao);
            }
        } else if(respostaDTO.idQuestao() != null) {
            Questao questao = questaoRepo.findById(respostaDTO.idQuestao())
                    .orElseThrow(() -> new RuntimeException("Questão não encontrada."));

            if(questao.getRespostas().size() > 4) {
                throw new RuntimeException("Uma questão não pode ter mais de 5 respostas.");
            }

            resposta.setQuestao(questao);
        }
    }
}
