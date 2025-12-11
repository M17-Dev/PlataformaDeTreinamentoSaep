package com.senai.plataforma_de_treinamento_saep.service.atividade;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.RealizacaoProvaDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.atividade.ProvaRealizacaoService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Prova;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.ProvaRealizada;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Resposta;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.exception.RegraDeNegocioException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.ProvaRealizadaRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.ProvaRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.QuestaoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.RespostaRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.AlunoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RealizarProvaServiceTest {

    @InjectMocks
    private ProvaRealizacaoService service;

    @Mock private ProvaRepository provaRepo;
    @Mock private AlunoRepository alunoRepo;
    @Mock private QuestaoRepository questaoRepo;
    @Mock private RespostaRepository respostaRepo;
    @Mock private ProvaRealizadaRepository provaRealizadaRepo;

    private Questao criarQuestaoMock(Long id) {
        Questao q = new Questao();
        q.setId(id);
        return q;
    }

    private Resposta criarRespostaMock(Long id, boolean correta, Questao questaoDona) {
        Resposta r = new Resposta();
        r.setId(id);
        r.setCertoOuErrado(correta);
        r.setQuestao(questaoDona);
        return r;
    }

    @Test
    @DisplayName("Deve entregar prova calculando nota corretamente (1 Certa / 1 Errada)")
    void deveEntregarProvaComSucesso() {
        Long idProva = 1L;
        Long idAluno = 1L;

        Prova prova = new Prova(); prova.setIdProva(idProva); prova.setQtdQuestoes(2);
        Aluno aluno = new Aluno(); aluno.setId(idAluno);

        Questao q1 = criarQuestaoMock(100L);
        Resposta r1Certa = criarRespostaMock(10L, true, q1);

        Questao q2 = criarQuestaoMock(200L);
        Resposta r2Errada = criarRespostaMock(20L, false, q2);

        List<RealizacaoProvaDTO.RespostaAlunoDTO> respostasAluno = List.of(
                new RealizacaoProvaDTO.RespostaAlunoDTO(100L, 10L), // Acertou
                new RealizacaoProvaDTO.RespostaAlunoDTO(200L, 20L)  // Errou
        );
        RealizacaoProvaDTO.EntregaProvaDTO dtoEntrega = new RealizacaoProvaDTO.EntregaProvaDTO(idAluno, respostasAluno);

        when(provaRepo.findById(idProva)).thenReturn(Optional.of(prova));
        when(alunoRepo.findById(idAluno)).thenReturn(Optional.of(aluno));

        when(questaoRepo.findById(100L)).thenReturn(Optional.of(q1));
        when(respostaRepo.findById(10L)).thenReturn(Optional.of(r1Certa));

        when(questaoRepo.findById(200L)).thenReturn(Optional.of(q2));
        when(respostaRepo.findById(20L)).thenReturn(Optional.of(r2Errada));

        when(provaRealizadaRepo.save(any(ProvaRealizada.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RealizacaoProvaDTO.ResultadoProvaDTO resultado = service.entregaProva(idProva, idAluno, dtoEntrega);

        assertNotNull(resultado);
        assertEquals(1, resultado.acertos()); // Deve ter 1 acerto
        assertEquals(2, resultado.totalQuestoes()); // Eram 2 questões
        assertEquals("Prova finalizada com sucesso!", resultado.mensagem());

        verify(provaRealizadaRepo).save(any(ProvaRealizada.class));
    }

    @Test
    @DisplayName("Deve falhar se a resposta não pertencer à questão")
    void deveFalharRespostaInvalida() {
        Long idProva = 1L;
        Long idAluno = 1L;

        Prova prova = new Prova();
        Aluno aluno = new Aluno();

        Questao qA = criarQuestaoMock(100L);

        Questao qB = criarQuestaoMock(200L);

        Resposta rDeB = criarRespostaMock(20L, true, qB);

        List<RealizacaoProvaDTO.RespostaAlunoDTO> respostasAluno = List.of(
                new RealizacaoProvaDTO.RespostaAlunoDTO(100L, 20L)
        );
        RealizacaoProvaDTO.EntregaProvaDTO dto = new RealizacaoProvaDTO.EntregaProvaDTO(idAluno, respostasAluno);

        when(provaRepo.findById(idProva)).thenReturn(Optional.of(prova));
        when(alunoRepo.findById(idAluno)).thenReturn(Optional.of(aluno));

        when(questaoRepo.findById(100L)).thenReturn(Optional.of(qA));
        when(respostaRepo.findById(20L)).thenReturn(Optional.of(rDeB));

        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class,
                () -> service.entregaProva(idProva, idAluno, dto));

        assertEquals("A resposta escolhida não pertence à questão informada.", ex.getMessage());

        verify(provaRealizadaRepo, never()).save(any());
    }
}