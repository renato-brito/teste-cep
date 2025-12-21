package com.example.demo.cep.controller;

import com.example.demo.cep.dto.CepInfo;
import com.example.demo.cep.service.CepService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/ceps")
public class CepController {

    private final CepService cepService;
    private static final Pattern CEP_PATTERN = Pattern.compile("^[0-9]{5}-?[0-9]{3}$");

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<CepInfo> buscarCep(@PathVariable String cep) {
        validarCep(cep);
        return ResponseEntity.ok(cepService.obterCep(cep));
    }

    private void validarCep(String cep) {
        if (cep == null || !CEP_PATTERN.matcher(cep).matches()) {
            throw new IllegalArgumentException("CEP deve seguir o padrão 99999-999");
        }
    }
}
