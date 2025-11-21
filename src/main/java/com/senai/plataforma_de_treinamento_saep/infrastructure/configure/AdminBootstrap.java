package com.senai.plataforma_de_treinamento_saep.infrastructure.configure;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Administrador;
import com.senai.plataforma_de_treinamento_saep.domain.enums.TipoDeUsuario;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${sistema.admin.cpf}")
    private String adminCpf;

    @Value("${sistema.admin.senha}")
    private String adminSenha;


    @Override
    public void run(String... args) {
        usuarioRepository.findByCpf(adminCpf).ifPresentOrElse(
                usuario -> {
                    if (!usuario.isStatus()) {
                        usuario.setStatus(true);
                        usuarioRepository.save(usuario);
                    }
                },
                () -> {
                    Administrador admin = Administrador.builder()
                            .nome("Administrador Provisório")
                            .cpf(adminCpf)
                            .senha(passwordEncoder.encode(adminSenha))
                            .tipoDeUsuario(TipoDeUsuario.ADMIN)
                            .status(true)
                            .build();
                    usuarioRepository.save(admin);
                    System.out.println("Usuário admin provisório criado: " + adminCpf);
                }
        );
    }
}