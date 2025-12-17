package com.example.demo.cep.client;

import com.example.demo.cep.dto.CepResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

/**
 * Implementação do cliente para consulta de CEP via API ViaCEP.
 *
 * <p>Obs.: a configuração do {@link RestClient} (baseUrl, timeout, etc.) é feita em
 * {@code com.example.demo.cep.config.CepClientConfig}.</p>
 */
@Component
@Primary
public class CepClient implements CepClientPort {

    private final RestClient restClient;

    /**
     * Construtor padrão. O {@link RestClient} é injetado já configurado.
     *
     * @param restClient cliente HTTP configurado
     */
    public CepClient(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Busca informações de um CEP na API ViaCEP.
     *
     * @param cep CEP a ser consultado (pode conter formatação com hífen)
     * @return dados do CEP retornados pela API
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
