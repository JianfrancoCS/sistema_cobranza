package edu.cibertec.taxihub_rest;

import jakarta.persistence.EntityListeners;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EntityListeners(AuditingEntityListener.class)
public class TaxihubRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaxihubRestApplication.class, args);
	}

}
