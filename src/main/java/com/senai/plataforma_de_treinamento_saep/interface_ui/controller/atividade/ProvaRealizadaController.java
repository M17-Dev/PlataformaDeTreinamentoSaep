package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.atividade;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.RealizacaoProvaDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.atividade.ProvaRealizacaoService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prova-realizada")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ALUNO')")
@CrossOrigin("*")
public class ProvaRealizadaController {

    private final ProvaRealizacaoService service;
    private final AlunoRepository repository;

    @PostMapping("/{idProva}/entregar")
    public ResponseEntity<RealizacaoProvaDTO.ResultadoProvaDTO> entregarProva(
            @PathVariable Long idProva,
            @RequestBody RealizacaoProvaDTO.EntregaProvaDTO dto,
            Authentication authentication
    ) {
        String cpfLogado = authentication.getName();

        Aluno aluno = repository.findByCpf(cpfLogado)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluno não encontrado com o token informado."));

        var resultado = service.entregaProva(idProva, aluno.getId(), dto);
        return ResponseEntity.ok(resultado);
    }
}