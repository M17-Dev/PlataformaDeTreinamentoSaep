package com.senai.plataforma_de_treinamento_saep.service.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.RetornoCriacaoUsuarioDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioUpdateDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.UsuarioService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.exception.ValidacaoDadosException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.UsuarioRepository;
import com.senai.plataforma_de_treinamento_saep.domain.service.usuario.UsuarioServiceDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepo;

    @Mock
    private PasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        UsuarioServiceDomain usuarioSD = new UsuarioServiceDomain(usuarioRepo);
        usuarioService = new UsuarioService(usuarioRepo, usuarioSD, encoder);
    }

    @Test
    @DisplayName("Deve cadastrar usuário e gerar senha corretamente (Teste Integrado)")
    void deveCadastrarUsuarioCompleto() {
        UsuarioDTO dto = new UsuarioDTO(
                null, "Maria Bonita", "999.888.777-66", "maria@email.com", null, TipoDeUsuario.ALUNO, true
        );

        Aluno alunoSalvo = new Aluno();
        alunoSalvo.setId(1L);
        alunoSalvo.setNome("Maria Bonita");

        when(usuarioRepo.existsByCpf(dto.cpf())).thenReturn(false); // O Domain Real vai chamar isso!
        when(encoder.encode(anyString())).thenReturn("senhaCriptografada");
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(alunoSalvo);

        RetornoCriacaoUsuarioDTO<UsuarioDTO> resultado = usuarioService.cadastrarUsuario(dto);

        assertNotNull(resultado);

        System.out.println("Senha gerada pelo Domain Real: " + resultado.senhaProvisoria());
        assertTrue(resultado.senhaProvisoria().startsWith("Maria"));
        assertTrue(resultado.senhaProvisoria().matches("Maria\\d{5}")); // Regex: Maria + 5 digitos

        verify(usuarioRepo, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve falhar na validação real de CPF existente")
    void deveFalharCpfDuplicado() {
        UsuarioDTO dto = new UsuarioDTO(
                null, "Teste", "111.222.333-44", "email@teste.com", null, TipoDeUsuario.ALUNO, true
        );

        when(usuarioRepo.existsByCpf(dto.cpf())).thenReturn(true);

        assertThrows(ValidacaoDadosException.class, () -> usuarioService.cadastrarUsuario(dto));

        verify(usuarioRepo, never()).save(any());
    }

    @Test
    @DisplayName("Deve falhar se nome estiver em branco (Validação do Domain)")
    void deveFalharNomeEmBranco() {
        UsuarioDTO dto = new UsuarioDTO(
                null, "", "111.222.333-44", "email@teste.com", null, TipoDeUsuario.ALUNO, true
        );

        RuntimeException erro = assertThrows(RuntimeException.class, () -> usuarioService.cadastrarUsuario(dto));

        assertEquals("O nome é um dado obrigatório para o cadastro de um usuário.", erro.getMessage());
    }

    @Test
    @DisplayName("Deve listar apenas usuários ativos")
    void deveListarUsuariosAtivos() {
        Aluno alunoAtivo = new Aluno();
        alunoAtivo.setId(1L);
        alunoAtivo.setNome("Ativo");
        alunoAtivo.setStatus(true);
        alunoAtivo.setTipoDeUsuario(TipoDeUsuario.ALUNO);

        when(usuarioRepo.findByStatusTrue()).thenReturn(List.of(alunoAtivo));

        List<UsuarioDTO> lista = usuarioService.listarUsuariosAtivos();

        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
        assertEquals("Ativo", lista.get(0).nome());
    }

    @Test
    @DisplayName("Deve buscar usuário por ID existente")
    void deveBuscarPorIdComSucesso() {
        Long id = 1L;
        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setNome("Busca");
        aluno.setTipoDeUsuario(TipoDeUsuario.ALUNO);
        aluno.setStatus(true);

        when(usuarioRepo.findById(id)).thenReturn(Optional.of(aluno));

        Optional<UsuarioDTO> resultado = usuarioService.buscarPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals("Busca", resultado.get().nome());
    }

    @Test
    @DisplayName("Deve lançar erro ao buscar ID inexistente")
    void deveFalharBuscaPorIdInexistente() {
        Long id = 99L;
        when(usuarioRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class, () -> usuarioService.buscarPorId(id));
    }

    @Test
    @DisplayName("Deve atualizar dados do usuário com sucesso")
    void deveAtualizarUsuario() {
        Long id = 1L;
        UsuarioUpdateDTO updateDTO = new UsuarioUpdateDTO("Nome Novo", "novo@email.com", "novaSenha");

        Aluno alunoAntigo = new Aluno();
        alunoAntigo.setId(id);
        alunoAntigo.setNome("Nome Antigo");
        alunoAntigo.setEmail("antigo@email.com");
        alunoAntigo.setTipoDeUsuario(TipoDeUsuario.ALUNO);

        Aluno alunoAtualizado = new Aluno();
        alunoAtualizado.setId(id);
        alunoAtualizado.setNome("Nome Novo");
        alunoAtualizado.setEmail("novo@email.com");
        alunoAtualizado.setTipoDeUsuario(TipoDeUsuario.ALUNO);

        when(usuarioRepo.findById(id)).thenReturn(Optional.of(alunoAntigo));
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(alunoAtualizado);

        UsuarioDTO resultado = usuarioService.atualizarUsuario(id, updateDTO);

        assertEquals("Nome Novo", resultado.nome());
        assertEquals("novo@email.com", resultado.email());

        // Verifica se chamou o save
        verify(usuarioRepo).save(alunoAntigo);
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar usuário inexistente")
    void deveFalharAtualizacaoUsuarioInexistente() {
        Long id = 99L;
        UsuarioUpdateDTO updateDTO = new UsuarioUpdateDTO("Nome", "email", "senha");

        when(usuarioRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class, () -> usuarioService.atualizarUsuario(id, updateDTO));

        verify(usuarioRepo, never()).save(any());
    }

    @Test
    @DisplayName("Deve inativar usuário ativo")
    void deveInativarUsuario() {
        Long id = 1L;
        Aluno alunoAtivo = new Aluno();
        alunoAtivo.setId(id);
        alunoAtivo.setStatus(true);

        when(usuarioRepo.findById(id)).thenReturn(Optional.of(alunoAtivo));

        boolean sucesso = usuarioService.inativarUsuario(id);

        assertTrue(sucesso);
        assertFalse(alunoAtivo.isStatus());
        verify(usuarioRepo).save(alunoAtivo);
    }

    @Test
    @DisplayName("Deve falhar ao inativar usuário que já está inativo")
    void deveFalharInativacaoDuplicada() {
        Long id = 1L;
        Aluno alunoInativo = new Aluno();
        alunoInativo.setId(id);
        alunoInativo.setStatus(false);

        when(usuarioRepo.findById(id)).thenReturn(Optional.of(alunoInativo));

        boolean sucesso = usuarioService.inativarUsuario(id);

        assertFalse(sucesso);
        verify(usuarioRepo, never()).save(any());
    }
}