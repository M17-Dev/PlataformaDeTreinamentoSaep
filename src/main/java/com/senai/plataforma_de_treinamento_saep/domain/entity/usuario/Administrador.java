package com.senai.plataforma_de_treinamento_saep.domain.entity.usuario;

import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Administrador extends Usuario{
}
