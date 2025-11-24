package com.senai.plataforma_de_treinamento_saep.aplication.service.escolar;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.escolar.CursoDTO;
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.Curso;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.escolar.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CursoService {
    private final CursoRepository cursoRepo;

    @Transactional
    public CursoDTO cadastrarCurso(CursoDTO dto) {
        return CursoDTO.toDTO(cursoRepo.save(dto.fromDTO()));
    }

    public List<CursoDTO> listarCursosAtivos() {
        return cursoRepo.findByStatusTrue()
                .stream()
                .map(
                        CursoDTO::toDTO
                )
                .collect(
                        Collectors.toList()
                );
    }

    public Optional<CursoDTO> buscarPorId(Long id) {
        return cursoRepo.findById(id)
                .map(
                        CursoDTO::toDTO
                );
    }

    @Transactional
    public CursoDTO atualizarCurso(Long id, CursoDTO dto) {
        return cursoRepo.findById(id)
                .map(
                        curso -> {
                            atualizarInfos(curso, dto);
                            Curso cursoAtualizado = cursoRepo.save(curso);
                            return CursoDTO.toDTO(cursoAtualizado);
                        }
                )
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso dono do ID: " + id + " não encontrado"));
    }

    @Transactional
    public boolean inativarCurso(Long id) {
        return cursoRepo.findById(id)
                .filter(
                        Curso::isStatus
                )
                .map(
                        curso -> {
                            curso.setStatus(false);
                            cursoRepo.save(curso);
                            return true;
                        }
                )
                .orElse(false);
    }

    @Transactional
    public boolean reativarCurso(Long id) {
        return cursoRepo.findById(id)
                .filter(
                        curso -> !curso.isStatus()
                )
                .map(
                        curso -> {
                            curso.setStatus(true);
                            cursoRepo.save(curso);
                            return true;
                        }
                )
                .orElse(false);
    }

    private void atualizarInfos(Curso curso, CursoDTO dto) {
        if (dto.nome() != null && !dto.nome().isBlank()) {
            curso.setNome(dto.nome());
        }
    }
}
