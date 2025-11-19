package com.senai.plataforma_de_treinamento_saep.aplication.dto.reciclagem;

import com.senai.plataforma_de_treinamento_saep.domain.entity.reciclagem.UsuarioTampinha;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO usado para cadastro manual de um UsuarioTampinha.
 */
public record UsuarioTampinhaDTO(
        Long id,
        @NotNull @Size(min = 4, max = 4)
        String pin,
        @NotNull @Size(min = 6, max = 6)
        String senha,
        Integer saldoTampinhas
) {
    public static UsuarioTampinhaDTO toDTO(UsuarioTampinha usuarioTampinha) {
        return new UsuarioTampinhaDTO(
                usuarioTampinha.getId(),
                usuarioTampinha.getPin(),
                usuarioTampinha.getSenha(),
                usuarioTampinha.getSaldoTampinhas()
        );
    }

    public UsuarioTampinha toEntity() {
        return UsuarioTampinha.builder()
                .pin(pin)
                .senha(senha)
                .saldoTampinhas(0)
                .build();
    }
}