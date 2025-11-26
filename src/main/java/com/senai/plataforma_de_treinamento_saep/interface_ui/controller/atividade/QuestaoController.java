package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.atividade;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.QuestaoDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.atividade.QuestaoService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questao")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR', 'PROFESSOR')")
@CrossOrigin("*")
public class QuestaoController {
    private final QuestaoService questaoService;
    private final UsuarioRepository usuarioRepo;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<QuestaoDTO> cadastrarQuestao(
            @RequestBody QuestaoDTO dto,
            Authentication authentication
    ) {
        String cpfLogado = authentication.getName();

        Usuario usuario = usuarioRepo.findByCpf(cpfLogado)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado com o token informado."));

        return ResponseEntity
                .status(201)
                .body(questaoService.cadastrarQuestao(dto, usuario.getId()));
    }

    @GetMapping
    public ResponseEntity<List<QuestaoDTO>> listarQuestoesAtivas() {
        return ResponseEntity.ok(questaoService.listarQuestoesAtivas());
    }

    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<QuestaoDTO>> listarQuestoesPeloIdDoCurso(@PathVariable Long idCurso){
        return ResponseEntity.ok(questaoService.listarQuestoesPeloCurso(idCurso));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestaoDTO> buscarPorId(@PathVariable Long id) {
        return questaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSOR')")
    public ResponseEntity<QuestaoDTO> atualizarQuestao(
            @PathVariable Long id,
            @RequestBody QuestaoDTO dto,
            Authentication authentication
    ) {
        String cpfLogado = authentication.getName();

        Usuario usuario = usuarioRepo.findByCpf(cpfLogado)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado com o token informado."));

        return ResponseEntity.ok(questaoService.atualizarQuestao(id, dto, usuario.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    public ResponseEntity<QuestaoDTO> inativarQuestao(@PathVariable Long id) {
        if (questaoService.inativarQuestao(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/reativar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    public ResponseEntity<QuestaoDTO> reativarQuestao(@PathVariable Long id) {
        if (questaoService.reativarQuestao(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
