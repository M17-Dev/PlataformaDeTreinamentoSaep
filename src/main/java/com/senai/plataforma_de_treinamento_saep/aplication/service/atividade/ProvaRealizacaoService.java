package com.senai.plataforma_de_treinamento_saep.aplication.service.atividade;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.RealizacaoProvaDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.*;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.exception.RegraDeNegocioException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.ProvaRealizadaRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.ProvaRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.QuestaoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.RespostaRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProvaRealizacaoService {

    private final ProvaRepository provaRepo;
    private final AlunoRepository alunoRepo;
    private final QuestaoRepository questaoRepo;
    private final RespostaRepository respostaRepo;
    private final ProvaRealizadaRepository provaRealizadaRepo;

    @Transactional
    public RealizacaoProvaDTO.ResultadoProvaDTO entregaProva(Long idProva, Long idAluno, RealizacaoProvaDTO.EntregaProvaDTO dto){

        Prova prova = provaRepo.findById(idProva)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Prova de ID: " + idProva + " não encontrada."));

        Aluno aluno = alunoRepo.findById(idAluno)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluno do ID: " + idAluno + " não encontrado."));

        ProvaRealizada provaRealizada = ProvaRealizada.builder()
                .prova(prova)
                .aluno(aluno)
                .respostasDadas(new ArrayList<>())
                .build();

        int acertos = 0;

        for (RealizacaoProvaDTO.RespostaAlunoDTO respostaAluno : dto.respostas()) {

            Questao questao = questaoRepo.findById(respostaAluno.idQuestao())
                    .orElseThrow(() -> new RegraDeNegocioException("Questão inválida enviada"));

            Resposta respostaEscolhida = respostaRepo.findById(respostaAluno.idRespostaEscolhida())
                    .orElseThrow(() -> new RegraDeNegocioException("Resposta inválida enviada"));

            if (!respostaEscolhida.getQuestao().getId().equals(questao.getId())) {
                throw new RegraDeNegocioException("A resposta escolhida não pertence à questão informada.");
            }

            boolean acertou = respostaEscolhida.isCertoOuErrado();
            if (acertou) {
                acertos++;
            }

            RespostaDada respostaDada = RespostaDada.builder()
                    .provaRealizada(provaRealizada)
                    .questao(questao)
                    .respostaEscolhida(respostaEscolhida)
                    .acertou(acertou)
                    .build();

            provaRealizada.getRespostasDadas().add(respostaDada);
        }

        provaRealizada.setQtdAcertos(acertos);
        provaRealizadaRepo.save(provaRealizada);

        return new RealizacaoProvaDTO.ResultadoProvaDTO(
                provaRealizada.getId(),
                prova.getQtdQuestoes(),
                acertos,
                "Prova finalizada com sucesso!"
        );
    }
}