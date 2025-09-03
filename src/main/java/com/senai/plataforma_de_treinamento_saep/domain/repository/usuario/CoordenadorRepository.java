package com.senai.plataforma_de_treinamento_saep.domain.repository.usuario;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Coordenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoordenadorRepository extends JpaRepository<Coordenador, Long> {
    List<Coordenador> findByStatusTrue();
}
