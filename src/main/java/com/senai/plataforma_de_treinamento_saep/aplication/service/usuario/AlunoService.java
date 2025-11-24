package com.senai.plataforma_de_treinamento_saep.aplication.service.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.AlunoDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioUpdateDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.reciclagem.UsuarioTampinhaService; // Importação Necessária
import com.senai.plataforma_de_treinamento_saep.domain.entity.escolar.Curso;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Aluno;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.escolar.CursoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.AlunoRepository;
import com.senai.plataforma_de_treinamento_saep.domain.service.usuario.UsuarioServiceDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepo;
    private final CursoRepository cursoRepo;
    private final UsuarioServiceDomain usuarioSD;
    private final UsuarioTampinhaService tampinhaService; // INJEÇÃO DO SERVIÇO DE RECICLAGEM

    public AlunoDTO cadastrarAluno(AlunoDTO dto) {
        usuarioSD.consultarDadosObrigatorios(dto.nome(), dto.cpf());
        if (dto.cursoId() == null){
            throw new RuntimeException("Um curso é obrigatório para criar um aluno.");
        }
        Aluno aluno = dto.fromDto();

        usuarioSD.verificarCpfExistente(dto.cpf());
        aluno.setSenha(usuarioSD.gerarSenhaPadrao(dto.nome()));

        associarRelacionamentos(aluno, dto);

        Aluno alunoSalvo = alunoRepo.save(aluno);

        // CRIAÇÃO AUTOMÁTICA: Cria o perfil de tampinhas após salvar o aluno
        tampinhaService.criarAutomatico(alunoSalvo);

        return AlunoDTO.toDTO(alunoSalvo);
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

    public AlunoDTO atualizarAluno(Long id, UsuarioUpdateDTO alunoDTO) {
        return alunoRepo.findById(id)
                .map(
                        aluno -> {
                            atualizarInfos(aluno, alunoDTO);
                            Aluno alunoAtualizado = alunoRepo.save(aluno);
                            return AlunoDTO.toDTO(alunoAtualizado);
                        }
                )
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluno dono do ID: " + id + " não encontrado"));
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

    private void atualizarInfos(Aluno aluno, UsuarioUpdateDTO dto) {
        if (dto.nome() != null && !dto.nome().isBlank()) {
            aluno.setNome(dto.nome());
        }
        if (dto.senha() != null && !dto.senha().isBlank()) {
            aluno.setSenha(dto.senha());
        }
    }

    private void associarRelacionamentos(Aluno aluno, AlunoDTO dto){
        Curso curso = cursoRepo.findById(dto.cursoId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso referente ao ID: " + dto.cursoId() + " não encontrado."));
        aluno.setCurso(curso);
    }
}