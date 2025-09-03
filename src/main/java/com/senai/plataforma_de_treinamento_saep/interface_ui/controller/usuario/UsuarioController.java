package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.UsuarioDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<Void> cadastrarUsuario(@RequestBody UsuarioDTO dto){
        usuarioService.cadastrarUsuario(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuariosAtivos() {
        return ResponseEntity.ok(usuarioService.listarUsuariosAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDTO dto){
        if (usuarioService.atualizarUsuario(id, dto)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioDTO> inativarUsuario(@PathVariable Long id) {
        if (usuarioService.inativarUsuario(id)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/reativar/{id}")
    public ResponseEntity<UsuarioDTO> reativarCliente(@PathVariable Long id){
        if (usuarioService.reativarUsuario(id)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
