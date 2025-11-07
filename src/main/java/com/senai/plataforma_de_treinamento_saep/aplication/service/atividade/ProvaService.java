package com.senai.plataforma_de_treinamento_saep.aplication.service.atividade;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.ProvaDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.QuestaoDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Prova;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.enums.NivelDeDificuldade;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.ProvaRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.QuestaoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.escolar.UnidadeCurricularRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProvaService {
    private final ProvaRepository provaRepo;
    private final AlunoRepository alunoRepo;
    private final UnidadeCurricularRepository ucRepo;
    private final QuestaoRepository questaoRepo;


    private ProvaDTO.ProvaResponseDTO cadastrarProva(ProvaDTO.ProvaRequestDTO dto){

    }

    //Métodos referentes ao "Response" da prova
    public Optional<Prova> buscarEntidadeProvaPorId(Long id) {
        return provaRepo.findById(id);
    }

    public ProvaDTO.ProvaResponseDTO retornoCadastro(Long id) {
        Prova prova = buscarEntidadeProvaPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Prova não encontrada."));

        List<QuestaoDTO> questoesFiltradas = buscarQuestoesFiltradas(
                prova.getUnidadeCurricular(),
                prova.getNivelDeDificuldade()
        );
        int qtdQuestoes = questoesFiltradas.size();

        List<Long> alunoIds = prova.getAlunos().stream()
                .map(Aluno::getId)
                .toList();

        Long ucId = (prova.getUnidadeCurricular() != null) ? prova.getUnidadeCurricular().getId() : null;
        String ucNome = (prova.getUnidadeCurricular() != null) ? prova.getUnidadeCurricular().getNome() : null;

        return new ProvaDTO.ProvaResponseDTO(
                prova.getIdProva(),
                prova.getDescricao(),
                prova.getDataProva(),
                alunoIds,
                ucId,
                ucNome,
                qtdQuestoes,
                prova.getQtdAcertos(),
                prova.getNivelDeDificuldade(),
                prova.isStatus(),
                questoesFiltradas
        );
    }

    private List<QuestaoDTO> buscarQuestoesFiltradas(UnidadeCurricular uc, NivelDeDificuldade nivel) {
        if (uc == null || nivel == null) {
            return Collections.emptyList();
        }
        return questaoRepo.findByUnidadeCurricularENivelDeDificuldade(uc, nivel)
                .stream()
                .map(QuestaoDTO::toDTO)
                .toList();
    }
}