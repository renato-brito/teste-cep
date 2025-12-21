package com.example.demo.cep.dto;

public record CepInfo(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String cidade,
        String estado
) {
}
