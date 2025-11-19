package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.reciclagem;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.reciclagem.UsuarioTampinhaDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.reciclagem.UsuarioTampinhaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reciclagem/usuario")
@RequiredArgsConstructor
public class UsuarioTampinhaController {

    private final UsuarioTampinhaService tampinhaService;

    @PostMapping("/manual")
    public ResponseEntity<UsuarioTampinhaDTO> cadastrarManual(@RequestBody UsuarioTampinhaDTO dto) {
        // Para cadastro manual, o PIN e a Senha devem vir no DTO
        return ResponseEntity
                .status(201)
                .body(tampinhaService.criarManual(dto));
    }

    // Opcional: Endpoint para verificar saldo
    @GetMapping("/saldo/{pin}")
    public ResponseEntity<UsuarioTampinhaDTO> buscarPorPin(@PathVariable String pin) {
        return tampinhaService.tampinhaRepo.findByPin(pin)
                .map(UsuarioTampinhaDTO::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}