package com.senai.plataforma_de_treinamento_saep.domain.entity.reciclagem;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario_tampinha")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioTampinha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // PIN de 4 dígitos para identificação na máquina de reciclagem (Wokwi)
    @Column(length = 4, nullable = false, unique = true)
    private String pin;

    // Senha de 6 dígitos para resgate ou ações (Pode ser simples, mas é para IoT)
    @Column(length = 6, nullable = false)
    private String senha;

    // Saldo acumulado
    private Integer saldoTampinhas = 0;

    // Opcional: Relacionamento com o usuário principal (ex: Professor)
    // Se desejar ligar este perfil de tampinhas a um usuário do sistema, você pode usar:
    // @OneToOne
    // @JoinColumn(name = "usuario_id", unique = true)
    // private Usuario usuarioPrincipal;
}