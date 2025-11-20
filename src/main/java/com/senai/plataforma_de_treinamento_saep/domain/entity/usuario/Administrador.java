package com.senai.plataforma_de_treinamento_saep.domain.entity.usuario;

import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Administrador extends Usuario{

    @Builder(builderMethodName = "administradorBuilder") // Renomeia o builder para evitar conflito
    public Administrador(Long id, String nome, String cpf, String senha, TipoDeUsuario tipoDeUsuario, boolean status) {
        super(id, nome, cpf, senha, tipoDeUsuario, status);
    }
}
