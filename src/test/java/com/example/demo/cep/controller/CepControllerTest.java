package com.example.demo.cep.controller;

import com.example.demo.cep.exception.GlobalExceptionHandler;
import com.example.demo.cep.dto.CepInfo;
import com.example.demo.cep.service.CepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CepControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CepService cepService;

    @BeforeEach
    void setUp() {
        var controller = new CepController(cepService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveRetornarCep() throws Exception {
        var info = new CepInfo("04842-010", "Rua Exemplo", "", "Bairro", "São Paulo", "SP");
        when(cepService.obterCep("04842-010")).thenReturn(info);

        mockMvc.perform(get("/ceps/04842-010"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").value("04842-010"))
                .andExpect(jsonPath("$.cidade").value("São Paulo"));
    }

    @Test
    void deveRetornarBadRequestQuandoCepInvalido() throws Exception {
        mockMvc.perform(get("/ceps/abc"))
                .andExpect(status().isBadRequest());
    }
}
