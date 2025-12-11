package com.senai.plataforma_de_treinamento_saep.service.escolar;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.escolar.CursoDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.escolar.CursoService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.Curso;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.escolar.CursoRepository;
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
class CursoServiceTest {

    @InjectMocks
    private CursoService cursoService;

    @Mock
    private CursoRepository cursoRepo;

    private Curso criarCursoMock(Long id, String nome) {
        Curso curso = new Curso();
        curso.setId(id);
        curso.setNome(nome);
        curso.setStatus(true);
        curso.setUnidadesCurriculares(new ArrayList<>());
        curso.setAlunos(new ArrayList<>());
        return curso;
    }

    @Test
    @DisplayName("Deve cadastrar curso com sucesso")
    void deveCadastrarCurso() {
        CursoDTO dtoEntrada = new CursoDTO(null, "Técnico em Redes", Collections.emptyList(), Collections.emptyList(), true);
        Curso cursoSalvo = criarCursoMock(1L, "Técnico em Redes");

        when(cursoRepo.save(any(Curso.class))).thenReturn(cursoSalvo);

        CursoDTO resultado = cursoService.cadastrarCurso(dtoEntrada);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Técnico em Redes", resultado.nome());
        verify(cursoRepo).save(any(Curso.class));
    }

    @Test
    @DisplayName("Deve listar cursos ativos")
    void deveListarCursos() {
        Curso c1 = criarCursoMock(1L, "Java");
        when(cursoRepo.findByStatusTrue()).thenReturn(List.of(c1));

        List<CursoDTO> lista = cursoService.listarCursosAtivos();

        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
        assertEquals("Java", lista.getFirst().nome());
    }

    @Test
    @DisplayName("Deve buscar por ID com sucesso")
    void deveBuscarPorId() {
        Curso c1 = criarCursoMock(1L, "Java");
        when(cursoRepo.findById(1L)).thenReturn(Optional.of(c1));

        Optional<CursoDTO> resultado = cursoService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Java", resultado.get().nome());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar ID inexistente")
    void deveRetornarVazioBuscaId() {
        when(cursoRepo.findById(99L)).thenReturn(Optional.empty());

        Optional<CursoDTO> resultado = cursoService.buscarPorId(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve atualizar curso com sucesso")
    void deveAtualizarCurso() {
        Long id = 1L;
        CursoDTO dtoUpdate = new CursoDTO(id, "Nome Editado", null, null, true);

        Curso cursoAntigo = criarCursoMock(id, "Nome Antigo");
        Curso cursoNovo = criarCursoMock(id, "Nome Editado");

        when(cursoRepo.findById(id)).thenReturn(Optional.of(cursoAntigo));
        when(cursoRepo.save(any(Curso.class))).thenReturn(cursoNovo);

        CursoDTO resultado = cursoService.atualizarCurso(id, dtoUpdate);

        assertEquals("Nome Editado", resultado.nome());
    }

    @Test
    @DisplayName("Deve falhar ao atualizar curso inexistente")
    void deveFalharAtualizacao() {
        Long id = 99L;
        CursoDTO dto = new CursoDTO(id, "Nome", null, null, true);

        when(cursoRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class, () -> cursoService.atualizarCurso(id, dto));
    }

    @Test
    @DisplayName("Deve inativar curso")
    void deveInativarCurso() {
        Long id = 1L;
        Curso curso = criarCursoMock(id, "Curso");

        when(cursoRepo.findById(id)).thenReturn(Optional.of(curso));

        boolean sucesso = cursoService.inativarCurso(id);

        assertTrue(sucesso);
        assertFalse(curso.isStatus());
        verify(cursoRepo).save(curso);
    }
}