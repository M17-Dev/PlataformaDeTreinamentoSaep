package com.senai.plataforma_de_treinamento_saep.service.atividade;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.QuestaoDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.RespostaDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.atividade.QuestaoService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Prova;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Resposta;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.Curso;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import com.senai.plataforma_de_treinamento_saep.domain.enums.NivelDeDificuldade;
import com.senai.plataforma_de_treinamento_saep.domain.exception.RegraDeNegocioException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.ProvaRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.QuestaoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.escolar.UnidadeCurricularRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.AlunoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestaoServiceTest {

    @InjectMocks
    private QuestaoService questaoService;

    @Mock
    private QuestaoRepository questaoRepo;

    @Mock
    private UnidadeCurricularRepository ucRepo;

    @Mock
    private UsuarioRepository usuarioRepo;

    @Mock
    private ProvaRepository provaRepo;

    @Mock
    private AlunoRepository alunoRepo;

    private Usuario criarProfessorMock() {
        Professor p = new Professor();
        p.setId(1L);
        return p;
    }

    private UnidadeCurricular criarUCMock(Long id) {
        UnidadeCurricular uc = new UnidadeCurricular();
        uc.setId(id);
        uc.setNome("UC Teste");
        uc.setCurso(new Curso(1L, "Curso TI", true, new ArrayList<>(), new ArrayList<>()));
        return uc;
    }

    private List<RespostaDTO> criar5RespostasValidas() {
        List<RespostaDTO> lista = new ArrayList<>();
        lista.add(new RespostaDTO(null, "Errada", null, false));
        lista.add(new RespostaDTO(null, "Errada", null, false));
        lista.add(new RespostaDTO(null, "Errada", null, false));
        lista.add(new RespostaDTO(null, "Errada", null, false));
        lista.add(new RespostaDTO(null, "Certa", null, true));
        return lista;
    }

    @Test
    @DisplayName("Deve cadastrar Questão com sucesso (alocando em prova nova)")
    void deveCadastrarQuestao() {
        QuestaoDTO dto = new QuestaoDTO(
                null, "Intro", "Pergunta?", "img.png", 1L,
                criar5RespostasValidas(), 1L, NivelDeDificuldade.MEDIO, true
        );

        Usuario prof = criarProfessorMock();
        UnidadeCurricular uc = criarUCMock(1L);
        Questao questaoSalva = new Questao();
        questaoSalva.setId(100L);
        questaoSalva.setUnidadeCurricular(uc);
        questaoSalva.setNivelDeDificuldade(NivelDeDificuldade.MEDIO);
        questaoSalva.setRespostas(new ArrayList<>());

        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(prof));
        when(ucRepo.findById(1L)).thenReturn(Optional.of(uc));
        when(ucRepo.existsById(1L)).thenReturn(true);

        when(questaoRepo.save(any(Questao.class))).thenReturn(questaoSalva);

        when(provaRepo.findFirstByUnidadeCurricularAndNivelDeDificuldadeAndStatusTrueAndQtdQuestoesLessThanOrderByIdProvaAsc(
                eq(uc), eq(NivelDeDificuldade.MEDIO), eq(5)
        )).thenReturn(Optional.empty());

        when(alunoRepo.findAllByCursoId(1L)).thenReturn(Collections.emptyList());

        QuestaoDTO resultado = questaoService.cadastrarQuestao(dto, 1L);

        assertNotNull(resultado);
        verify(questaoRepo).save(any(Questao.class));
        verify(provaRepo).save(any(Prova.class));
    }

    @Test
    @DisplayName("Deve alocar questão em prova existente")
    void deveAlocarEmProvaExistente() {
        QuestaoDTO dto = new QuestaoDTO(
                null, "Intro", "Pergunta?", null, 1L,
                criar5RespostasValidas(), 1L, NivelDeDificuldade.FACIL, true
        );

        Usuario prof = criarProfessorMock();
        UnidadeCurricular uc = criarUCMock(1L);
        Questao questaoSalva = new Questao();
        questaoSalva.setUnidadeCurricular(uc);
        questaoSalva.setNivelDeDificuldade(NivelDeDificuldade.FACIL);
        questaoSalva.setRespostas(new ArrayList<>());

        Prova provaExistente = new Prova();
        provaExistente.setIdProva(50L);
        provaExistente.setQtdQuestoes(2);
        provaExistente.setQuestoes(new ArrayList<>());

        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(prof));
        when(ucRepo.findById(1L)).thenReturn(Optional.of(uc));
        when(ucRepo.existsById(1L)).thenReturn(true);
        when(questaoRepo.save(any(Questao.class))).thenReturn(questaoSalva);

        when(provaRepo.findFirstByUnidadeCurricularAndNivelDeDificuldadeAndStatusTrueAndQtdQuestoesLessThanOrderByIdProvaAsc(
                any(), any(), eq(5)
        )).thenReturn(Optional.of(provaExistente));

        questaoService.cadastrarQuestao(dto, 1L);

        assertEquals(1, provaExistente.getQuestoes().size());
        verify(provaRepo).save(provaExistente);
    }

    @Test
    @DisplayName("Deve falhar se não tiver 5 respostas")
    void deveFalharSem5Respostas() {
        List<RespostaDTO> respostas = new ArrayList<>();
        respostas.add(new RespostaDTO(null, "1", null, true));

        QuestaoDTO dto = new QuestaoDTO(
                null, "I", "P", null, 1L, respostas, 1L, NivelDeDificuldade.MEDIO, true
        );

        when(ucRepo.existsById(1L)).thenReturn(true);

        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class,
                () -> questaoService.cadastrarQuestao(dto, 1L));

        assertEquals("A questão deve ter exatamente 5 respostas!", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar se não tiver resposta correta")
    void deveFalharSemRespostaCorreta() {
        List<RespostaDTO> respostas = new ArrayList<>();

        respostas.add(new RespostaDTO(null, "Opcao 1", null, false));
        respostas.add(new RespostaDTO(null, "Opcao 2", null, false));
        respostas.add(new RespostaDTO(null, "Opcao 3", null, false));
        respostas.add(new RespostaDTO(null, "Opcao 4", null, false));
        respostas.add(new RespostaDTO(null, "Opcao 5", null, false));

        QuestaoDTO dto = new QuestaoDTO(
                null, "I", "P", null, 1L, respostas, 1L, NivelDeDificuldade.MEDIO, true
        );

        when(ucRepo.existsById(1L)).thenReturn(true);

        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class,
                () -> questaoService.cadastrarQuestao(dto, 1L));

        assertEquals("A questão deve conter uma resposta correta!", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar se tiver mais de uma correta")
    void deveFalharMuitasCorretas() {
        List<RespostaDTO> respostas = new ArrayList<>();
        respostas.add(new RespostaDTO(null, "Certa 1", null, true));
        respostas.add(new RespostaDTO(null, "Certa 2", null, true));
        respostas.add(new RespostaDTO(null, "Errada", null, true));
        respostas.add(new RespostaDTO(null, "Errada", null, true));
        respostas.add(new RespostaDTO(null, "Errada", null, true));

        QuestaoDTO dto = new QuestaoDTO(
                null, "I", "P", null, 1L, respostas, 1L, NivelDeDificuldade.MEDIO, true
        );

        when(ucRepo.existsById(1L)).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> questaoService.cadastrarQuestao(dto, 1L));
    }

    @Test
    @DisplayName("Deve inativar Questão")
    void deveInativarQuestao() {
        Questao q = new Questao();
        q.setId(1L);
        q.setStatus(true);
        q.setRespostas(new ArrayList<>());
        Resposta r = new Resposta(); r.setStatus(true);
        q.getRespostas().add(r);

        when(questaoRepo.findById(1L)).thenReturn(Optional.of(q));

        boolean sucesso = questaoService.inativarQuestao(1L);

        assertTrue(sucesso);
        assertFalse(q.isStatus()); // Questão inativa
        assertFalse(r.isStatus()); // Resposta inativa (cascata)
        verify(questaoRepo).save(q);
    }
}