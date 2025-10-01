package com.senai.plataforma_de_treinamento_saep.aplication.service.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.ProfessorDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioUpdateDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.ProfessorRepository;
import com.senai.plataforma_de_treinamento_saep.domain.service.usuario.UsuarioServiceDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository profRepo;
    private final UsuarioServiceDomain usuarioSD;

    public Professor cadastrarProfessor(ProfessorDTO dto) {
        usuarioSD.verificarCpfExistente(dto.cpf());
        return profRepo.save(dto.fromDto());
    }

    public List<ProfessorDTO> listarProfessoresAtivos() {
        return profRepo.findByStatusTrue()
                .stream()
                .map(
                        ProfessorDTO::toDTO
                )
                .collect(
                        Collectors.toList()
                );
    }

    public Optional<ProfessorDTO> buscarPorId(Long id) {
        return profRepo.findById(id)
                .map(
                        ProfessorDTO::toDTO
                );
    }

    public ProfessorDTO atualizarProfessor(Long id, UsuarioUpdateDTO profDTO) {
        return profRepo.findById(id)
                .map(
                        professor -> {
                            atualizarInfos(professor, profDTO);
                            Professor professorAtualizado = profRepo.save(professor);
                            return ProfessorDTO.toDTO(professorAtualizado);
                        }
                )
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Professor dono do ID: " + id + " não encontrado"));
    }

    public boolean inativarProfessor(Long id) {
        return profRepo.findById(id)
                .filter(
                        Professor::isStatus
                )
                .map(
                        professor -> {
                            professor.setStatus(false);
                            profRepo.save(professor);
                            return true;
                        }
                )
                .orElse(false);
    }

    public boolean reativarProfessor(Long id) {
        return profRepo.findById(id)
                .filter(
                        professor -> !professor.isStatus()
                )
                .map(
                        professor -> {
                            professor.setStatus(true);
                            profRepo.save(professor);
                            return true;
                        }
                )
                .orElse(false);
    }

    private void atualizarInfos(Professor prof, UsuarioUpdateDTO dto) {
        if (dto.nome() != null && !dto.nome().isBlank()) {
            prof.setNome(dto.nome());
        }
        if (dto.senha() != null && !dto.senha().isBlank()) {
            prof.setSenha(dto.senha());
        }
    }
}