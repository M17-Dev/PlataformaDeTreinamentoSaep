package com.senai.plataforma_de_treinamento_saep.aplication.dto.atividade;

import java.util.List;

public class RealizacaoProvaDTO {

    public record RespostaAlunoDTO(
            Long idQuestao,
            Long idRespostaEscolhida
    ) {}

    public record EntregaProvaDTO(
            Long idAluno,
            List<RespostaAlunoDTO> respostas
    ) {}

    public record ResultadoProvaDTO(
            Long idProvaRealizada,
            int totalQuestoes,
            int acertos,
            String mensagem
    ) {}
}