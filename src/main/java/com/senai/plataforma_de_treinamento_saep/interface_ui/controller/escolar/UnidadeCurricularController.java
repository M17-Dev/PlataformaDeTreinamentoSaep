package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.escolar;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.escolar.UnidadeCurricularDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.escolar.UnidadeCurricularService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidade-curricular")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
@CrossOrigin("*")
public class UnidadeCurricularController {
    private final UnidadeCurricularService service;

    @PostMapping
    public ResponseEntity<UnidadeCurricularDTO> cadastrarUnidadeCurricular(@RequestBody UnidadeCurricularDTO dto) {
        return ResponseEntity
                .status(201)
                .body(service.cadastrarUnidadeCurricular(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'PROFESSOR')")
    public ResponseEntity<List<UnidadeCurricularDTO>> listarUnidadesCurricularesAtivas() {
        return ResponseEntity.ok(service.listarUnidadesCurricularesAtivas());
    }

    @GetMapping("/curso/{idCurso}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<List<UnidadeCurricularDTO>> listarUnidadesCurricularesAtivasDeUmCurso(@PathVariable Long id){
        return ResponseEntity.ok(service.listarUnidadesCurricularesDoCurso(id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<UnidadeCurricularDTO> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadeCurricularDTO> atualizarUnidadeCurricular(@PathVariable Long id, @RequestBody UnidadeCurricularDTO dto) {
        return ResponseEntity.ok(service.atualizarUnidadeCurricular(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UnidadeCurricularDTO> inativarUnidadeCurricular(@PathVariable Long id) {
        if (service.inativarUnidadeCurricular(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/reativar/{id}")
    public ResponseEntity<UnidadeCurricularDTO> reativarUnidadeCurricular(@PathVariable Long id) {
        if (service.reativarUnidadeCurricular(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
