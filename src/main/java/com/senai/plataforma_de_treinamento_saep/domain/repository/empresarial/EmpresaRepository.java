package com.senai.plataforma_de_treinamento_saep.domain.repository.empresarial;

import com.senai.plataforma_de_treinamento_saep.domain.entity.empresarial.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface
EmpresaRepository extends JpaRepository {
    List<Empresa> findByStatusTrue();
}
