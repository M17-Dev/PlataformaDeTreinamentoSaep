package com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario;

import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;

public record LoginRespostaDTO(
    Long id,
    String nome,
    TipoDeUsuario tipoDeUsuario
){
}
