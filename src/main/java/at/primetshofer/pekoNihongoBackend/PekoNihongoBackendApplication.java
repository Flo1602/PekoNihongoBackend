package at.primetshofer.pekoNihongoBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PekoNihongoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PekoNihongoBackendApplication.class, args);
	}

}
