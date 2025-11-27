package com.senai.plataforma_de_treinamento_saep.aplication.service.reciclagem;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.reciclagem.UsuarioReciclagemDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.reciclagem.UsuarioReciclagem;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.reciclagem.UsuarioReciclagemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class UsuarioReciclagemService {

    public final UsuarioReciclagemRepository reciclagemRepo;
    private final Random random = new Random();

    // --- MÉTODOS AUXILIARES ---

    private String gerarPinUnico() {
        String pin;
        do {
            pin = String.format("%04d", random.nextInt(10000));
        } while (reciclagemRepo.existsByPin(pin));
        return pin;
    }

    private String gerarSenhaPadrao() {
        // Gera uma senha numérica aleatória de 6 dígitos para maior segurança inicial
        return String.format("%06d", random.nextInt(1000000));
    }

    // --- MÉTODOS PRINCIPAIS ---

    /**
     * Criação de UsuarioReciclagem de forma manual (via REST/Controller).
     * Geralmente usado para membros da comunidade externa (sem login no sistema escolar).
     */
    @Transactional
    public UsuarioReciclagemDTO criarManual(UsuarioReciclagemDTO dto) {
        // Verifica se o PIN informado manualmente já existe (caso o DTO venha com PIN)
        if (reciclagemRepo.existsByPin(dto.pin())) {
            throw new RuntimeException("Este PIN já está em uso. Escolha outro.");
        }

        UsuarioReciclagem usuarioReciclagem = dto.toEntity();
        // Garante saldo zero no início
        usuarioReciclagem.setSaldoTampinhas(0);

        return UsuarioReciclagemDTO.toDTO(reciclagemRepo.save(usuarioReciclagem));
    }

    /**
     * Criação Automática vinculada a um Usuario do sistema (Professor/Aluno/Coord).
     * O Nome é copiado do usuário principal.
     */
    @Transactional
    public void criarContaVinculada(Usuario usuarioPrincipal) {
        UsuarioReciclagem usuarioReciclagem = UsuarioReciclagem.builder()
                .nome(usuarioPrincipal.getNome()) // Copia o nome do Professor/Aluno
                .pin(gerarPinUnico())
                .senha(gerarSenhaPadrao())
                .saldoTampinhas(0)
                .usuarioSistema(usuarioPrincipal) // Faz o vínculo no banco
                .build();

        UsuarioReciclagem salvo = reciclagemRepo.save(usuarioReciclagem);

        System.out.println("--------------------------------------------------");
        System.out.println("CONTA RECICLAGEM CRIADA PARA: " + salvo.getNome());
        System.out.println("PIN: " + salvo.getPin() + " | SENHA: " + salvo.getSenha());
        System.out.println("--------------------------------------------------");
    }

    /**
     * Adiciona tampinhas ao saldo do usuário.
     * Método preparado para ser usado futuramente pelo MQTT.
     */
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

    /**
     * Valida credenciais (preparado para MQTT).
     */
    public boolean validarLogin(String pin, String senha) {
        return reciclagemRepo.findByPin(pin)
                .map(conta -> conta.getSenha().equals(senha))
                .orElse(false);
    }
}