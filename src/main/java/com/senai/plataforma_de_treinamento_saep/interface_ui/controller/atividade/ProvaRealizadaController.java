package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.atividade;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.RealizacaoProvaDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.atividade.ProvaRealizacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prova-realizada")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ALUNO')")
@CrossOrigin("*")
public class ProvaRealizadaController {

    private final ProvaRealizacaoService service;

    @PostMapping("/{idProva}/entregar")
    public ResponseEntity<RealizacaoProvaDTO.ResultadoProvaDTO> entregarProva(@PathVariable Long idProva, @RequestBody RealizacaoProvaDTO.EntregaProvaDTO dto) {
        var resultado = service.entregaProva(idProva, dto);
        return ResponseEntity.ok(resultado);
    }
}