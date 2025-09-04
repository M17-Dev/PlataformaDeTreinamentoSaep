package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.AlunoDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.CoordenadorDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.CoordenadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coordenador")
@RequiredArgsConstructor
public class CoordenadorController {
    private final CoordenadorService coordService;

    @PostMapping
    public ResponseEntity<Void> cadastrarCoordenador(@RequestBody CoordenadorDTO dto) {
        coordService.cadastrarCoordenador(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CoordenadorDTO>> listarCoordenadoresAtivos() {
        return ResponseEntity.ok(coordService.listarCoordenadoresAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoordenadorDTO> buscarPorId(@PathVariable Long id) {
        return coordService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoordenadorDTO> atualizarCoordenador(@PathVariable Long id, @RequestBody CoordenadorDTO dto) {
        if (coordService.atualizarCoordenador(id, dto)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CoordenadorDTO> inativarCoordenador(@PathVariable Long id) {
        if (coordService.inativarCoordenador(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/reativar/{id}")
    public ResponseEntity<CoordenadorDTO> reativarCoordenador(@PathVariable Long id) {
        if (coordService.reativarCoordenador(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
