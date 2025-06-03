package com.gildong.gildongE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GildongEApplication {

	public static void main(String[] args) {
		SpringApplication.run(GildongEApplication.class, args);
	}

}
