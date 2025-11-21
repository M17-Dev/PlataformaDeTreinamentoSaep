package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.escolar;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.escolar.UnidadeCurricularDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.escolar.UnidadeCurricularService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidade-curricular")
@RequiredArgsConstructor
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
    public ResponseEntity<List<UnidadeCurricularDTO>> listarUnidadesCurricularesAtivas() {
        return ResponseEntity.ok(service.listarUnidadesCurricularesAtivas());
    }

    @GetMapping("/{id}")
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
