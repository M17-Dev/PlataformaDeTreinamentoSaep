package com.senai.plataforma_de_treinamento_saep.service.escolar;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.escolar.UnidadeCurricularDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.escolar.UnidadeCurricularService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.atividade.Questao;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.Curso;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.UnidadeCurricular;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.exception.RegraDeNegocioException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.QuestaoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.escolar.CursoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.escolar.UnidadeCurricularRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnidadeCurricularServiceTest {

    @InjectMocks
    private UnidadeCurricularService ucService;

    @Mock
    private UnidadeCurricularRepository ucRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private QuestaoRepository questaoRepository;

    private Curso criarCursoMock() {
        Curso c = new Curso();
        c.setId(1L);
        c.setNome("Curso Teste");
        return c;
    }

    private UnidadeCurricular criarUCMock(Long id, String nome) {
        UnidadeCurricular uc = new UnidadeCurricular();
        uc.setId(id);
        uc.setNome(nome);
        uc.setFraseDaUc("Aprenda Java");
        uc.setStatus(true);
        uc.setCurso(criarCursoMock());
        uc.setQuestoes(new ArrayList<>());
        uc.setProvas(new ArrayList<>());
        return uc;
    }

    @Test
    @DisplayName("Deve cadastrar UC com sucesso (vinculando Curso)")
    void deveCadastrarUC() {
        UnidadeCurricularDTO dto = new UnidadeCurricularDTO(
                null, "Java Básico", "Frase", 1L, null, Collections.emptyList(), null, true
        );

        Curso cursoMock = criarCursoMock();
        UnidadeCurricular ucSalva = criarUCMock(1L, "Java Básico");

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(cursoMock));

        when(ucRepository.save(any(UnidadeCurricular.class))).thenReturn(ucSalva);

        UnidadeCurricularDTO resultado = ucService.cadastrarUnidadeCurricular(dto);

        assertNotNull(resultado);
        assertEquals("Java Básico", resultado.nome());
        assertEquals(1L, resultado.cursoId());

        verify(cursoRepository).findById(1L);
        verify(ucRepository).save(any());
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar UC sem ID do Curso")
    void deveFalharCadastroSemCursoId() {
        UnidadeCurricularDTO dto = new UnidadeCurricularDTO(
                null, "Java", "Frase", null, null, Collections.emptyList(), null, true
        );

        assertThrows(RegraDeNegocioException.class, () -> ucService.cadastrarUnidadeCurricular(dto));

        verify(ucRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar UC se Curso não existir")
    void deveFalharCadastroCursoInexistente() {
        UnidadeCurricularDTO dto = new UnidadeCurricularDTO(
                null, "Java", "Frase", 99L, null, Collections.emptyList(), null, true
        );

        when(cursoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class, () -> ucService.cadastrarUnidadeCurricular(dto));
    }

    @Test
    @DisplayName("Deve listar UCs ativas")
    void deveListarUCs() {
        UnidadeCurricular uc = criarUCMock(1L, "Web");
        when(ucRepository.findByStatusTrue()).thenReturn(List.of(uc));

        List<UnidadeCurricularDTO> lista = ucService.listarUnidadesCurricularesAtivas();

        assertFalse(lista.isEmpty());
        assertEquals("Web", lista.getFirst().nome());
    }

    @Test
    @DisplayName("Deve listar UCs por Curso")
    void deveListarUCsPorCurso() {
        UnidadeCurricular uc = criarUCMock(1L, "Web");
        when(ucRepository.findByCursoIdAndStatusTrue(1L)).thenReturn(List.of(uc));

        List<UnidadeCurricularDTO> lista = ucService.listarUnidadesCurricularesDoCurso(1L);

        assertEquals(1, lista.size());
    }

    @Test
    @DisplayName("Deve atualizar UC com sucesso")
    void deveAtualizarUC() {
        Long id = 1L;
        UnidadeCurricularDTO dtoUpdate = new UnidadeCurricularDTO(
                id, "Java Avançado", "Nova Frase", 1L, null, Collections.emptyList(), null, true
        );

        UnidadeCurricular ucAntiga = criarUCMock(id, "Java Básico");
        UnidadeCurricular ucNova = criarUCMock(id, "Java Avançado");
        Curso cursoMock = criarCursoMock();

        when(ucRepository.findById(id)).thenReturn(Optional.of(ucAntiga));
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(cursoMock));
        when(ucRepository.save(any())).thenReturn(ucNova);

        UnidadeCurricularDTO resultado = ucService.atualizarUnidadeCurricular(id, dtoUpdate);

        assertEquals("Java Avançado", resultado.nome());
    }

    @Test
    @DisplayName("Deve inativar UC")
    void deveInativarUC() {
        Long id = 1L;
        UnidadeCurricular uc = criarUCMock(id, "UC");

        when(ucRepository.findById(id)).thenReturn(Optional.of(uc));

        boolean sucesso = ucService.inativarUnidadeCurricular(id);

        assertTrue(sucesso);
        assertFalse(uc.isStatus());
        verify(ucRepository).save(uc);
    }

    @Test
    @DisplayName("Deve vincular Questões na criação (Teste de Associação)")
    void deveVincularQuestoes() {
        List<Long> idsQuestoes = List.of(100L);
        UnidadeCurricularDTO dto = new UnidadeCurricularDTO(
                null, "Lógica", "", 1L, null,
                idsQuestoes, null, true
        );

        Curso cursoMock = criarCursoMock();
        UnidadeCurricular ucSalva = criarUCMock(1L, "Lógica");

        Questao q1 = new Questao();
        q1.setId(100L);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(cursoMock));

        when(questaoRepository.findAllById(idsQuestoes)).thenReturn(List.of(q1));

        when(ucRepository.save(any())).thenReturn(ucSalva);

        ucService.cadastrarUnidadeCurricular(dto);

        verify(questaoRepository).findAllById(idsQuestoes);
    }
}