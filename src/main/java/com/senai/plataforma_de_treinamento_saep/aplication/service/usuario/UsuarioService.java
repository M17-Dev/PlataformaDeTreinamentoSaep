package com.senai.plataforma_de_treinamento_saep.aplication.service.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.UsuarioRepository;
import com.senai.plataforma_de_treinamento_saep.domain.service.usuario.UsuarioServiceDomain;
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
    private final UsuarioServiceDomain usuarioSD;

    @Transactional
    public void cadastrarUsuario(UsuarioDTO dto) {
        usuarioSD.verificarCpfExistente(dto.cpf());
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
        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(dto.senha());
        }
    }
}
