package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.atividade;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.ProvaDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.atividade.ProvaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prova")
@RequiredArgsConstructor
public class ProvaController {
    private final ProvaService provaService;

    @PostMapping
    public ResponseEntity<ProvaDTO.ProvaResponseDTO> cadastrarProva(@RequestBody ProvaDTO.ProvaRequestDTO dto) {
        return ResponseEntity
                .status(201)
                .body(provaService.cadastrarProva(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProvaDTO.ProvaResponseDTO>> listarProvasAtivas() {
        return ResponseEntity.ok(provaService.listarProvasAtivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProvaDTO.ProvaResponseDTO> buscarPorId(@PathVariable Long id) {
        return provaService.buscarProvaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
