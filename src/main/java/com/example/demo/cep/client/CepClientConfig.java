package com.example.demo.cep.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class CepClientConfig {

    @Bean
    public RestClient restClient(@Value("${cep.client.base-url:http://localhost}") String baseUrl,
                                 @Value("${cep.client.timeout:PT5S}") Duration timeout) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout((int) timeout.toMillis());
        requestFactory.setConnectTimeout((int) timeout.toMillis());
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}

