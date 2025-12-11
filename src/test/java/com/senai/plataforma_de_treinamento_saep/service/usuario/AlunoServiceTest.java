package com.senai.plataforma_de_treinamento_saep.service.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.AlunoDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.RetornoCriacaoUsuarioDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioUpdateDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.AlunoService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.Curso;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;
import com.senai.plataforma_de_treinamento_saep.domain.repository.atividade.ProvaRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.escolar.CursoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.AlunoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.service.usuario.UsuarioServiceDomain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @InjectMocks
    private AlunoService alunoService;

    @Mock
    private AlunoRepository alunoRepo;

    @Mock
    private CursoRepository cursoRepo;

    @Mock
    private ProvaRepository provaRepo;

    @Mock
    private UsuarioServiceDomain usuarioSD;

    @Mock
    private PasswordEncoder encoder;

    // Helper para criar o Curso Mockado
    private Curso criarCursoMock() {
        Curso c = new Curso();
        c.setId(1L);
        c.setNome("Dev Sistemas");
        return c;
    }

    // Helper para criar um Aluno completo e evitar NullPointerException no toDTO
    private Aluno criarAlunoMock(Long id, String nome) {
        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setNome(nome);
        aluno.setCpf("000.000.000-00");
        aluno.setEmail("aluno@email.com");
        aluno.setTipoDeUsuario(TipoDeUsuario.ALUNO);
        aluno.setStatus(true);

        // Configurando Curso (Obrigatório para o toDTO)
        Curso curso = new Curso();
        curso.setId(1L);
        curso.setNome("Desenvolvimento de Sistemas");
        aluno.setCurso(criarCursoMock());

        // Configurando Listas (Obrigatório para o toDTO)
        aluno.setProvas(new ArrayList<>());
        aluno.setProvasRealizadas(new ArrayList<>());

        return aluno;
    }

    @Test
    @DisplayName("Deve cadastrar Aluno com sucesso")
    void deveCadastrarAluno() {
        AlunoDTO dtoEntrada = new AlunoDTO(
                null, "Aluno Teste", "000.000.000-00", "aluno@senai.br", null,
                1L, Collections.emptyList(), Collections.emptyList(), true
        );

        Aluno alunoSalvo = criarAlunoMock(1L, "Aluno Teste");
        Curso cursoMock = criarCursoMock();

        when(cursoRepo.findById(1L)).thenReturn(Optional.of(cursoMock));
        when(provaRepo.findByCursoId(1L)).thenReturn(new ArrayList<>());

        when(usuarioSD.gerarSenhaPadrao(any())).thenReturn("senha123");
        when(encoder.encode("senha123")).thenReturn("hashSenha");
        when(alunoRepo.save(any(Aluno.class))).thenReturn(alunoSalvo);

        RetornoCriacaoUsuarioDTO<AlunoDTO> retorno = alunoService.cadastrarAluno(dtoEntrada);

        assertNotNull(retorno);
        assertEquals("Aluno Teste", retorno.usuario().nome());

        verify(cursoRepo).findById(1L);
        verify(provaRepo).findByCursoId(1L);
    }

    @Test
    @DisplayName("Deve listar alunos ativos")
    void deveListarAlunos() {
        Aluno a1 = criarAlunoMock(1L, "A1");

        when(alunoRepo.findByStatusTrue()).thenReturn(List.of(a1));

        List<AlunoDTO> lista = alunoService.listarAlunosAtivos();

        assertEquals(1, lista.size());
        assertEquals("A1", lista.getFirst().nome());
        assertEquals(1L, lista.getFirst().cursoId());
    }

    @Test
    @DisplayName("Deve atualizar aluno")
    void deveAtualizarAluno() {
        Long id = 1L;
        UsuarioUpdateDTO update = new UsuarioUpdateDTO("Aluno Editado", null, null);

        Aluno alunoAntigo = criarAlunoMock(id, "Aluno Antigo");
        Aluno alunoNovo = criarAlunoMock(id, "Aluno Editado"); // Simula objeto após update

        when(alunoRepo.findById(id)).thenReturn(Optional.of(alunoAntigo));
        when(alunoRepo.save(any(Aluno.class))).thenReturn(alunoNovo);

        AlunoDTO resultado = alunoService.atualizarAluno(id, update);

        assertEquals("Aluno Editado", resultado.nome());
    }

    @Test
    @DisplayName("Deve inativar aluno com sucesso")
    void deveInativarAluno() {
        Long id = 1L;
        Aluno alunoAtivo = criarAlunoMock(id, "Aluno Ativo");

        when(alunoRepo.findById(id)).thenReturn(Optional.of(alunoAtivo));

        boolean sucesso = alunoService.inativarAluno(id);

        assertTrue(sucesso);
        assertFalse(alunoAtivo.isStatus());

        verify(alunoRepo).save(alunoAtivo);
    }

    @Test
    @DisplayName("Deve falhar ao inativar aluno inexistente")
    void deveFalharInativacao() {
        Long id = 99L;
        when(alunoRepo.findById(id)).thenReturn(Optional.empty());

        boolean sucesso = alunoService.inativarAluno(id);

        assertFalse(sucesso);
        verify(alunoRepo, never()).save(any());
    }
}