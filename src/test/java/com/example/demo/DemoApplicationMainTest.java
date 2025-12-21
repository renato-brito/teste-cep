package com.example.demo;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

/**
 * Teste unitário para garantir cobertura total do método {@link DemoApplication#main(String[])}.
 * <p>
 * A ideia aqui é validar que o bootstrap do Spring é disparado, sem precisar subir o contexto
 * (mantendo o teste rápido e determinístico).
 */
class DemoApplicationMainTest {

    @Test
    void deveChamarSpringApplicationRun() {
        // Arrange
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(eq(DemoApplication.class), any(String[].class)))
                    .thenReturn(null);

            // Act
            DemoApplication.main(new String[]{});

            // Assert
            mocked.verify(() -> SpringApplication.run(eq(DemoApplication.class), any(String[].class)));
        }
    }
}

