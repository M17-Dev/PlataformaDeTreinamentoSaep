package com.senai.plataforma_de_treinamento_saep.domain.entity.reciclagem;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario_reciclagem")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioReciclagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome para identificação (pode ser o mesmo do Usuario do sistema ou um nome avulso)
    @Column(nullable = false)
    private String nome;

    // PIN de 4 dígitos para identificação na máquina de reciclagem (Wokwi)
    @Column(length = 4, nullable = false, unique = true)
    private String pin;

    // Senha de 6 dígitos para resgate ou ações (Pode ser simples, mas é para IoT)
    @Column(length = 6, nullable = false)
    private String senha;

    // Saldo acumulado
    @Builder.Default
    private Integer saldoTampinhas = 0;

    // Relacionamento com o usuário principal (ex: Professor/Aluno)
    // Optional = true, pois podemos ter usuários de reciclagem avulsos (comunidade externa)
    @OneToOne(optional = true)
    @JoinColumn(name = "usuario_id", unique = true, nullable = true)
    private Usuario usuarioSistema;
}