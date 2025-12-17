package com.example.demo.cep.client;

import com.example.demo.cep.dto.CepResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CepClientTest {

    private CepClient cepClient;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        var builder = RestClient.builder().baseUrl("http://localhost");
        mockServer = MockRestServiceServer.bindTo(builder)
                .ignoreExpectOrder(true)
                .build();
        cepClient = new CepClient(builder.build());
    }

    @Test
    void deveBuscarCepComSucesso() {
        mockServer.expect(requestTo("http://localhost/04842010/json"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "cep": "04842-010",
                          "logradouro": "Rua Exemplo",
                          "complemento": "",
                          "bairro": "Bairro",
                          "localidade": "São Paulo",
                          "uf": "SP"
                        }
                        """, MediaType.APPLICATION_JSON));

        CepResponse resposta = cepClient.buscarCep("04842-010");

        assertThat(resposta.cep()).isEqualTo("04842-010");
        assertThat(resposta.localidade()).isEqualTo("São Paulo");
        mockServer.verify();
    }

    @Test
    void deveSanitizarCepRemovendoCaracteresInvalidos() {
        mockServer.expect(requestTo("http://localhost/04842010/json"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "cep": "04842-010",
                          "logradouro": "Outra Rua",
                          "complemento": "Casa",
                          "bairro": "Outro Bairro",
                          "localidade": "São Paulo",
                          "uf": "SP"
                        }
                        """, MediaType.APPLICATION_JSON));

        cepClient.buscarCep(" 04842-010-XX ");

        mockServer.verify();
    }

    @Test
    void deveLancarErroQuandoCepVazio() {
        assertThatThrownBy(() -> cepClient.buscarCep(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CEP não pode ser vazio");
    }

    @Test
    void deveLancarErroQuandoApiRetorna404() {
        mockServer.expect(requestTo("http://localhost/04842010/json"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> cepClient.buscarCep("04842-010"))
                .isInstanceOf(RestClientException.class);

        mockServer.verify();
    }

    @Test
    void deveLancarErroQuandoCepNulo() {
        assertThatThrownBy(() -> cepClient.buscarCep(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CEP não pode ser vazio");
    }

    @Test
    void deveLancarErroQuandoJsonMalformado() {
        mockServer.expect(requestTo("http://localhost/04842010/json"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{ invalid-json }", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> cepClient.buscarCep("04842-010"))
                .isInstanceOf(RuntimeException.class);

        mockServer.verify();
    }
}
