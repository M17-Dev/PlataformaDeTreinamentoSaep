package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.CoordenadorDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioUpdateDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.CoordenadorService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Coordenador;
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
    public ResponseEntity<CoordenadorDTO> cadastrarCoordenador(@RequestBody CoordenadorDTO dto) {
        return ResponseEntity
                .status(201)
                .body(CoordenadorDTO.toDTO(coordService.cadastrarCoordenador(dto)));
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
    public ResponseEntity<CoordenadorDTO> atualizarCoordenador(@PathVariable Long id, @RequestBody UsuarioUpdateDTO dto) {
        return ResponseEntity
                .ok(coordService.atualizarCoordenador(id, dto));
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
