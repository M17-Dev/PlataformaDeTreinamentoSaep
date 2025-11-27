package com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.reciclagem.UsuarioReciclagemDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Coordenador;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;

public record CoordenadorDTO(
        Long id,
        String nome,
        String cpf,
        String email,
        String senha,
        boolean status,
        UsuarioReciclagemDTO reciclagem
) {
    public static CoordenadorDTO toDTO(Coordenador coordenador) {
        return new CoordenadorDTO(
                coordenador.getId(),
                coordenador.getNome(),
                coordenador.getCpf(),
                coordenador.getEmail(),
                coordenador.getSenha(),
                coordenador.isStatus(),
                null
        );
    }

    public static CoordenadorDTO toDTO(Coordenador coordenador, UsuarioReciclagemDTO reciclagem) {
        return new CoordenadorDTO(
                coordenador.getId(),
                coordenador.getNome(),
                coordenador.getCpf(),
                coordenador.getEmail(),
                coordenador.getSenha(),
                coordenador.isStatus(),
                reciclagem
        );
    }

    public Coordenador fromDto(){
        Coordenador coordenador = new Coordenador();
        coordenador.setNome(nome);
        coordenador.setCpf(cpf);
        coordenador.setEmail(email);
        coordenador.setSenha(senha);
        coordenador.setTipoDeUsuario(TipoDeUsuario.COORDENADOR);
        coordenador.setStatus(true);
        return coordenador;
    }
}