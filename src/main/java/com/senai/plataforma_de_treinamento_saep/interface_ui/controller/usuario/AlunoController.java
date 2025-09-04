package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.AlunoDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.AlunoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aluno")
@RequiredArgsConstructor
public class AlunoController {
    private final AlunoService alunoService;

    @PostMapping
    public ResponseEntity<Void> cadastrarAluno(@RequestBody AlunoDTO dto) {
        alunoService.cadastrarAluno(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<AlunoDTO>> listarAlunosAtivos() {
        return ResponseEntity.ok(alunoService.listarAlunosAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoDTO> buscarPorId(@PathVariable Long id) {
        return alunoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoDTO> atualizarAluno(@PathVariable Long id, @RequestBody AlunoDTO dto) {
        if (alunoService.atualizarAluno(id, dto)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AlunoDTO> inativarAluno(@PathVariable Long id) {
        if (alunoService.inativarAluno(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/reativar/{id}")
    public ResponseEntity<AlunoDTO> reativarAluno(@PathVariable Long id) {
        if (alunoService.reativarAluno(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}