package com.senai.plataforma_de_treinamento_saep.domain.repository.empresarial;

import com.senai.plataforma_de_treinamento_saep.domain.entity.empresarial.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    List<Empresa> findByStatusTrue();
}
