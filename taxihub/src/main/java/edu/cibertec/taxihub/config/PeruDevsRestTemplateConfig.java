package edu.cibertec.taxihub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PeruDevsRestTemplateConfig {

    @Value("${perudevs.url}")
    private String perudevsUrl;


    @Bean
    public RestTemplate peruDevsReniecRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(perudevsUrl)
                .build();
    }

}
