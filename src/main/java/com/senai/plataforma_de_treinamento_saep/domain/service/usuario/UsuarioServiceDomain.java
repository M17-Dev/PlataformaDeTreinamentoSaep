package com.senai.plataforma_de_treinamento_saep.domain.service.usuario;

import com.senai.plataforma_de_treinamento_saep.domain.exception.ValidacaoDadosException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioServiceDomain {
    private final UsuarioRepository usuarioRepository;

    public void verificarCpfExistente(String cpf){
        if (usuarioRepository.existsByCpf(cpf)) {
            throw new ValidacaoDadosException("O CPF informado já está cadastrado no sistema.");
        }
    }
}
