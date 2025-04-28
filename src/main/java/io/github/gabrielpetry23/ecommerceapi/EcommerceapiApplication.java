package io.github.gabrielpetry23.ecommerceapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EcommerceapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceapiApplication.class, args);
	}

}
