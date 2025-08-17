package com.senai.plataforma_de_treinamento_saep.aplication.dto;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Coordenador;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;

public record CoordenadorDTO(
        Long id,
        String nome,
        String cpf,
        String matricula
) {
    public static CoordenadorDTO toDTO(Coordenador coordenador) {
        return new CoordenadorDTO(
                coordenador.getId(),
                coordenador.getNome(),
                coordenador.getCpf(),
                coordenador.getMatricula()
        );
    }

    public Coordenador fromDto(){
        Coordenador coordenador = new Coordenador();
        coordenador.setNome(nome);
        coordenador.setCpf(cpf);
        coordenador.setMatricula(matricula);
        coordenador.setTipoDeUsuario(TipoDeUsuario.COORDENADOR);
        coordenador.setStatus(true);

        return coordenador;
    }
}
