package com.senai.plataforma_de_treinamento_saep.domain.repository;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    List<Usuario> findByStatusTrue();
}
