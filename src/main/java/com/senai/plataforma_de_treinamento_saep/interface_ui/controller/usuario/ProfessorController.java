package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.ProfessorDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioUpdateDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.ProfessorService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professor")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
@CrossOrigin("*")
public class ProfessorController {
    private final ProfessorService profService;

    @PostMapping
    public ResponseEntity<ProfessorDTO> cadastrarProfessor(@RequestBody ProfessorDTO dto) {
        return ResponseEntity
                .status(201)
                .body(profService.cadastrarProfessor(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'PROFESSOR')")
    public ResponseEntity<List<ProfessorDTO>> listarProfessoresAtivos() {
        return ResponseEntity.ok(profService.listarProfessoresAtivos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'PROFESSOR')")
    public ResponseEntity<ProfessorDTO> buscarPorId(@PathVariable Long id) {
        return profService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfessorDTO> atualizarProfessor(@PathVariable Long id, @RequestBody UsuarioUpdateDTO dto) {
        return ResponseEntity.ok(profService.atualizarProfessor(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProfessorDTO> inativarProfessor(@PathVariable Long id) {
        if (profService.inativarProfessor(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/reativar/{id}")
    public ResponseEntity<ProfessorDTO> reativarProfessor(@PathVariable Long id) {
        if (profService.reativarProfessor(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
