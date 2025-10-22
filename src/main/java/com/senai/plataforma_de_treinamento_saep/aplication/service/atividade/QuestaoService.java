package com.senai.plataforma_de_treinamento_saep.aplication.service.atividade;


import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.QuestaoDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
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
        if (dto.respostas() != null && dto.respostas().size() > 5) {
            throw new RuntimeException("Uma questão não pode ter mais de 5 respostas.");
        }

        Questao questao = dto.fromDTO();
        associarRelacionamentos(questao, dto);

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
        if (dto.respostas() != null && !dto.respostas().isEmpty()){
            questao.setPergunta(dto.pergunta());
        }
        if (dto.respostas() != null && !dto.respostas().isEmpty()){
            questao.setPergunta(dto.pergunta());
        }
    }

    private void associarRelacionamentos(Questao questao, QuestaoDTO dto){
        // 1. Associa o Professor
        if (dto.professorId() != null) {
            Professor prof = profRepo.findById(dto.professorId())
                    .orElseThrow(() -> new RuntimeException("Professor não encontrado."));
            questao.setProfessorID(prof);
        }

        // 2. Associa as Unidades Curriculares
        // Se a lista no DTO for nula ou vazia, define uma lista vazia na entidade.
        if (dto.unidadeCurricularIds() != null && !dto.unidadeCurricularIds().isEmpty()) {
            List<UnidadeCurricular> ucs = ucRepo.findAllById(dto.unidadeCurricularIds());

            if (ucs.size() != dto.unidadeCurricularIds().size()) {
                throw new RuntimeException("Uma ou mais Unidades Curriculares não foram encontradas.");
            }
            // Define a nova lista de UCs na questão
            questao.setUnidadeCurriculares(ucs);
        } else {
            // Se o front-end mandar uma lista vazia, remove todas as associações.
            questao.getUnidadeCurriculares().clear();
        }
    }
}
