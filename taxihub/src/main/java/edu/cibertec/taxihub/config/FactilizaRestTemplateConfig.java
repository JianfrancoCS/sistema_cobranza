package edu.cibertec.taxihub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FactilizaRestTemplateConfig {

    @Value("${factiliza.token}")
    private String factilizaToken;

    @Value("${factiliza.url}")
    private String factilizaReniecApiUrl;

    @Bean
    public RestTemplate reniecRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(factilizaReniecApiUrl)
                .defaultHeader("Authorization", "Bearer " + factilizaToken)
                .build();
    }

    @Bean
    public RestTemplate placaRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(factilizaReniecApiUrl)
                .defaultHeader("Authorization", "Bearer " + factilizaToken)
                .build();
    }

    @Bean
    public RestTemplate licenciaRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(factilizaReniecApiUrl)
                .defaultHeader("Authorization", "Bearer " + factilizaToken)
                .build();
    }

}
