package com.senai.plataforma_de_treinamento_saep.domain.service.reciclagem;

import com.senai.plataforma_de_treinamento_saep.domain.entity.reciclagem.ContaReciclagem;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import com.senai.plataforma_de_treinamento_saep.domain.repository.reciclagem.ContaReciclagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class ContaReciclagemService {

    private final ContaReciclagemRepository repository;
    private final SecureRandom random = new SecureRandom();

    // Cria conta vinculada a um Professor/Aluno/Coord
    public void criarContaVinculada(Usuario usuario) {
        ContaReciclagem conta = new ContaReciclagem();
        conta.setUsuarioSistema(usuario);
        conta.setSaldoTampinhas(0);
        conta.setPin(gerarPinUnico());
        conta.setSenhaMaquina(gerarSenhaNumerica());

        repository.save(conta);
        // Aqui você poderia enviar um email para o usuário com o PIN/SENHA da máquina
        System.out.println("CONTA RECICLAGEM CRIADA PARA: " + usuario.getNome());
        System.out.println("PIN: " + conta.getPin() + " | SENHA: " + conta.getSenhaMaquina());
    }

    // Métodos auxiliares de geração
    private String gerarPinUnico() {
        String pin;
        do {
            pin = String.format("%04d", random.nextInt(10000));
        } while (repository.existsByPin(pin));
        return pin;
    }

    private String gerarSenhaNumerica() {
        return String.format("%06d", random.nextInt(1000000));
    }

    // Métodos para uso do MQTT (Login e Contagem)
    public boolean validarLoginMaquina(String pin, String senha) {
        return repository.findByPin(pin)
                .map(conta -> conta.getSenhaMaquina().equals(senha))
                .orElse(false);
    }

    public boolean adicionarTampinhas(String pin, int quantidade) {
        return repository.findByPin(pin)
                .map(conta -> {
                    conta.setSaldoTampinhas(conta.getSaldoTampinhas() + quantidade);
                    repository.save(conta);
                    return true;
                })
                .orElse(false);
    }
}