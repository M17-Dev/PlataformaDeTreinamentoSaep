package com.senai.plataforma_de_treinamento_saep.aplication.service.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.LoginRequisicaoDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.LoginRespostaDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UsuarioRepository usuarioRepository;

    public LoginRespostaDTO autenticar(LoginRequisicaoDTO requisicao){
        Usuario usuario = usuarioRepository
                .findByCpf(requisicao.cpf())
                .filter(Usuario::isStatus)
                .orElseThrow(() -> new RuntimeException("CPF inválido."));

        if (!usuario.getSenha().equals(requisicao.senha())) {
            throw new RuntimeException("Senha inválida.");
        }

        return new LoginRespostaDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getTipoDeUsuario()
        );
    }
}