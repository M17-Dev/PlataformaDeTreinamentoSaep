package com.senai.plataforma_de_treinamento_saep.aplication.service.usuario;

import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.CoordenadorDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.RetornoCriacaoUsuarioDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.usuario.UsuarioUpdateDTO;
import com.senai.plataforma_de_treinamento_saep.aplication.service.reciclagem.UsuarioReciclagemService;
import com.senai.plataforma_de_treinamento_saep.domain.entity.usuario.Coordenador;
import com.senai.plataforma_de_treinamento_saep.domain.exception.EntidadeNaoEncontradaException;
import com.senai.plataforma_de_treinamento_saep.domain.repository.usuario.CoordenadorRepository;
import com.senai.plataforma_de_treinamento_saep.domain.service.usuario.UsuarioServiceDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.senai.plataforma_de_treinamento_saep.domain.entity.reciclagem.UsuarioReciclagem;
import com.senai.plataforma_de_treinamento_saep.aplication.dto.reciclagem.UsuarioReciclagemDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoordenadorService {

    private final CoordenadorRepository coordRepo;
    private final UsuarioServiceDomain usuarioSD;
    private final PasswordEncoder encoder;
    // Injeção do Service de Reciclagem
    private final UsuarioReciclagemService reciclagemService;

    @Transactional
    public RetornoCriacaoUsuarioDTO<CoordenadorDTO> cadastrarCoordenador(CoordenadorDTO dto) {
        usuarioSD.consultarDadosObrigatorios(dto.nome(), dto.cpf());
        usuarioSD.verificarCpfExistente(dto.cpf());

        Coordenador coordenador = dto.fromDto();
        String senhaPlana = (usuarioSD.gerarSenhaPadrao(dto.nome()));
        coordenador.setSenha(encoder.encode(senhaPlana));

        // 1. Salva a entidade
        Coordenador coordSalvoEntity = coordRepo.save(coordenador);

        // 2. Cria a conta e CAPTURA o retorno
        UsuarioReciclagem reciclagemSalva = reciclagemService.criarContaVinculada(coordSalvoEntity);

        // Converte a reciclagem para DTO
        UsuarioReciclagemDTO reciclagemDTO = UsuarioReciclagemDTO.toDTO(reciclagemSalva);

        // 3. Converte usando o método que ACEITA a reciclagem
        CoordenadorDTO coordSalvoDTO = CoordenadorDTO.toDTO(coordSalvoEntity, reciclagemDTO);

        return new RetornoCriacaoUsuarioDTO<>(coordSalvoDTO, senhaPlana);
    }
    public List<CoordenadorDTO> listarCoordenadoresAtivos() {
        return coordRepo.findByStatusTrue()
                .stream()
                .map(CoordenadorDTO::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<CoordenadorDTO> buscarPorId(Long id) {
        return coordRepo.findById(id).map(CoordenadorDTO::toDTO);
    }

    @Transactional
    public CoordenadorDTO atualizarCoordenador(Long id, UsuarioUpdateDTO dto) {
        return coordRepo.findById(id)
                .map(coordenador -> {
                    atualizarInfos(coordenador, dto);
                    Coordenador coordAtualizado = coordRepo.save(coordenador);
                    return CoordenadorDTO.toDTO(coordAtualizado);
                })
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Coordenador dono do ID: " + id + " não encontrado"));
    }

    @Transactional
    public boolean inativarCoordenador(Long id) {
        return coordRepo.findById(id)
                .filter(Coordenador::isStatus)
                .map(coordenador -> {
                    coordenador.setStatus(false);
                    coordRepo.save(coordenador);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public boolean reativarCoordenador(Long id) {
        return coordRepo.findById(id)
                .filter(coordenador -> !coordenador.isStatus())
                .map(coordenador -> {
                    coordenador.setStatus(true);
                    coordRepo.save(coordenador);
                    return true;
                })
                .orElse(false);
    }

    private void atualizarInfos(Coordenador coord, UsuarioUpdateDTO dto) {
        if (dto.nome() != null && !dto.nome().isBlank()) {
            coord.setNome(dto.nome());
        }
        if (dto.email() != null && !dto.email().isBlank()) {
            coord.setEmail(dto.email());
        }
        if (dto.senha() != null && !dto.senha().isBlank()) {
            coord.setSenha(dto.senha());
        }
    }
}