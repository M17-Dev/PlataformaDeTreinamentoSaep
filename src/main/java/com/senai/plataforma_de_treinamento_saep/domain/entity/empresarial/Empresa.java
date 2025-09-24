package com.senai.plataforma_de_treinamento_saep.domain.entity.empresarial;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected String razaoSocial;
    protected String cnpj;
    protected String endereco;
    protected String telefone;
    protected String email;
    protected String representanteLegal;
    protected String logo;

    protected boolean status;

}
