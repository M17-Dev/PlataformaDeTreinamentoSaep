package com.senai.plataforma_de_treinamento_saep.service.atividade;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.ProvaDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.atividade.ProvaService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Prova;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.Curso;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;
import com.senai.plataforma_de_treinamento_saep.domain.enums.NivelDeDificuldade;
import com.senai.plataforma_de_treinamento_saep.domain.exception.RegraDeNegocioException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.ProvaRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.QuestaoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.escolar.UnidadeCurricularRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.AlunoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProvaServiceTest {

    @InjectMocks
    private ProvaService provaService;

    @Mock
    private ProvaRepository provaRepo;

    @Mock
    private AlunoRepository alunoRepo;

    @Mock
    private UnidadeCurricularRepository ucRepo;

    @Mock
    private QuestaoRepository questaoRepo;

    private UnidadeCurricular criarUCMock(Long id) {
        Curso curso = new Curso();
        curso.setId(1L);
        curso.setNome("Curso TI");

        UnidadeCurricular uc = new UnidadeCurricular();
        uc.setId(id);
        uc.setNome("Lógica");
        uc.setCurso(curso);
        return uc;
    }

    private Questao criarQuestaoMock(Long id, NivelDeDificuldade nivel, UnidadeCurricular uc) {
        return Questao.builder()
                .id(id)
                .pergunta("Questão teste")
                .nivelDeDificuldade(nivel)
                .unidadeCurricular(uc)
                .status(true)
                .respostas(new ArrayList<>())
                .build();
    }

    private Prova criarProvaMock(Long id, NivelDeDificuldade nivel) {
        UnidadeCurricular uc = criarUCMock(1L);
        return Prova.builder()
                .idProva(id)
                .descricao("Prova Teste")
                .nivelDeDificuldade(nivel)
                .unidadeCurricular(uc)
                .questoes(new ArrayList<>())
                .alunos(new ArrayList<>())
                .status(true)
                .dataCriacao(LocalDate.now())
                .dataUltimaAtualizacao(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar Prova com sucesso (e puxar alunos/questões)")
    void deveCadastrarProva() {
        List<Long> idsQuestoes = List.of(100L);
        ProvaDTO.ProvaRequestDTO dto = new ProvaDTO.ProvaRequestDTO(
                "Prova 1", null, 1L, NivelDeDificuldade.MEDIO, idsQuestoes
        );

        UnidadeCurricular ucMock = criarUCMock(1L);
        Questao questaoMock = criarQuestaoMock(100L, NivelDeDificuldade.MEDIO, ucMock);
        Prova provaSalva = criarProvaMock(1L, NivelDeDificuldade.MEDIO);

        when(ucRepo.findById(1L)).thenReturn(Optional.of(ucMock));
        when(alunoRepo.findAllByCursoId(1L)).thenReturn(new ArrayList<>());
        when(questaoRepo.findAllById(idsQuestoes)).thenReturn(List.of(questaoMock));

        when(questaoRepo.findByUnidadeCurricularAndNivelDeDificuldadeAndStatusTrueAndProvasIsEmpty(
                any(), any(), any(Pageable.class))).thenReturn(new ArrayList<>());

        when(provaRepo.save(any(Prova.class))).thenReturn(provaSalva);

        ProvaDTO.ProvaResponseDTO resultado = provaService.cadastrarProva(dto);

        assertNotNull(resultado);
        verify(provaRepo).save(any(Prova.class));
        verify(alunoRepo).findAllByCursoId(1L);
    }

    @Test
    @DisplayName("Deve falhar cadastro se Questão tiver dificuldade diferente")
    void deveFalharCadastroDificuldadeDiferente() {
        List<Long> idsQuestoes = List.of(100L);
        ProvaDTO.ProvaRequestDTO dto = new ProvaDTO.ProvaRequestDTO(
                "Prova Errada", null, 1L, NivelDeDificuldade.FACIL, idsQuestoes
        );

        UnidadeCurricular ucMock = criarUCMock(1L);
        Questao questaoDificil = criarQuestaoMock(100L, NivelDeDificuldade.DIFICIL, ucMock);

        when(ucRepo.findById(1L)).thenReturn(Optional.of(ucMock));
        when(alunoRepo.findAllByCursoId(1L)).thenReturn(new ArrayList<>());
        when(questaoRepo.findAllById(idsQuestoes)).thenReturn(List.of(questaoDificil));

        assertThrows(RegraDeNegocioException.class, () -> provaService.cadastrarProva(dto));

        verify(provaRepo, never()).save(any());
    }

    @Test
    @DisplayName("Deve adicionar questão na prova com sucesso")
    void deveAdicionarQuestao() {
        Long idProva = 1L;
        Long idQuestao = 200L;

        Prova prova = criarProvaMock(idProva, NivelDeDificuldade.MEDIO);
        Questao novaQuestao = criarQuestaoMock(idQuestao, NivelDeDificuldade.MEDIO, prova.getUnidadeCurricular());

        when(provaRepo.findById(idProva)).thenReturn(Optional.of(prova));
        when(questaoRepo.findById(idQuestao)).thenReturn(Optional.of(novaQuestao));
        when(provaRepo.countActiveQuestionsByProvaId(idProva)).thenReturn(2);
        when(provaRepo.save(any())).thenReturn(prova);

        ProvaDTO.ProvaResponseDTO res = provaService.adicionarQuestaoNaProva(idProva, idQuestao);

        assertNotNull(res);
        assertEquals(1, prova.getQuestoes().size());
    }

    @Test
    @DisplayName("Deve bloquear adição de questão se limite (5) foi atingido")
    void deveBloquearLimiteQuestoes() {
        Long idProva = 1L;
        Long idQuestao = 200L;

        Prova prova = criarProvaMock(idProva, NivelDeDificuldade.MEDIO);
        Questao novaQuestao = criarQuestaoMock(idQuestao, NivelDeDificuldade.MEDIO, prova.getUnidadeCurricular());

        when(provaRepo.findById(idProva)).thenReturn(Optional.of(prova));
        when(questaoRepo.findById(idQuestao)).thenReturn(Optional.of(novaQuestao));

        when(provaRepo.countActiveQuestionsByProvaId(idProva)).thenReturn(5);

        assertThrows(RegraDeNegocioException.class, () -> provaService.adicionarQuestaoNaProva(idProva, idQuestao));

        verify(provaRepo, never()).save(any());
    }

    @Test
    @DisplayName("Deve bloquear adição de questão de UC diferente")
    void deveBloquearUCDiferente() {
        Long idProva = 1L;
        Prova prova = criarProvaMock(idProva, NivelDeDificuldade.MEDIO);

        UnidadeCurricular outraUC = criarUCMock(2L);
        Questao questaoErrada = criarQuestaoMock(200L, NivelDeDificuldade.MEDIO, outraUC);

        when(provaRepo.findById(idProva)).thenReturn(Optional.of(prova));
        when(questaoRepo.findById(200L)).thenReturn(Optional.of(questaoErrada));

        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class,
                () -> provaService.adicionarQuestaoNaProva(idProva, 200L));

        assertTrue(ex.getMessage().contains("não pertence à mesma Unidade Curricular"));
    }

    @Test
    @DisplayName("Deve substituir questão com sucesso")
    void deveSubstituirQuestao() {
        Long idProva = 1L;
        Long idAntiga = 100L;
        Long idNova = 200L;

        Prova prova = criarProvaMock(idProva, NivelDeDificuldade.MEDIO);

        Questao qAntiga = criarQuestaoMock(idAntiga, NivelDeDificuldade.MEDIO, prova.getUnidadeCurricular());
        prova.getQuestoes().add(qAntiga);

        Questao qNova = criarQuestaoMock(idNova, NivelDeDificuldade.MEDIO, prova.getUnidadeCurricular());

        when(provaRepo.findById(idProva)).thenReturn(Optional.of(prova));
        when(questaoRepo.findById(idNova)).thenReturn(Optional.of(qNova));
        when(provaRepo.save(any())).thenReturn(prova);

        provaService.substituirQuestao(idProva, idAntiga, idNova);

        assertEquals(1, prova.getQuestoes().size());
        assertEquals(idNova, prova.getQuestoes().getFirst().getId());
    }

    @Test
    @DisplayName("Deve listar provas do curso")
    void deveListarProvasDoCurso() {
        Prova prova = criarProvaMock(1L, NivelDeDificuldade.FACIL);
        when(provaRepo.findByCursoId(10L)).thenReturn(List.of(prova));

        List<ProvaDTO.ProvaResponseDTO> lista = provaService.listarProvasPeloIdDoCurso(10L);

        assertFalse(lista.isEmpty());
    }

    @Test
    @DisplayName("Deve inativar prova")
    void deveInativarProva() {
        Prova prova = criarProvaMock(1L, NivelDeDificuldade.FACIL);
        when(provaRepo.findById(1L)).thenReturn(Optional.of(prova));

        boolean sucesso = provaService.desativarProva(1L);

        assertTrue(sucesso);
        assertFalse(prova.isStatus());
    }
}