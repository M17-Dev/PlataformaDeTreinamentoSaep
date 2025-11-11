package com.senai.plataforma_de_treinamento_saep.aplication.service.atividade;


import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.QuestaoDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Resposta;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.QuestaoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.escolar.UnidadeCurricularRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestaoService {

    private final QuestaoRepository questaoRepo;
    private final UnidadeCurricularRepository ucRepo;
    private final ProfessorRepository profRepo;

    public Questao cadastrarQuestao(QuestaoDTO dto){
        if (dto.professorId() == null) {
            throw new RuntimeException("Um professor é obrigatório para cadastrar uma questão.");
        }
        if (dto.respostas() != null && dto.respostas().size() > 4) {
            throw new RuntimeException("Uma questão não pode ter mais de 5 respostas.");
        }

        Questao questao = dto.fromDTO();
        associarRelacionamentos(questao, dto);

        return questaoRepo.save(questao);
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
                            associarRelacionamentos(questao, dto);
                            Questao questaoAtualizada =  questaoRepo.save(questao);
                            return QuestaoDTO.toDTO(questaoAtualizada);
                        }
                )
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Questão com o ID:" + id + " não encontrada."));
    }

    public boolean inativarQuestao(Long id) {
        return questaoRepo.findById(id)
                .filter(
                        Questao::isStatus
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
        if (dto.nivelDeDificuldade() != null) {
            questao.setNivelDeDificuldade(dto.nivelDeDificuldade());
        }
    }

    private void associarRelacionamentos(Questao questao, QuestaoDTO dto){
        if (dto.professorId() != null) {
            Professor prof = profRepo.findById(dto.professorId())
                    .orElseThrow(() -> new RuntimeException("Professor não encontrado."));
            questao.setProfessorId(prof);
        }

        if (dto.unidadeCurricularId() != null) {
            UnidadeCurricular uc = ucRepo.findById(dto.unidadeCurricularId())
                    .orElseThrow(() -> new RuntimeException("Unidade Curricular com ID " + dto.unidadeCurricularId() + " não encontrada."));
            questao.setUnidadeCurricular(uc);
        }

        if (dto.respostas() != null) {
            List<Resposta> novasRespostas = dto.respostas().stream()
                    .map(dtoResposta -> {
                        Resposta resposta = new Resposta();
                        resposta.setId(dtoResposta.id());
                        resposta.setTexto(dtoResposta.texto());
                        resposta.setCertoOuErrado(dtoResposta.certaOuErrada());
                        resposta.setQuestao(questao);
                        return resposta;
                    })
                    .toList();
            questao.getRespostas().clear();
            questao.getRespostas().addAll(novasRespostas);
        }
    }
}