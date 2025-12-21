package com.example.demo.cep.client;

import com.example.demo.cep.dto.CepResponse;

public interface CepClientPort {
    CepResponse buscarCep(String cep);
}

