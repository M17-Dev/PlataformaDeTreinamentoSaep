package com.senai.plataforma_de_treinamento_saep.aplication.service.reciclagem;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.reciclagem.UsuarioTampinhaDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.reciclagem.UsuarioTampinha;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.reciclagem.UsuarioTampinhaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class UsuarioTampinhaService {

    public final UsuarioTampinhaRepository tampinhaRepo;

    // Geração de PIN e Senha (simples, para fins de exemplo)
    private String gerarPinUnico() {
        Random random = new Random();
        String pin;
        do {
            pin = String.format("%04d", random.nextInt(10000));
        } while (tampinhaRepo.existsByPin(pin));
        return pin;
    }

    private String gerarSenhaPadrao() {
        // Senha simples de 6 dígitos
        return "123456";
    }

    /**
     * Criação de UsuarioTampinha de forma manual (via REST/Controller).
     */
    @Transactional
    public UsuarioTampinhaDTO criarManual(UsuarioTampinhaDTO dto) {
        UsuarioTampinha usuarioTampinha = dto.toEntity();
        return UsuarioTampinhaDTO.toDTO(tampinhaRepo.save(usuarioTampinha));
    }

    /**
     * Criação de UsuarioTampinha automática (associada a um usuário principal).
     * @param nomeBase Nome base para referenciar o usuário principal (opcional).
     */
    @Transactional
    public UsuarioTampinha criarAutomatico(String nomeBase) {
        UsuarioTampinha usuarioTampinha = UsuarioTampinha.builder()
                .pin(gerarPinUnico())
                .senha(gerarSenhaPadrao())
                .saldoTampinhas(0)
                .build();

        return tampinhaRepo.save(usuarioTampinha);
    }

    /**
     * Adiciona tampinhas ao saldo do usuário, chamado pelo Listener MQTT.
     */
    @Transactional
    public UsuarioTampinhaDTO adicionarTampinhas(String pin, Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade de tampinhas deve ser positiva.");
        }

        UsuarioTampinha usuario = tampinhaRepo.findByPin(pin)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("UsuarioTampinha com PIN: " + pin + " não encontrado."));

        usuario.setSaldoTampinhas(usuario.getSaldoTampinhas() + quantidade);
        return UsuarioTampinhaDTO.toDTO(tampinhaRepo.save(usuario));
    }

}