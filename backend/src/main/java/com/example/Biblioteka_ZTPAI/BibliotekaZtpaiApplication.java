package com.example.Biblioteka_ZTPAI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BibliotekaZtpaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BibliotekaZtpaiApplication.class, args);
	}

}
