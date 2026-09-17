package HorasLaborales.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HorasLaboralesAutomotrizApplication {

	public static void main(String[] args) {

		SpringApplication.run(HorasLaboralesAutomotrizApplication.class, args);

	}

}