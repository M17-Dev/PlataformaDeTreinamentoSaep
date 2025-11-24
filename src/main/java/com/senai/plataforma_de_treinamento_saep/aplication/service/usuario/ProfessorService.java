package com.senai.plataforma_de_treinamento_saep.aplication.service.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.reciclagem.UsuarioTampinhaDTO; // Importar
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.ProfessorDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioUpdateDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.reciclagem.UsuarioTampinhaService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.reciclagem.UsuarioTampinha; // Importar
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
    private final UsuarioTampinhaService tampinhaService;

    public ProfessorDTO cadastrarProfessor(ProfessorDTO dto) {
        usuarioSD.consultarDadosObrigatorios(dto.nome(), dto.cpf());
        usuarioSD.verificarCpfExistente(dto.cpf());

        Professor professor = dto.fromDto();
        professor.setSenha(usuarioSD.gerarSenhaPadrao(dto.nome()));

        Professor professorSalvo = profRepo.save(professor);

        // 1. Cria a conta e captura o objeto criado (que tem o PIN e Senha)
        UsuarioTampinha reciclagem = tampinhaService.criarAutomatico(professorSalvo);

        // 2. Converte para DTO
        UsuarioTampinhaDTO reciclagemDTO = UsuarioTampinhaDTO.toDTO(reciclagem);

        // 3. Retorna o ProfessorDTO COM os dados da reciclagem dentro
        return ProfessorDTO.toDTO(professorSalvo, reciclagemDTO);
    }



    public List<ProfessorDTO> listarProfessoresAtivos() {
        return profRepo.findByStatusTrue()
                .stream()
                .map(ProfessorDTO::toDTO) // Usa o método que deixa reciclagem como null
                .collect(Collectors.toList());
    }

    // ... (código continua igual para update, inativar, etc)
    public Optional<ProfessorDTO> buscarPorId(Long id) {
        return profRepo.findById(id).map(ProfessorDTO::toDTO);
    }

    public ProfessorDTO atualizarProfessor(Long id, UsuarioUpdateDTO profDTO) {
        return profRepo.findById(id)
                .map(professor -> {
                    atualizarInfos(professor, profDTO);
                    Professor professorAtualizado = profRepo.save(professor);
                    return ProfessorDTO.toDTO(professorAtualizado);
                })
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Professor não encontrado"));
    }

    // ... inativar, reativar, atualizarInfos iguais ...
    public boolean inativarProfessor(Long id) {
        return profRepo.findById(id)
                .filter(Professor::isStatus)
                .map(professor -> {
                    professor.setStatus(false);
                    profRepo.save(professor);
                    return true;
                })
                .orElse(false);
    }

    public boolean reativarProfessor(Long id) {
        return profRepo.findById(id)
                .filter(professor -> !professor.isStatus())
                .map(professor -> {
                    professor.setStatus(true);
                    profRepo.save(professor);
                    return true;
                })
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