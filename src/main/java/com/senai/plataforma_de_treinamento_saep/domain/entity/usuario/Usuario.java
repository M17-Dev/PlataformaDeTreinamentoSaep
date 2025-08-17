package com.senai.plataforma_de_treinamento_saep.domain.entity.usuario;

import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected String nome;
    protected String cpf;
    protected String matricula;

    @Enumerated(EnumType.STRING)
    protected TipoDeUsuario tipoDeUsuario;
    protected boolean status;
}
