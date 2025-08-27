package com.senai.plataforma_de_treinamento_saep.aplication.service;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.AlunoDTO;
import com.senai.plataforma_de_treinamento_saep.domain.repository.AlunoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private AlunoRepository alunoRepo;

    @Transactional
    public void cadastrarAluno(AlunoDTO dto) {
        alunoRepo.save(dto.fromDto());
    }
}
