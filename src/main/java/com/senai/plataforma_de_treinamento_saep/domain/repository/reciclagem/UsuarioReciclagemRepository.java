package com.senai.plataforma_de_treinamento_saep.domain.repository.reciclagem;

import com.senai.plataforma_de_treinamento_saep.domain.entity.reciclagem.UsuarioReciclagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioReciclagemRepository extends JpaRepository<UsuarioReciclagem, Long> {

    // Método para buscar pelo PIN, usado para identificação no Wokwi/IoT
    Optional<UsuarioReciclagem> findByPin(String pin);

    // Método para verificar se o PIN já existe durante a criação
    boolean existsByPin(String pin);
}