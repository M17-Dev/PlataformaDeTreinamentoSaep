package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.LoginRequisicaoDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.LoginRespostaDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<LoginRespostaDTO> login(@RequestBody LoginRequisicaoDTO dto){
        try {
            LoginRespostaDTO resposta = loginService.autenticar(dto);
            return ResponseEntity.ok(resposta);
        }catch (RuntimeException exception){
            return ResponseEntity.status(401).build();
        }
    }
}
