package com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Coordenador;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;

public record UsuarioUpdateDTO(
        String nome,
        String senha,
        TipoDeUsuario tipoDeUsuario
) {
    public Usuario fromDto(){
        Usuario usuario = switch (tipoDeUsuario) {
            case ALUNO -> new Aluno();
            case PROFESSOR -> new Professor();
            case COORDENADOR -> new Coordenador();
        };

        usuario.setNome(nome);
        usuario.setSenha(senha);
        usuario.setTipoDeUsuario(tipoDeUsuario);
        return usuario;
    }
}