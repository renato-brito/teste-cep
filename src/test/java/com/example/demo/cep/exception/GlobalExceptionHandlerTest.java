package com.example.demo.cep.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testes unitários focados em cobrir todos os fluxos do {@link GlobalExceptionHandler}.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleIllegalArgument_deveRetornar400ComBodyEPath() {
        // Arrange
        MockHttpServletRequest servletRequest = new MockHttpServletRequest("GET", "/ceps/abc");
        WebRequest webRequest = new ServletWebRequest(servletRequest);

        // Act
        var response = handler.handleIllegalArgument(new IllegalArgumentException("CEP deve seguir o padrão 99999-999"), webRequest);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).contains("CEP deve seguir");
        assertThat(response.getBody().path()).isEqualTo("/ceps/abc");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void handleIllegalArgument_quandoNaoEhServletWebRequest_deveRetornarPathVazio() {
        // Arrange
        WebRequest webRequest = mock(WebRequest.class);

        // Act
        var response = handler.handleIllegalArgument(new IllegalArgumentException("erro"), webRequest);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().path()).isEmpty();
    }

    @Test
    void handleConstraintViolation_deveConsolidarMensagensEretornar400() {
        // Arrange
        ConstraintViolation<?> v1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> v2 = mock(ConstraintViolation.class);

        Path path1 = mock(Path.class);
        Path path2 = mock(Path.class);

        when(path1.toString()).thenReturn("cep");
        when(path2.toString()).thenReturn("uf");

        when(v1.getPropertyPath()).thenReturn(path1);
        when(v2.getPropertyPath()).thenReturn(path2);
        when(v1.getMessage()).thenReturn("inválido");
        when(v2.getMessage()).thenReturn("obrigatório");

        var ex = new ConstraintViolationException(Set.of(v1, v2));

        MockHttpServletRequest servletRequest = new MockHttpServletRequest("GET", "/ceps/04842-010");
        WebRequest webRequest = new ServletWebRequest(servletRequest);

        // Act
        var response = handler.handleConstraintViolation(ex, webRequest);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().path()).isEqualTo("/ceps/04842-010");
        assertThat(response.getBody().message())
                .contains("cep: inválido")
                .contains("uf: obrigatório");
    }

    @Test
    void handleConstraintViolation_quandoNaoEhServletWebRequest_deveRetornarPathVazio() {
        // Arrange
        ConstraintViolation<?> v1 = mock(ConstraintViolation.class);
        Path path1 = mock(Path.class);
        when(path1.toString()).thenReturn("campo");
        when(v1.getPropertyPath()).thenReturn(path1);
        when(v1.getMessage()).thenReturn("inválido");

        var ex = new ConstraintViolationException(Set.of(v1));
        WebRequest webRequest = mock(WebRequest.class);

        // Act
        var response = handler.handleConstraintViolation(ex, webRequest);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().path()).isEmpty();
        assertThat(response.getBody().message()).contains("campo: inválido");
    }

    @Test
    void handleNoResourceFound_deveRetornar404SemBody() {
        // Arrange
        var ex = new NoResourceFoundException(org.springframework.http.HttpMethod.GET, "/favicon.ico", "No static resource favicon.ico");

        // Act
        var response = handler.handleNoResourceFound(ex);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void handleGeneric_deveRetornar500ComMensagemPadrao() {
        // Arrange
        MockHttpServletRequest servletRequest = new MockHttpServletRequest("GET", "/ceps/04842-010");
        WebRequest webRequest = new ServletWebRequest(servletRequest);

        // Act
        var response = handler.handleGeneric(new RuntimeException("boom"), webRequest);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Erro interno");
        assertThat(response.getBody().path()).isEqualTo("/ceps/04842-010");
    }

    @Test
    void handleGeneric_quandoNaoEhServletWebRequest_deveRetornarPathVazio() {
        // Arrange
        WebRequest webRequest = mock(WebRequest.class);

        // Act
        var response = handler.handleGeneric(new RuntimeException("boom"), webRequest);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().path()).isEmpty();
    }
}
