package com.senai.plataforma_de_treinamento_saep.domain.repository;

import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Coordenador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoordenadorRepository extends JpaRepository<Coordenador, Long> {
    List<Coordenador> findByStatusTrue();
}
