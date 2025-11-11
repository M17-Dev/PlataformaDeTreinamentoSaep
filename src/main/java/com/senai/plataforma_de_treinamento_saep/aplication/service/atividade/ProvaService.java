package com.senai.plataforma_de_treinamento_saep.aplication.service.atividade;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.ProvaDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.QuestaoDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Prova;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProvaService {
    private final ProvaRepository provaRepo;
    private final AlunoRepository alunoRepo;
    private final UnidadeCurricularRepository ucRepo;
    private final QuestaoRepository questaoRepo;


    public ProvaDTO.ProvaResponseDTO cadastrarProva(ProvaDTO.ProvaRequestDTO dto){

        Prova prova = dto.toEntity();
        associarRelacionamentos(prova, dto);

        Prova provaSalva = provaRepo.save(prova);

        return converterProvaParaResponseDto(provaSalva);
    }

    public List<ProvaDTO.ProvaResponseDTO> listarProvasAtivas(){
        return provaRepo.findByStatusTrue()
                .stream()
                .map(
                        this::converterProvaParaResponseDto
                ).collect(
                        Collectors.toList()
                );
    }

    public Optional<ProvaDTO.ProvaResponseDTO> buscarProvaPorId(Long id){
        return Optional.of(provaRepo.findById(id)
                .map(this::converterProvaParaResponseDto)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("A prova de ID: " + id + " não foi encontrada.")));
    }

    //Métodos referentes ao "Response" da prova
    private ProvaDTO.ProvaResponseDTO converterProvaParaResponseDto(Prova prova){
        List<QuestaoDTO> questoesFiltradas = buscarQuestoesFiltradas(
                prova.getUnidadeCurricular(),
                prova.getNivelDeDificuldade()
        );
        int qtdQuestoes = questoesFiltradas.size();

        List<Long> alunoIds = (prova.getAlunos() != null) ?
                prova.getAlunos().stream().map(Aluno::getId).toList() : Collections.emptyList();

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

    /*private Optional<ProvaDTO.ProvaResponseDTO> buscarEntidadeProvaPorId(Long id) {
        Optional<Prova> prova = provaRepo.findById(id);
        return prova.map(this::converterProvaParaResponseDto);
    }*/

    private List<QuestaoDTO> buscarQuestoesFiltradas(UnidadeCurricular uc, NivelDeDificuldade nivel) {
        if (uc == null || nivel == null) {
            return Collections.emptyList();
        }
        return questaoRepo.findByUnidadeCurricularAndNivelDeDificuldade(uc, nivel)
                .stream()
                .map(QuestaoDTO::toDTO)
                .toList();
    }

    private void associarRelacionamentos(Prova prova, ProvaDTO.ProvaRequestDTO dto){
        UnidadeCurricular uc = ucRepo.findById(dto.unidadeCurricularId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("UC não encontrada."));
        prova.setUnidadeCurricular(uc);

        if (dto.alunoIds() != null && !dto.alunoIds().isEmpty()) {
            List<Aluno> alunos = alunoRepo.findAllById(dto.alunoIds());

            if (alunos.size() != dto.alunoIds().size()) {
                throw new EntidadeNaoEncontradaException("Um ou mais Alunos da lista não foram encontrados.");
            }

            prova.setAlunos(alunos);
        }

        List<Questao> questoesFiltradas = questaoRepo.findByUnidadeCurricularAndNivelDeDificuldade(
                uc,
                dto.nivelDeDificuldade()
        );
        prova.setQtdQuestoes(questoesFiltradas.size());
    }
}