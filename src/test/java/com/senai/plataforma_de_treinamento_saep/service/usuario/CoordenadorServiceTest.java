package com.senai.plataforma_de_treinamento_saep.service.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.CoordenadorDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.RetornoCriacaoUsuarioDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.CoordenadorService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Coordenador;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.CoordenadorRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoordenadorServiceTest {

    @InjectMocks
    private CoordenadorService coordenadorService;

    @Mock
    private CoordenadorRepository coordenadorRepo;

    @Mock
    private UsuarioServiceDomain usuarioSD;

    @Mock
    private PasswordEncoder encoder;

    @Test
    @DisplayName("Deve cadastrar Coordenador com sucesso")
    void deveCadastrarCoordenador() {
        CoordenadorDTO dto = new CoordenadorDTO(null, "Denise", "999.999.999-99", "denise@senai.br", null, true);

        Coordenador coordSalvo = new Coordenador();
        coordSalvo.setId(1L);
        coordSalvo.setNome("Denise");
        coordSalvo.setTipoDeUsuario(TipoDeUsuario.COORDENADOR);

        when(usuarioSD.gerarSenhaPadrao(any())).thenReturn("senha123");
        when(encoder.encode("senha123")).thenReturn("hashSenha");
        when(coordenadorRepo.save(any(Coordenador.class))).thenReturn(coordSalvo);

        RetornoCriacaoUsuarioDTO<CoordenadorDTO> retorno = coordenadorService.cadastrarCoordenador(dto);

        assertNotNull(retorno);
        verify(coordenadorRepo).save(any(Coordenador.class));
    }

    @Test
    @DisplayName("Deve listar coordenadores ativos")
    void deveListarCoordenadores() {
        Coordenador c1 = new Coordenador();
        c1.setNome("C1");

        when(coordenadorRepo.findByStatusTrue()).thenReturn(List.of(c1));

        List<CoordenadorDTO> lista = coordenadorService.listarCoordenadoresAtivos();

        assertFalse(lista.isEmpty());
        assertEquals("C1", lista.getFirst().nome());
    }

    @Test
    @DisplayName("Deve inativar coordenador (Delete Lógico)")
    void deveInativarCoordenador() {
        Long id = 1L;
        Coordenador c1 = new Coordenador();
        c1.setId(id);
        c1.setStatus(true);

        when(coordenadorRepo.findById(id)).thenReturn(Optional.of(c1));

        boolean sucesso = coordenadorService.inativarCoordenador(id);

        assertTrue(sucesso);
        assertFalse(c1.isStatus());
        verify(coordenadorRepo).save(c1);
    }
}