package com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Coordenador;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;

public record CoordenadorDTO(
        Long id,
        String nome,
        String cpf,
        String login,
        String senha
) {
    public static CoordenadorDTO toDTO(Coordenador coordenador) {
        return new CoordenadorDTO(
                coordenador.getId(),
                coordenador.getNome(),
                coordenador.getCpf(),
                coordenador.getLogin(),
                coordenador.getSenha()
        );
    }

    public Coordenador fromDto(){
        Coordenador coordenador = new Coordenador();
        coordenador.setNome(nome);
        coordenador.setCpf(cpf);
        coordenador.setLogin(login);
        coordenador.setSenha(senha);
        coordenador.setTipoDeUsuario(TipoDeUsuario.COORDENADOR);
        coordenador.setStatus(true);

        return coordenador;
    }
}
