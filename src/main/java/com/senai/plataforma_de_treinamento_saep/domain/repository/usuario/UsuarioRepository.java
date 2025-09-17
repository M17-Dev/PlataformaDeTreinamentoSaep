package com.senai.plataforma_de_treinamento_saep.domain.repository.usuario;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
    List<Usuario> findByStatusTrue();
    Optional<Usuario> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
}
