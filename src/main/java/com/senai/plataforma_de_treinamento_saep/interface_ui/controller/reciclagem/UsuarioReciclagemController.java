package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.reciclagem;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.reciclagem.UsuarioReciclagemDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.reciclagem.UsuarioReciclagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reciclagem/usuario")
@RequiredArgsConstructor
public class UsuarioReciclagemController {

    private final UsuarioReciclagemService reciclagemService;

    @PostMapping("/manual")
    public ResponseEntity<UsuarioReciclagemDTO> cadastrarManual(@RequestBody UsuarioReciclagemDTO dto) {
        return ResponseEntity
                .status(201)
                .body(reciclagemService.criarManual(dto));
    }

    @GetMapping("/saldo/{pin}")
    public ResponseEntity<UsuarioReciclagemDTO> buscarPorPin(@PathVariable String pin) {
        return reciclagemService.reciclagemRepo.findByPin(pin) // Nota: Se o campo 'tampinhaRepo' no service for privado, crie um método 'buscarPorPin' no Service.
                .map(UsuarioReciclagemDTO::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}