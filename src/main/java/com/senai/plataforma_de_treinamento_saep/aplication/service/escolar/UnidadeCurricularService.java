package com.senai.plataforma_de_treinamento_saep.aplication.service.escolar;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.QuestaoDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.escolar.UnidadeCurricularDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.Curso;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.QuestaoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.escolar.CursoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.escolar.UnidadeCurricularRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnidadeCurricularService {
    private final UnidadeCurricularRepository unidadeCurricularRepository;
    private final CursoRepository cursoRepository;
    private final QuestaoRepository questaoRepository;

    public UnidadeCurricular cadastrarUnidadeCurricular(UnidadeCurricularDTO dto) {
        if (dto.cursoId() == null){
            throw new RuntimeException("Uma UC deve pertencer a um Curso.");
        }

        UnidadeCurricular uc = dto.fromDTO();
        associarRelacionamentos(uc, dto);

        return unidadeCurricularRepository.save(uc);
    }

    public List<UnidadeCurricularDTO> listarUnidadesCurricularesAtivas() {
        return unidadeCurricularRepository.findByStatusTrue()
                .stream()
                .map(
                        UnidadeCurricularDTO::toDTO
                )
                .collect(
                        Collectors.toList()
                );
    }

    public Optional<UnidadeCurricularDTO> buscarPorId(Long id) {
        return unidadeCurricularRepository.findById(id)
                .map(
                        UnidadeCurricularDTO::toDTO
                );
    }

    public UnidadeCurricularDTO atualizarUnidadeCurricular(Long id, UnidadeCurricularDTO dto) {
        return unidadeCurricularRepository.findById(id)
                .map(
                        uc -> {
                            atualizarInfos(uc, dto);
                            associarRelacionamentos(uc, dto);
                            UnidadeCurricular unidadeCurricularAtualizada = unidadeCurricularRepository.save(uc);
                            return UnidadeCurricularDTO.toDTO(unidadeCurricularAtualizada);
                        }
                )
                .orElseThrow(() -> new EntidadeNaoEncontradaException("UC com o ID:" + id + " não encontrada."));
    }

    public boolean inativarUnidadeCurricular(Long id) {
        return unidadeCurricularRepository.findById(id)
                .filter(
                        UnidadeCurricular::isStatus
                )
                .map(
                        uc -> {
                            uc.setStatus(false);
                            unidadeCurricularRepository.save(uc);
                            return true;
                        }
                )
                .orElse(false);
    }

    public boolean reativarUnidadeCurricular(Long id) {
        return unidadeCurricularRepository.findById(id)
                .filter(
                        uc -> !uc.isStatus()
                )
                .map(
                        uc -> {
                            uc.setStatus(true);
                            unidadeCurricularRepository.save(uc);
                            return true;
                        }
                )
                .orElse(false);
    }

    private void atualizarInfos(UnidadeCurricular uc, UnidadeCurricularDTO dto) {
        if (dto.nome() != null && !dto.nome().isBlank()) {
            uc.setNome(dto.nome());
        }
    }

    private void associarRelacionamentos(UnidadeCurricular uc, UnidadeCurricularDTO dto){
        if (dto.cursoId() != null){
            Curso curso = cursoRepository.findById(dto.cursoId())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso referente ao ID: " + dto.cursoId() + " não encontrado."));
            uc.setCurso(curso);
        }

        List<Questao> novasQuestoes;
        if (dto.questoesId() != null && !dto.questoesId().isEmpty()) {
            novasQuestoes = questaoRepository.findAllById(dto.questoesId());
            if (novasQuestoes.size() != dto.questoesId().size()) {
                throw new EntidadeNaoEncontradaException("Uma ou mais questões da lista não foram encontradas.");
            }
        } else {
            novasQuestoes = new ArrayList<>(); // Cria uma lista vazia se o DTO mandou 'null' ou '[]'
        }

        // 3. Remover associações antigas (Atualizando o lado "Dono")
        // Pega a lista de questões que ESTAVAM no banco...
        List<Questao> velhasQuestoes = new ArrayList<>(uc.getQuestoes());
        for (Questao questaoAntiga : velhasQuestoes) {
            // Se a questão antiga NÃO ESTÁ na nova lista...
            if (!novasQuestoes.contains(questaoAntiga)) {
                questaoAntiga.setUnidadeCurricular(null); // ...remove a referência nela (o "dono")
            }
        }

        // 4. Adicionar associações novas (Atualizando o lado "Dono")
        for (Questao novaQuestao : novasQuestoes) {
            novaQuestao.setUnidadeCurricular(uc); // ...adiciona a referência nela (o "dono")
        }

        // 5. Sincronizar a lista em memória (o lado "Inverso")
        uc.getQuestoes().clear();
        uc.getQuestoes().addAll(novasQuestoes);
    }
}