package com.senai.plataforma_de_treinamento_saep.aplication.dto.reciclagem;

import com.senai.plataforma_de_treinamento_saep.domain.entity.reciclagem.UsuarioReciclagem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO usado para cadastro manual e retorno de um UsuarioReciclagem.
 */
public record UsuarioReciclagemDTO(
        Long id,
        @NotBlank(message = "O nome é obrigatório")
        String nome,
        @NotNull @Size(min = 4, max = 4, message = "O PIN deve ter exatos 4 dígitos")
        String pin,
        @NotNull @Size(min = 6, max = 6, message = "A senha deve ter exatos 6 dígitos")
        String senha,
        Integer saldoTampinhas
) {
    public static UsuarioReciclagemDTO toDTO(UsuarioReciclagem entity) {
        return new UsuarioReciclagemDTO(
                entity.getId(),
                entity.getNome(),
                entity.getPin(),
                entity.getSenha(),
                entity.getSaldoTampinhas()
        );
    }

    public UsuarioReciclagem toEntity() {
        return UsuarioReciclagem.builder()
                .nome(nome)
                .pin(pin)
                .senha(senha)
                .saldoTampinhas(saldoTampinhas != null ? saldoTampinhas : 0)
                .build();
    }
}