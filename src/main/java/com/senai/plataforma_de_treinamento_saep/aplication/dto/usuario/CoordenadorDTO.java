package com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Coordenador;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;

public record CoordenadorDTO(
        Long id,
        String nome,
        String cpf,
        String senha,
        boolean status
) {
    public static CoordenadorDTO toDTO(Coordenador coordenador) {
        return new CoordenadorDTO(
                coordenador.getId(),
                coordenador.getNome(),
                coordenador.getCpf(),
                coordenador.getSenha(),
                coordenador.isStatus()
        );
    }

    public Coordenador fromDto(){
        Coordenador coordenador = new Coordenador();
        coordenador.setNome(nome);
        coordenador.setCpf(cpf);
        coordenador.setSenha(senha);
        coordenador.setTipoDeUsuario(TipoDeUsuario.COORDENADOR);
        coordenador.setStatus(true);

        return coordenador;
    }
}
