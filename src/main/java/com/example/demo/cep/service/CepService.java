package com.example.demo.cep.service;

import com.example.demo.cep.client.CepClientPort;
import com.example.demo.cep.dto.CepInfo;
import com.example.demo.cep.dto.CepResponse;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pela lógica de negócio relacionada à consulta de CEPs.
 * Atua como intermediário entre o controller e o cliente da API externa.
 */
@Service
public class CepService {

    private final CepClientPort cepClient;

    /**
     * Construtor com injeção de dependência do cliente de CEP.
     *
     * @param cepClient Cliente para comunicação com a API de CEP
     */
    public CepService(CepClientPort cepClient) {
        this.cepClient = cepClient;
    }

    /**
     * Obtém informações de um CEP através da API externa e converte para o formato interno.
     *
     * @param cep CEP a ser consultado
     * @return Informações do CEP no formato da aplicação
     */
    public CepInfo obterCep(String cep) {
        var resposta = cepClient.buscarCep(cep);
        return mapear(resposta);
    }

    /**
     * Converte a resposta da API externa para o formato de dados interno da aplicação.
     *
     * @param response Resposta da API externa
     * @return Objeto CepInfo com os dados mapeados
     */
    private CepInfo mapear(CepResponse response) {
        return new CepInfo(
                response.cep(),
                response.logradouro(),
                response.complemento(),
                response.bairro(),
                response.localidade(),
                response.uf()
        );
    }
}
