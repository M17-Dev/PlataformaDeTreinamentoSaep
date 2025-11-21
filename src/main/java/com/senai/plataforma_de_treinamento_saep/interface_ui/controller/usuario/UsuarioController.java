package com.senai.plataforma_de_treinamento_saep.interface_ui.controller.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioUpdateDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.usuario.UsuarioService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UsuarioDTO> cadastrarAluno(@RequestBody UsuarioDTO dto) {
        return ResponseEntity
                .status(201)
                .body(usuarioService.cadastrarUsuario(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuariosAtivos() {
        return ResponseEntity.ok(usuarioService.listarUsuariosAtivos());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioUpdateDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizarUsuario(id, dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioDTO> inativarUsuario(@PathVariable Long id) {
        if (usuarioService.inativarUsuario(id)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/reativar/{id}")
    public ResponseEntity<UsuarioDTO> reativarCliente(@PathVariable Long id){
        if (usuarioService.reativarUsuario(id)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
