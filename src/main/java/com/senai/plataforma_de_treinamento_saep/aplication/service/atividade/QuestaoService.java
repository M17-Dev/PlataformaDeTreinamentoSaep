package com.senai.plataforma_de_treinamento_saep.aplication.service.atividade;


import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.QuestaoDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.RespostaDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Resposta;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.exception.RegraDeNegocioException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.QuestaoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.escolar.UnidadeCurricularRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestaoService {

    private final QuestaoRepository questaoRepo;
    private final UnidadeCurricularRepository ucRepo;
    private final ProfessorRepository profRepo;

    @Transactional
    public QuestaoDTO cadastrarQuestao(QuestaoDTO dto) {
        verificarDadosObrigatorios(dto);
        validarQuantidadeDeRespostas(dto.respostas());
        validarUnicaRespostaCorreta(dto.respostas());

        Questao questao = dto.fromDTO();
        associarRelacionamentos(questao, dto);

        List<Resposta> respostasQuestao = converterEAssociarRespostas(dto.respostas(), questao);
        questao.setRespostas(respostasQuestao);

        Questao questaoSalva = questaoRepo.save(questao);

        return QuestaoDTO.toDTO(questaoSalva);
    }

    public List<QuestaoDTO> listarQuestoesAtivas() {
        return questaoRepo.findByStatusTrue()
                .stream()
                .map(questao -> {
                    List<RespostaDTO> respostasFiltradasDTO = questao.getRespostas().stream()
                            .filter(Resposta::isStatus)
                            .map(RespostaDTO::toDTO)
                            .toList();

                    return QuestaoDTO.toDTO(questao, respostasFiltradasDTO);
                })
                .toList();
    }

    public List<QuestaoDTO> listarQuestoesPeloCurso(Long idCurso) {
        return questaoRepo.findByCursoId(idCurso).stream()
                .map(
                        questao -> {
                            List<RespostaDTO> respostasFiltradasDTO = questao.getRespostas().stream()
                                    .filter(Resposta::isStatus)
                                    .map(RespostaDTO::toDTO)
                                    .toList();

                            return QuestaoDTO.toDTO(questao, respostasFiltradasDTO);
                        }
                )
                .toList();
    }

    public Optional<QuestaoDTO> buscarPorId(Long id) {
        return questaoRepo.findById(id)
                .map(
                        questao -> {
                            List<RespostaDTO> respostasFiltradasDTO = questao.getRespostas()
                                    .stream()
                                    .filter(Resposta::isStatus)
                                    .map(RespostaDTO::toDTO)
                                    .toList();

                            return QuestaoDTO.toDTO(questao, respostasFiltradasDTO);
                        }
                );
    }

    @Transactional
    public QuestaoDTO atualizarQuestao(Long id, QuestaoDTO dto) {
        return questaoRepo.findById(id)
                .map(
                        questao -> {
                            atualizarInfos(questao, dto);
                            associarRelacionamentos(questao, dto);
                            Questao questaoAtualizada = questaoRepo.save(questao);
                            return QuestaoDTO.toDTO(questaoAtualizada);
                        }
                )
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Questão com o ID:" + id + " não encontrada."));
    }

    @Transactional
    public boolean inativarQuestao(Long id) {
        return questaoRepo.findById(id)
                .filter(
                        Questao::isStatus
                )
                .map(
                        questao -> {
                            questao.setStatus(false);
                            questao.getRespostas().forEach(resposta -> {
                                resposta.setStatus(false);
                            });
                            questaoRepo.save(questao);
                            return true;
                        }
                )
                .orElse(false);
    }

    @Transactional
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
        if (dto.imagem() != null && !dto.imagem().isBlank()) {
            questao.setImagem(dto.imagem());
        }
        if (dto.nivelDeDificuldade() != null) {
            questao.setNivelDeDificuldade(dto.nivelDeDificuldade());
        }
    }

    private void associarRelacionamentos(Questao questao, QuestaoDTO dto) {
        if (dto.professorId() != null) {
            Professor prof = profRepo.findById(dto.professorId())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Professor de ID: " + dto.professorId() + " não encontrado."));
            questao.setProfessorId(prof);
        }

        if (dto.unidadeCurricularId() != null) {
            UnidadeCurricular uc = ucRepo.findById(dto.unidadeCurricularId())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Unidade Curricular com ID " + dto.unidadeCurricularId() + " não encontrada."));
            questao.setUnidadeCurricular(uc);
        }
    }

    private void verificarDadosObrigatorios(QuestaoDTO dto) {
        if (dto.professorId() == null) {
            throw new RegraDeNegocioException("Um professor é obrigatório para cadastrar uma questão.");
        }
        if (dto.unidadeCurricularId() == null) {
            throw new RegraDeNegocioException("Uma unidade curricular é obrigatória para cadastrar uma questão");
        }
        if (!ucRepo.existsById(dto.unidadeCurricularId())) {
            throw new EntidadeNaoEncontradaException("Unidade curricular de ID:" + dto.unidadeCurricularId() + " não encontrada.");
        }
        if (dto.nivelDeDificuldade() == null) {
            throw new RegraDeNegocioException("O nível de dificuldade é obrigatório para cadastrar uma questãao");
        }
    }

    private void validarQuantidadeDeRespostas(List<RespostaDTO> respostas) {
        if (respostas == null || respostas.size() != 5) {
            throw new RegraDeNegocioException("A questão deve ter exatamente 5 respostas!");
        }
    }

    private void validarUnicaRespostaCorreta(List<RespostaDTO> respostas) {
        long respostasCorretas = respostas.stream()
                .filter(r -> Boolean.TRUE.equals(r.certaOuErrada()))
                .count();

        if (respostasCorretas == 0) {
            throw new RegraDeNegocioException("A questão deve conter uma resposta correta!");
        }
        if (respostasCorretas > 1) {
            throw new RegraDeNegocioException("A questão não pode conter mais que uma resposta correta!");
        }
    }

    private List<Resposta> converterEAssociarRespostas(List<RespostaDTO> respostas, Questao questaoPai) {
        return respostas.stream()
                .map(dto -> {
                    Resposta resposta = dto.fromDTO();

                    resposta.setStatus(true);

                    if (dto.certaOuErrada() == null) {
                        resposta.setCertoOuErrado(false);
                    }

                    resposta.setQuestao(questaoPai);

                    return resposta;
                })
                .toList();
    }
}