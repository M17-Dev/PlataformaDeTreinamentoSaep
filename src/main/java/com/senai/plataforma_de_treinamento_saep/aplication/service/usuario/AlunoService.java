package com.senai.plataforma_de_treinamento_saep.aplication.service.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.AlunoDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.AlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepo;

    public void cadastrarAluno(AlunoDTO dto) {
        alunoRepo.save(dto.fromDto());
    }

    public List<AlunoDTO> listarAlunosAtivos() {
        return alunoRepo.findByStatusTrue()
                .stream()
                .map(
                        AlunoDTO::toDTO
                )
                .collect(
                        Collectors.toList()
                );
    }

    public Optional<AlunoDTO> buscarPorId(Long id) {
        return alunoRepo.findById(id)
                .map(
                        AlunoDTO::toDTO
                );
    }

    public boolean atualizarAluno(Long id, AlunoDTO alunoDTO) {
        return alunoRepo.findById(id)
                .map(
                        aluno -> {
                            atualizarInfos(aluno, alunoDTO);
                            alunoRepo.save(aluno);
                            return true;
                        }
                )
                .orElse(false);
    }

    public boolean inativarAluno(Long id) {
        return alunoRepo.findById(id)
                .filter(
                        Aluno::isStatus
                )
                .map(
                        aluno -> {
                            aluno.setStatus(false);
                            alunoRepo.save(aluno);
                            return true;
                        }
                )
                .orElse(false);
    }

    public boolean reativarAluno(Long id) {
        return alunoRepo.findById(id)
                .filter(
                        aluno -> !aluno.isStatus()
                )
                .map(
                        aluno -> {
                            aluno.setStatus(true);
                            alunoRepo.save(aluno);
                            return true;
                        }
                )
                .orElse(false);
    }

    private void atualizarInfos(Aluno aluno, AlunoDTO dto) {
        if (dto.nome() != null && !dto.nome().isBlank()) {
            aluno.setNome(dto.nome());
        }
        if (dto.cpf() != null && !dto.cpf().isBlank()) {
            aluno.setCpf(dto.cpf());
        }
        if (dto.login() != null && !dto.login().isBlank()) {
            aluno.setLogin(dto.login());
        }
        if (dto.senha() != null && !dto.senha().isBlank()) {
            aluno.setSenha(dto.senha());
        }
    }
}
