package com.senai.plataforma_de_treinamento_saep.domain.entity.reciclagem;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContaReciclagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // O ESP32 pede PIN (4 dígitos) e Senha (6 dígitos) numéricos
    @Column(length = 4, nullable = false)
    private String pin;

    @Column(length = 6, nullable = false)
    private String senhaMaquina;

    private Integer saldoTampinhas = 0;

    // Relacionamento opcional: Um usuário do sistema pode ter uma conta de reciclagem
    // Mas uma conta de reciclagem pode existir sem um usuário do sistema (null)
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private Usuario usuarioSistema;
}