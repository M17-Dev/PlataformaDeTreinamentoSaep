package com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.reciclagem.UsuarioReciclagemDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Coordenador;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;

public record UsuarioDTO(
        Long id,
        String nome,
        String cpf,
        String email,
        String senha,
        TipoDeUsuario tipoDeUsuario,
        boolean status,
        UsuarioReciclagemDTO reciclagem
) {
    public static UsuarioDTO toDTO(Usuario usuario) {
        return toDTO(usuario, null);
    }

    public static UsuarioDTO toDTO(Usuario usuario, UsuarioReciclagemDTO reciclagem) {
        TipoDeUsuario tipoUsuario = switch (usuario) {
            case Aluno aluno -> TipoDeUsuario.ALUNO;
            case Coordenador coord -> TipoDeUsuario.COORDENADOR;
            case Professor prof -> TipoDeUsuario.PROFESSOR;
            default -> throw new IllegalArgumentException("Tipo de usuário invalido");
        };
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getEmail(),
                usuario.getSenha(),
                tipoUsuario,
                usuario.isStatus(),
                reciclagem
        );
    }

    public Usuario fromDTO() {
        Usuario usuario = switch (tipoDeUsuario) {
            case ALUNO -> new Aluno();
            case PROFESSOR -> new Professor();
            case COORDENADOR -> new Coordenador();
            default -> throw new RuntimeException("O tipo de usuário só pode ser [ALUNO, PROFESSOR, COORDENADOR}");
        };

        usuario.setNome(nome);
        usuario.setCpf(cpf);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setTipoDeUsuario(tipoDeUsuario);
        usuario.setStatus(true);
        return usuario;
    }
}