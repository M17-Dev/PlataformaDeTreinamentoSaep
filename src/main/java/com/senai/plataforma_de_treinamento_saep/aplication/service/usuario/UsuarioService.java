package com.senai.plataforma_de_treinamento_saep.aplication.service.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.RetornoCriacaoUsuarioDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioUpdateDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.UsuarioRepository;
import com.senai.plataforma_de_treinamento_saep.domain.service.usuario.UsuarioServiceDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final UsuarioServiceDomain usuarioSD;
    private final PasswordEncoder encoder;

    @Transactional
    public RetornoCriacaoUsuarioDTO<UsuarioDTO> cadastrarUsuario(UsuarioDTO dto) {
        usuarioSD.consultarDadosObrigatorios(dto.nome(), dto.cpf());
        usuarioSD.verificarCpfExistente(dto.cpf());

        Usuario usuario = dto.fromDTO();
        String senhaDescriptografada = (usuarioSD.gerarSenhaPadrao(dto.nome()));
        usuario.setSenha(encoder.encode(senhaDescriptografada));

        UsuarioDTO usuarioSalvo = UsuarioDTO.toDTO(usuarioRepo.save(usuario));

        return new RetornoCriacaoUsuarioDTO<>(usuarioSalvo, senhaDescriptografada);
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
        return Optional.ofNullable(usuarioRepo.findById(id)
                .map(
                        UsuarioDTO::toDTO
                )
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário de ID: " + id + " não foi encontrado.")));
    }

    @Transactional
    public UsuarioDTO atualizarUsuario(Long id, UsuarioUpdateDTO usuarioDto) {
        return usuarioRepo.findById(id)
                .map(
                        usuario -> {
                            atualizarInfos(usuario, usuarioDto);
                            Usuario usuarioAtualizado =  usuarioRepo.save(usuario);
                            return UsuarioDTO.toDTO(usuarioAtualizado);
                        }
                )
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario dono do ID: " + id + " não encontrado"));
    }

    @Transactional
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

    @Transactional
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

    private void atualizarInfos(Usuario usuario, UsuarioUpdateDTO dto) {
        if (dto.nome() != null && !dto.nome().isBlank()) {
            usuario.setNome(dto.nome());
        }
        if (dto.email() != null && !dto.email().isBlank()) {
            usuario.setEmail(dto.email());
        }
        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(dto.senha());
        }
    }
}
