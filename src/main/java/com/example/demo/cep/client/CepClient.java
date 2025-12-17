package com.example.demo.cep.client;

import com.example.demo.cep.dto.CepResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Implementação do cliente para consulta de CEP via API ViaCEP.
 * Configura automaticamente timeout e URL base através das properties da aplicação.
 */
@Component
@Primary
public class CepClient implements CepClientPort {

    private final RestClient restClient;

    /**
     * Construtor principal que configura o cliente HTTP com timeout e URL base.
     *
     * @param baseUrl URL base da API de CEP (configurada via application.properties)
     * @param timeout Timeout para conexão e leitura (configurado via application.properties)
     */
    @Autowired
    public CepClient(@Value("${cep.client.base-url}") String baseUrl,
                     @Value("${cep.client.timeout}") Duration timeout) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout((int) timeout.toMillis());
        requestFactory.setConnectTimeout((int) timeout.toMillis());
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    /**
     * Construtor alternativo que recebe um RestClient já configurado.
     * Usado principalmente em testes unitários com mock.
     *
     * @param restClient Cliente HTTP já configurado
     */
    public CepClient(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Busca informações de um CEP na API ViaCEP.
     *
     * @param cep CEP a ser consultado (pode conter formatação com hífen)
     * @return Dados do CEP retornados pela API
     * @throws IllegalArgumentException se o CEP for vazio ou nulo
     */
    @Override
    public CepResponse buscarCep(String cep) {
        var cepSanitizado = sanitizeCep(cep);
        return restClient.get()
                .uri("/{cep}/json", cepSanitizado)
                .retrieve()
                .body(CepResponse.class);
    }

    /**
     * Remove caracteres não numéricos do CEP e valida se não está vazio.
     *
     * @param cep CEP a ser sanitizado
     * @return CEP contendo apenas números
     * @throws IllegalArgumentException se o CEP for vazio ou nulo
     */
    private String sanitizeCep(String cep) {
        if (!StringUtils.hasText(cep)) {
            throw new IllegalArgumentException("CEP não pode ser vazio");
        }
        return cep.replaceAll("[^0-9]", "");
    }
}
