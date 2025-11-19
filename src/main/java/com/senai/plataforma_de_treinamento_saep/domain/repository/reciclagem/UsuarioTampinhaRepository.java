package com.senai.plataforma_de_treinamento_saep.domain.repository.reciclagem;

import com.senai.plataforma_de_treinamento_saep.domain.entity.reciclagem.UsuarioTampinha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioTampinhaRepository extends JpaRepository<UsuarioTampinha, Long> {

    // Método para buscar pelo PIN, usado para identificação no Wokwi/IoT
    Optional<UsuarioTampinha> findByPin(String pin);

    // Método para verificar se o PIN já existe durante a criação
    boolean existsByPin(String pin);
}