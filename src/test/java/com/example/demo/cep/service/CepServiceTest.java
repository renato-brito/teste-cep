package com.example.demo.cep.service;

import com.example.demo.cep.client.CepClientPort;
import com.example.demo.cep.dto.CepInfo;
import com.example.demo.cep.dto.CepResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CepServiceTest {

    private CepClientPort cepClient;
    private CepService cepService;

    @BeforeEach
    void setUp() {
        cepClient = mock(CepClientPort.class);
        cepService = new CepService(cepClient);
    }

    @Test
    void deveRetornarInfoQuandoCepValido() {
        var response = new CepResponse("04842-010", "Rua Exemplo", "", "Bairro", "São Paulo", "SP", null, null, null, null);
        when(cepClient.buscarCep("04842-010")).thenReturn(response);

        CepInfo info = cepService.obterCep("04842-010");

        assertThat(info.cep()).isEqualTo("04842-010");
        assertThat(info.cidade()).isEqualTo("São Paulo");
    }
}
