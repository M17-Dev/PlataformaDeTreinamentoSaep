package com.senai.plataforma_de_treinamento_saep.aplication.service.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.ProfessorDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Professor;
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

    public void cadastrarProfessor(ProfessorDTO dto) {
        profRepo.save(dto.fromDto());
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

    public boolean atualizarProfessor(Long id, ProfessorDTO profDTO) {
        return profRepo.findById(id)
                .map(
                        professor -> {
                            atualizarInfos(professor, profDTO);
                            profRepo.save(professor);
                            return true;
                        }
                )
                .orElse(false);
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

    private void atualizarInfos(Professor prof, ProfessorDTO dto) {
        if (dto.nome() != null && !dto.nome().isBlank()) {
            prof.setNome(dto.nome());
        }
        if (dto.senha() != null && !dto.senha().isBlank()) {
            prof.setSenha(dto.senha());
        }
    }
}