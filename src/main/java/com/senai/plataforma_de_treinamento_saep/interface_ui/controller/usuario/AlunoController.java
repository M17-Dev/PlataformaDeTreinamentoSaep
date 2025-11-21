package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.AlunoDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioUpdateDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.AlunoService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aluno")
@RequiredArgsConstructor
public class AlunoController {
    private final AlunoService alunoService;

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @PostMapping
    public ResponseEntity<AlunoDTO> cadastrarAluno(@RequestBody AlunoDTO dto) {
        return ResponseEntity
                .status(201)
                .body(alunoService.cadastrarAluno(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'PROFESSOR')")
    @GetMapping
    public ResponseEntity<List<AlunoDTO>> listarAlunosAtivos() {
        return ResponseEntity.ok(alunoService.listarAlunosAtivos());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'PROFESSOR')")
    @GetMapping("/{id}")
    public ResponseEntity<AlunoDTO> buscarPorId(@PathVariable Long id) {
        return alunoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<AlunoDTO> atualizarAluno(@PathVariable Long id, @RequestBody UsuarioUpdateDTO dto) {
        return ResponseEntity.ok(alunoService.atualizarAluno(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<AlunoDTO> inativarAluno(@PathVariable Long id) {
        if (alunoService.inativarAluno(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    @PutMapping("/reativar/{id}")
    public ResponseEntity<AlunoDTO> reativarAluno(@PathVariable Long id) {
        if (alunoService.reativarAluno(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}