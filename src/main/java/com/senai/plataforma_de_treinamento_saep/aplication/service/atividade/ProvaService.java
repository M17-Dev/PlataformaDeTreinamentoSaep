package com.senai.plataforma_de_treinamento_saep.aplication.service.atividade;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.ProvaDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.QuestaoDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Prova;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.enums.NivelDeDificuldade;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.exception.RegraDeNegocioException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.ProvaRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.QuestaoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.escolar.UnidadeCurricularRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProvaService {
    private final ProvaRepository provaRepo;
    private final AlunoRepository alunoRepo;
    private final UnidadeCurricularRepository ucRepo;
    private final QuestaoRepository questaoRepo;

    @Transactional
    public ProvaDTO.ProvaResponseDTO cadastrarProva(ProvaDTO.ProvaRequestDTO dto){

        Prova prova = dto.toEntity();
        associarRelacionamentos(prova, dto);
        preencherComQuestoesFlutuantes(prova);

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

    public List<ProvaDTO.ProvaResponseDTO> listarProvasPeloIdDoCurso(Long idCurso){
        return provaRepo.findByCursoId(idCurso).stream()
                .map(
                        this::converterProvaParaResponseDto
                )
                .toList();
    }

    public Optional<ProvaDTO.ProvaResponseDTO> buscarProvaPorId(Long id){
        return Optional.of(provaRepo.findById(id)
                .map(this::converterProvaParaResponseDto)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("A prova de ID: " + id + " não foi encontrada.")));
    }

    @Transactional
    public ProvaDTO.ProvaResponseDTO atualizarProva(Long id, ProvaDTO.AtualizarProvaDTO dto){
        return provaRepo.findById(id)
                .map(
                        prova -> {
                            atualizarInfos(prova, dto);
                            Prova provaAtualizada = provaRepo.save(prova);
                            return converterProvaParaResponseDto(provaAtualizada);
                        }
                )
                .orElseThrow(() -> new EntidadeNaoEncontradaException("A prova de ID: " + id + " não encontrada."));
    }

    @Transactional
    public boolean desativarProva(Long id){
        return provaRepo.findById(id)
                .filter(
                        Prova::isStatus
                )
                .map(
                        prova -> {
                            prova.setStatus(false);
                            provaRepo.save(prova);
                            return true;
                        }
                )
                .orElse(false);
    }

    @Transactional
    public boolean reativarProva(Long id){
        return provaRepo.findById(id)
                .filter(
                        prova -> !prova.isStatus()
                )
                .map(
                        prova -> {
                            prova.setStatus(true);
                            provaRepo.save(prova);
                            return true;
                        }
                )
                .orElse(false);
    }

    @Transactional
    public ProvaDTO.ProvaResponseDTO adicionarQuestaoNaProva(Long idProva, Long idQuestaoASerAdicionada){
        Prova prova = provaRepo.findById(idProva)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("A prova de ID: " + idProva + " não foi encontrada."));

        Questao questao = questaoRepo.findById(idQuestaoASerAdicionada)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("A questão de ID: " + idQuestaoASerAdicionada + " não foi encontrada"));

        validarDadosDaQuestao(prova, questao);
        int questoesAtivasNaProva = provaRepo.countActiveQuestionsByProvaId(idProva);

        if (questoesAtivasNaProva >= 5){
            throw new RegraDeNegocioException("A prova de ID: " + idProva + " já atingiu o limite de 5 questões ATIVAS.");
        }

        prova.getQuestoes().add(questao);
        prova.setQtdQuestoes(prova.getQuestoes().size());

        Prova provaAtualizada = provaRepo.save(prova);

        return converterProvaParaResponseDto(provaAtualizada);
    }

    @Transactional
    public ProvaDTO.ProvaResponseDTO substituirQuestao(Long idProva,Long idQuestaoASerAtualizada, Long idNovaQuestao){
        Prova prova = provaRepo.findById(idProva)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("A prova de ID: " + idProva + " não foi encontrada"));

        Questao novaQuestao = questaoRepo.findById(idNovaQuestao)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("A questao de ID: " + idNovaQuestao + " não encontrada."));

        validarDadosDaQuestao(prova, novaQuestao);

        Questao questaoAntiga = prova.getQuestoes().stream()
                .filter(q -> q.getId().equals(idQuestaoASerAtualizada))
                .findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("A questão de ID: " + idQuestaoASerAtualizada + " não foi encontrada"));

        prova.getQuestoes().remove(questaoAntiga);
        prova.getQuestoes().add(novaQuestao);

        Prova provaAtualizada = provaRepo.save(prova);

        return converterProvaParaResponseDto(provaAtualizada);
    }

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
                prova.getNivelDeDificuldade(),
                questoesProva,
                prova.isStatus()
        );
    }

    private void associarRelacionamentos(Prova prova, ProvaDTO.ProvaRequestDTO dto){
        UnidadeCurricular uc = ucRepo.findById(dto.unidadeCurricularId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("UC não encontrada."));
        prova.setUnidadeCurricular(uc);

        Long cursoId = uc.getCurso().getId();
        List<Aluno> alunosDoCurso = alunoRepo.findAllByCursoId(cursoId);

        prova.setAlunos(alunosDoCurso);

        NivelDeDificuldade nivelDaProva = prova.getNivelDeDificuldade();
        List<Questao> questoesParaAdicionar = new ArrayList<>();

        if (dto.questoesId() != null && !dto.questoesId().isEmpty()) {
            List<Questao> questoesBuscadas = questaoRepo.findAllById(dto.questoesId());

            for (Questao questao : questoesBuscadas) {
                if (questao.getNivelDeDificuldade() != nivelDaProva) {
                    throw new RegraDeNegocioException("A questão '" + questao.getId() + "' (" + questao.getNivelDeDificuldade() +
                            ") não pode ser adicionada a uma prova de nível " + nivelDaProva);
                }
                questoesParaAdicionar.add(questao);
            }
        }
        prova.setQuestoes(questoesParaAdicionar);
        prova.setQtdQuestoes(questoesParaAdicionar.size());
    }

    private void atualizarInfos(Prova prova, ProvaDTO.AtualizarProvaDTO dto){
        if (prova.getDescricao() != null && !dto.descricao().isBlank()){
            prova.setDescricao(dto.descricao());
        }
    }

    private void validarDadosDaQuestao(Prova prova, Questao questao){
        if (!questao.getUnidadeCurricular().getId().equals(prova.getUnidadeCurricular().getId())){
            throw new RegraDeNegocioException("A nova questão não pertence à mesma Unidade Curricular da prova.");
        }
        if (!questao.getNivelDeDificuldade().equals(prova.getNivelDeDificuldade())){
            throw new RegraDeNegocioException("O nível de dificuldade da nova questão (" + questao.getNivelDeDificuldade() + ") não é o mesmo da prova (" + prova.getNivelDeDificuldade() + ").");
        }
    }

    private void preencherComQuestoesFlutuantes(Prova prova) {
        int questoesAtuais = prova.getQuestoes().size();
        final int LIMITE_QUESTOES = 5;
        int vagasRestantes = LIMITE_QUESTOES - questoesAtuais;

        if (vagasRestantes <= 0) {
            return;
        }

        Pageable limiteBusca = PageRequest.of(0, vagasRestantes, Sort.by("id").ascending());

        List<Questao> questoesFlutuantes = questaoRepo.findByUnidadeCurricularAndNivelDeDificuldadeAndStatusTrueAndProvasIsEmpty(
                prova.getUnidadeCurricular(),
                prova.getNivelDeDificuldade(),
                limiteBusca
        );

        if (!questoesFlutuantes.isEmpty()) {
            prova.getQuestoes().addAll(questoesFlutuantes);
            prova.setQtdQuestoes(prova.getQuestoes().size());
        }
    }
}