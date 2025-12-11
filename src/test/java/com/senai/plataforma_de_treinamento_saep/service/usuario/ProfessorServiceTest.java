package com.senai.plataforma_de_treinamento_saep.service.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.ProfessorDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.RetornoCriacaoUsuarioDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioUpdateDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.ProfessorService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.ProfessorRepository;
import com.senai.plataforma_de_treinamento_saep.domain.service.usuario.UsuarioServiceDomain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceTest {

    @InjectMocks
    private ProfessorService professorService;

    @Mock
    private ProfessorRepository professorRepo;

    @Mock
    private UsuarioServiceDomain usuarioSD;

    @Mock
    private PasswordEncoder encoder;

    @Test
    @DisplayName("Deve cadastrar Professor com sucesso")
    void deveCadastrarProfessor() {
        ProfessorDTO dto = new ProfessorDTO(null, "Prof. Pardal", "123.456.789-00", "pardal@senai.br", null, true);

        Professor profSalvo = new Professor();
        profSalvo.setId(1L);
        profSalvo.setNome("Prof. Pardal");
        profSalvo.setTipoDeUsuario(TipoDeUsuario.PROFESSOR); // No Entity continua existindo

        when(usuarioSD.gerarSenhaPadrao(any())).thenReturn("senha123");
        when(encoder.encode("senha123")).thenReturn("hashSenha");
        when(professorRepo.save(any(Professor.class))).thenReturn(profSalvo);

        RetornoCriacaoUsuarioDTO<ProfessorDTO> retorno = professorService.cadastrarProfessor(dto);

        assertNotNull(retorno);
        assertEquals("Prof. Pardal", retorno.usuario().nome());
        verify(professorRepo).save(any(Professor.class));
    }

    @Test
    @DisplayName("Deve listar professores ativos")
    void deveListarProfessores() {
        Professor p1 = new Professor();
        p1.setNome("P1");

        when(professorRepo.findByStatusTrue()).thenReturn(List.of(p1));

        List<ProfessorDTO> lista = professorService.listarProfessoresAtivos();

        assertFalse(lista.isEmpty());
        assertEquals("P1", lista.get(0).nome());
    }

    @Test
    @DisplayName("Deve atualizar professor")
    void deveAtualizarProfessor() {
        Long id = 1L;
        UsuarioUpdateDTO update = new UsuarioUpdateDTO("Novo Nome", "novo@email.com", null);

        Professor antigo = new Professor();
        antigo.setId(id);

        Professor novo = new Professor();
        novo.setId(id);
        novo.setNome("Novo Nome");

        when(professorRepo.findById(id)).thenReturn(Optional.of(antigo));
        when(professorRepo.save(any(Professor.class))).thenReturn(novo);

        ProfessorDTO dto = professorService.atualizarProfessor(id, update);

        assertEquals("Novo Nome", dto.nome());
    }

    @Test
    @DisplayName("Deve inativar professor")
    void deveInativarProfessor() {
        Long id = 1L;
        Professor p1 = new Professor();
        p1.setId(id);
        p1.setStatus(true);

        when(professorRepo.findById(id)).thenReturn(Optional.of(p1));

        boolean sucesso = professorService.inativarProfessor(id);

        assertTrue(sucesso);
        assertFalse(p1.isStatus());
        verify(professorRepo).save(p1);
    }
}