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

import java.util.ArrayList;
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

    public ProvaDTO.ProvaResponseDTO atualizarProva(Long id, ProvaDTO.ProvaRequestDTO dto){
        return provaRepo.findById(id)
                .map(
                        prova -> {

                        }
                )
    }

    //Método referente ao "Response" da prova
    private ProvaDTO.ProvaResponseDTO converterProvaParaResponseDto(Prova prova){
        List<Long> alunoIds = (prova.getAlunos() != null) ?
                prova.getAlunos().stream().map(Aluno::getId).toList() : Collections.emptyList();

        Long ucId = (prova.getUnidadeCurricular() != null) ? prova.getUnidadeCurricular().getId() : null;
        String ucNome = (prova.getUnidadeCurricular() != null) ? prova.getUnidadeCurricular().getNome() : null;

        int qtdQuestoes = prova.getQtdQuestoes();

        List<QuestaoDTO> questoesProva = prova.getQuestoes().stream()
                .map(QuestaoDTO::toDTO)
                .toList();

        return new ProvaDTO.ProvaResponseDTO(
                prova.getIdProva(),
                prova.getDescricao(),
                prova.getDataCriacao(),
                prova.getDataUltimaAtualizacao(),
                alunoIds,
                ucId,
                ucNome,
                qtdQuestoes,
                prova.getQtdAcertos(),
                prova.getNivelDeDificuldade(),
                questoesProva,
                prova.isStatus()
        );
    }

    private void associarRelacionamentos(Prova prova, ProvaDTO.ProvaRequestDTO dto){
        UnidadeCurricular uc = ucRepo.findById(dto.unidadeCurricularId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("UC não encontrada."));
        prova.setUnidadeCurricular(uc);

        if (uc.getCurso() == null) {
            throw new RuntimeException("A UC selecionada não está vinculada a nenhum curso.");
        }
        Long cursoId = uc.getCurso().getId();
        List<Aluno> alunosDoCurso = alunoRepo.findAllByCursoId(cursoId);

        prova.setAlunos(alunosDoCurso);

        NivelDeDificuldade nivelDaProva = prova.getNivelDeDificuldade();
        List<Questao> questoesParaAdicionar = new ArrayList<>();

        if (dto.questoesId() != null && !dto.questoesId().isEmpty()) {
            List<Questao> questoesBuscadas = questaoRepo.findAllById(dto.questoesId());

            for (Questao questao : questoesBuscadas) {
                if (questao.getNivelDeDificuldade() != nivelDaProva) {
                    throw new RuntimeException("A questão '" + questao.getTitulo() + "' (" + questao.getNivelDeDificuldade() +
                            ") não pode ser adicionada a uma prova de nível " + nivelDaProva);
                }
                questoesParaAdicionar.add(questao);
            }
        }
        prova.setQuestoes(questoesParaAdicionar);
        prova.setQtdQuestoes(questoesParaAdicionar.size());
    }

    private void atualizarInfos(Prova prova, ProvaDTO.ProvaRequestDTO dto){
        if (prova.getDescricao() != null && !dto.descricao().isBlank()){
            prova.setDescricao(dto.descricao());
        }
    }
}