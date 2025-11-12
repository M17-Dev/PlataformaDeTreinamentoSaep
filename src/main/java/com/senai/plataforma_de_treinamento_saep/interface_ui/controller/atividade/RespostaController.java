package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.atividade;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade.RespostaDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.atividade.RespostaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resposta")
@RequiredArgsConstructor
public class RespostaController {

    private final RespostaService respostaService;

    @PostMapping
    public ResponseEntity<RespostaDTO> cadastrarResposta(@RequestBody RespostaDTO respostaDTO) {
        return ResponseEntity.status(201).body(respostaService.cadastrarResposta(respostaDTO));
    }

    @GetMapping
    public ResponseEntity<List<RespostaDTO>> listarRespostas() {
        return ResponseEntity.ok(respostaService.listarRespostas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespostaDTO> buscarPorId(@PathVariable Long id) {

        if(respostaService.buscarPorId(id).isPresent()) {
            RespostaDTO respostaDTO = respostaService.buscarPorId(id).get();
            return ResponseEntity.ok(respostaDTO);
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespostaDTO> atualizarResposta(@PathVariable Long id, @RequestBody RespostaDTO respostaDTO) {
        return respostaService.atualizarResposta(respostaDTO, id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarResposta(@PathVariable Long id) {
        return respostaService.deletarResposta(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
