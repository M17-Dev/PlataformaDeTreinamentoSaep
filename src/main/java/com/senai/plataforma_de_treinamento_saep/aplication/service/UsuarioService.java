package com.senai.plataforma_de_treinamento_saep.aplication.service;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.UsuarioDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;
import com.senai.plataforma_de_treinamento_saep.domain.repository.AlunoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.CoordenadorRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.ProfessorRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final AlunoRepository alunoRepo;
    private final ProfessorRepository profRepo;
    private final CoordenadorRepository coordRepo;

    @Transactional
    public void cadastrarUsuario(UsuarioDTO dto) {
        usuarioRepo.save(dto.fromDTO());
    }

    public List<UsuarioDTO> listarUsuariosAtivos() {
        return usuarioRepo.findByStatusTrue()
                .stream()
                .map(
                        UsuarioDTO::toDTO
                )
                .collect(
                        Collectors.toList()
                );
    }

    public Optional<UsuarioDTO> buscarPorId(Long id) {
        return usuarioRepo.findById(id)
                .map(
                        UsuarioDTO::toDTO
                );
    }

    public boolean atualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        return usuarioRepo.findById(id)
                .map(
                        usuario -> {
                            atualizarInfos(usuario, usuarioDTO);
                            usuarioRepo.save(usuario);
                            return true;
                        }
                )
                .orElse(false);
    }

    public boolean inativarUsuario(Long id) {
        return usuarioRepo.findById(id)
                .filter(
                        Usuario::isStatus
                )
                .map(
                        usuario -> {
                            usuario.setStatus(false);
                            usuarioRepo.save(usuario);
                            return true;
                        }
                )
                .orElse(false);
    }

    public boolean reativarUsuario(Long id) {
        return usuarioRepo.findById(id)
                .filter(
                        usuario -> !usuario.isStatus()
                )
                .map(
                        usuario -> {
                            usuario.setStatus(true);
                            usuarioRepo.save(usuario);
                            return true;
                        }
                )
                .orElse(false);
    }

    private void atualizarInfos(Usuario usuario, UsuarioDTO dto) {
        if (dto.nome() != null && !dto.nome().isBlank()) {
            usuario.setNome(dto.nome());
        }
        if (dto.cpf() != null && !dto.cpf().isBlank()) {
            usuario.setCpf(dto.cpf());
        }
        if (dto.login() != null && !dto.login().isBlank()) {
            usuario.setLogin(dto.login());
        }
        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(dto.senha());
        }
    }
}
