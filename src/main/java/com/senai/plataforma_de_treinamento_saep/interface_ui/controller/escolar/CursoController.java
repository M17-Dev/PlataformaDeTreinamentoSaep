package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.escolar;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.escolar.CursoDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.escolar.CursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curso")
@RequiredArgsConstructor
public class CursoController {
    private final CursoService cursoService;

    @PostMapping
    public ResponseEntity<CursoDTO> cadastrarCurso(@RequestBody CursoDTO dto) {
        return ResponseEntity
                .status(201)
                .body(CursoDTO.toDTO(cursoService.cadastrarCurso(dto)));
    }

    @GetMapping
    public ResponseEntity<List<CursoDTO>> listarCursoAtivas() {
        return ResponseEntity.ok(cursoService.listarCursosAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoDTO> buscarPorId(@PathVariable Long id) {
        return cursoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoDTO> atualizarCurso(@PathVariable Long id, @RequestBody CursoDTO dto) {
        return ResponseEntity.ok(cursoService.atualizarCurso(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CursoDTO> inativarCurso(@PathVariable Long id) {
        if (cursoService.inativarCurso(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/reativar/{id}")
    public ResponseEntity<CursoDTO> reativarCurso(@PathVariable Long id) {
        if (cursoService.reativarCurso(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}