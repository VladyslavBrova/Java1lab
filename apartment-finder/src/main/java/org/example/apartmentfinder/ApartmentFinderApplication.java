package org.example.apartmentfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApartmentFinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApartmentFinderApplication.class, args);
    }
}
