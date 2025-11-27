package com.senai.plataforma_de_treinamento_saep.aplication.service.reciclagem;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.reciclagem.UsuarioReciclagemDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.reciclagem.UsuarioReciclagem;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.reciclagem.UsuarioReciclagemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UsuarioReciclagemService {

    private final UsuarioReciclagemRepository reciclagemRepo;
    private final Random random = new Random();

    private String gerarPinUnico() {
        String pin;
        do {
            pin = String.format("%04d", random.nextInt(10000));
        } while (reciclagemRepo.existsByPin(pin));
        return pin;
    }

    private String gerarSenhaPadrao() {
        return String.format("%06d", random.nextInt(1000000));
    }

    // ... (Mantenha o método criarManual igual) ...
    @Transactional
    public UsuarioReciclagemDTO criarManual(UsuarioReciclagemDTO dto) {
        if (reciclagemRepo.existsByPin(dto.pin())) {
            throw new RuntimeException("Este PIN já está em uso. Escolha outro.");
        }
        UsuarioReciclagem usuarioReciclagem = dto.toEntity();
        usuarioReciclagem.setSaldoTampinhas(0);
        return UsuarioReciclagemDTO.toDTO(reciclagemRepo.save(usuarioReciclagem));
    }

    /**
     * ALTERAÇÃO AQUI: Agora retorna UsuarioReciclagem em vez de void
     */
    @Transactional
    public UsuarioReciclagem criarContaVinculada(Usuario usuarioPrincipal) {
        UsuarioReciclagem usuarioReciclagem = UsuarioReciclagem.builder()
                .nome(usuarioPrincipal.getNome())
                .pin(gerarPinUnico())
                .senha(gerarSenhaPadrao())
                .saldoTampinhas(0)
                .usuarioSistema(usuarioPrincipal)
                .build();

        UsuarioReciclagem salvo = reciclagemRepo.save(usuarioReciclagem);

        // Logs opcionais
        System.out.println("--------------------------------------------------");
        System.out.println("CONTA RECICLAGEM CRIADA PARA: " + salvo.getNome());
        System.out.println("--------------------------------------------------");

        return salvo; // Retorna o objeto salvo
    }

    // ... (Mantenha o restante da classe igual) ...
    @Transactional
    public UsuarioReciclagemDTO adicionarTampinhas(String pin, Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade de tampinhas deve ser positiva.");
        }
        UsuarioReciclagem usuario = reciclagemRepo.findByPin(pin)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("UsuarioReciclagem com PIN: " + pin + " não encontrado."));

        usuario.setSaldoTampinhas(usuario.getSaldoTampinhas() + quantidade);
        return UsuarioReciclagemDTO.toDTO(reciclagemRepo.save(usuario));
    }

    public boolean validarLogin(String pin, String senha) {
        return reciclagemRepo.findByPin(pin)
                .map(conta -> conta.getSenha().equals(senha))
                .orElse(false);
    }

    public Optional<UsuarioReciclagem> buscarPorPin(String pin) {
        return reciclagemRepo.findByPin(pin);
    }
}