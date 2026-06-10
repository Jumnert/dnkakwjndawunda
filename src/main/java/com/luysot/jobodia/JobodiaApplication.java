package com.luysot.jobodia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JobodiaApplication {

	public static void main(String[] args) {

        SpringApplication.run(JobodiaApplication.class, args);

        System.out.println("Backend Started...");
	}

}
