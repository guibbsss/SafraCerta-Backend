package com.safracerta.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI safraCertaOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("SafraCerta API")
            .version("0.0.1")
            .description(
                "Plataforma de gestão rural. Endpoints adicionados em @RestController "
                    + "são descobertos automaticamente pelo SpringDoc."));
  }
}
