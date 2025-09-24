package com.senai.plataforma_de_treinamento_saep.aplication.dto.empresarial;

import com.senai.plataforma_de_treinamento_saep.domain.entity.empresarial.Empresa;

public record EmpresaDTO(
        Long id,
        String razaoSocial,
        String cnpj,
        String endereco,
        String telefone,
        String email,
        String representanteLegal,
        String logo,
        boolean status
) {

    public static EmpresaDTO toDTO(Empresa empresa) {
        return new EmpresaDTO(
                empresa.getId(),
                empresa.getRazaoSocial(),
                empresa.getCnpj(),
                empresa.getEndereco(),
                empresa.getTelefone(),
                empresa.getEmail(),
                empresa.getRepresentanteLegal(),
                empresa.getLogo(),
                empresa.isStatus()
                );
    }

    public Empresa fromDTO(){
        Empresa empresa = new Empresa();
        empresa.setRazaoSocial(razaoSocial);
        empresa.setCnpj(cnpj);
        empresa.setEndereco(endereco);
        empresa.setTelefone(telefone);
        empresa.setEmail(email);
        empresa.setRepresentanteLegal(representanteLegal);
        empresa.setLogo(logo);
        empresa.setStatus(true);

        return empresa;
    }


}
