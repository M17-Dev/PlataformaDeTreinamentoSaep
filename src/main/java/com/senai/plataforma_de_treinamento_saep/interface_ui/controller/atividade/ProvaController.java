package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.atividade;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.ProvaDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.atividade.ProvaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prova")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
public class ProvaController {
    private final ProvaService provaService;

    @PostMapping
    public ResponseEntity<ProvaDTO.ProvaResponseDTO> cadastrarProva(@RequestBody ProvaDTO.ProvaRequestDTO dto) {
        return ResponseEntity
                .status(201)
                .body(provaService.cadastrarProva(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'PROFESSOR')")
    public ResponseEntity<List<ProvaDTO.ProvaResponseDTO>> listarProvasAtivas() {
        return ResponseEntity.ok(provaService.listarProvasAtivas());
    }


    @GetMapping("/curso/{idCurso}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<List<ProvaDTO.ProvaResponseDTO>> listarProvasPeloIdDoCurso(@PathVariable Long idCurso){
        return ResponseEntity.ok(provaService.listarProvasPeloIdDoCurso(idCurso));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'PROFESSOR', 'ALUNO')")
    public ResponseEntity<ProvaDTO.ProvaResponseDTO> buscarPorId(@PathVariable Long id) {
        return provaService.buscarProvaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProvaDTO.ProvaResponseDTO> atualizarProva(@PathVariable Long id, @RequestBody ProvaDTO.AtualizarProvaDTO dto){
        return ResponseEntity.ok(provaService.atualizarProva(id, dto));
    }

    @PostMapping("/{idProva}/questoes/adicionar")
    public ResponseEntity<ProvaDTO.ProvaResponseDTO> adicionarQuestaoNaProva(@PathVariable Long idProva, @RequestBody ProvaDTO.AdicionarQuestaoProvaDTO dto){
        ProvaDTO.ProvaResponseDTO resposta = provaService.adicionarQuestaoNaProva(idProva, dto.idQuestaoASerAdicionada());
        return ResponseEntity.ok(resposta);
    }

    @PutMapping("/{idProva}/questoes/substituir")
    public ResponseEntity<ProvaDTO.ProvaResponseDTO> susbstituirQuestaoDaProva(@PathVariable Long idProva, @RequestBody ProvaDTO.SubstituirQuestaoProvaDTO dto){
        ProvaDTO.ProvaResponseDTO resposta = provaService.substituirQuestao(
                idProva,
                dto.idQuestaoASerAtualizada(),
                dto.idNovaQuestao()
        );

        return ResponseEntity.ok(resposta);
    }

    @DeleteMapping("/{idProva}/inativar")
    public ResponseEntity<ProvaDTO.ProvaResponseDTO> inativarProva(@PathVariable Long idProva){
        if (provaService.desativarProva(idProva)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{idProva}/reativar")
    public ResponseEntity<ProvaDTO.ProvaResponseDTO> reativarProva(@PathVariable Long idProva){
        if (provaService.reativarProva(idProva)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
