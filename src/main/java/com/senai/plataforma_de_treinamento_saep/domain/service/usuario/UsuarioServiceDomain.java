package com.senai.plataforma_de_treinamento_saep.domain.service.usuario;

import com.senai.plataforma_de_treinamento_saep.domain.exception.ValidacaoDadosException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.Normalizer;

@Service
@RequiredArgsConstructor
public class UsuarioServiceDomain {
    private final UsuarioRepository usuarioRepository;

    public void consultarDadosObrigatorios(String nome,String cpf){
        if (nome.isBlank()){
            throw new RuntimeException("O nome é um dado obrigatório para o cadastro de um usuário.");
        }
        if (cpf.isBlank()){
            throw new RuntimeException("O CPF é um dado obrigatório para o cadastro de um usuário.");
        }
    }

    public void verificarCpfExistente(String cpf){
        if (usuarioRepository.existsByCpf(cpf)) {
            throw new ValidacaoDadosException("O CPF informado já está cadastrado no sistema.");
        }
    }

    public String gerarSenhaPadrao(String nome) {
        // 1. Pega o primeiro nome (Ex: "José Silva" -> "José")
        String primeiroNome = nome.split(" ")[0];

        // 2. Limpa o nome (Ex: "José" -> "Jose")
        String nomeLimpo = removerAcentos(primeiroNome);

        // 3. Gera os 5 números (Ex: "13579")
        String numeros = gerarNumerosAleatorios();

        // 4. Junta tudo (Ex: "Jose" + "13579" -> "Jose13579")
        return nomeLimpo + numeros;
    }

    private String removerAcentos(String texto) {
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("\\p{InCombiningDiacriticalMarks}", "");
        return texto;
    }

    private String gerarNumerosAleatorios() {
        SecureRandom random = new SecureRandom();
        StringBuilder numeros = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            // nextInt(9) gera de 0 a 8. Somamos 1 para ficar de 1 a 9.
            int digito = random.nextInt(9) + 1;
            numeros.append(digito);
        }

        return numeros.toString();
    }
}
