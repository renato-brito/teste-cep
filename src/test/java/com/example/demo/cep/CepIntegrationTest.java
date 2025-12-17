package com.example.demo.cep;

import com.example.demo.cep.client.CepClientPort;
import com.example.demo.cep.dto.CepResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CepIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    // holder para fornecer respostas dinâmicas no bean de teste
    private static final AtomicReference<CepResponse> cepResponseHolder = new AtomicReference<>();

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CepClientPort cepClientPort() {
            return cep -> cepResponseHolder.get();
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void deveRetornarCepViaEndpoint() throws Exception {
        var response = new CepResponse("04842-010", "Rua Exemplo", "", "Bairro", "São Paulo", "SP", null, null, null, null);
        cepResponseHolder.set(response);

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/ceps/04842-010"))
                .andExpect(status().isOk())
                .andReturn();

        var content = mvcResult.getResponse().getContentAsString();
        assertThat(content).contains("\"cidade\":\"São Paulo\"");
    }
}
