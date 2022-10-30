package com.gmail.mbotyuk.parserhtml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ParserHtmlApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParserHtmlApplication.class, args);
    }

}
