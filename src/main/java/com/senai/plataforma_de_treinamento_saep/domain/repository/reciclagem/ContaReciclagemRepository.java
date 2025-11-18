package com.senai.plataforma_de_treinamento_saep.domain.repository.reciclagem;

import com.senai.plataforma_de_treinamento_saep.domain.entity.reciclagem.ContaReciclagem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ContaReciclagemRepository extends JpaRepository<ContaReciclagem, Long> {
    Optional<ContaReciclagem> findByPin(String pin);
    boolean existsByPin(String pin);
}