package ru.itis.javalabmessagequeqe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JavaLabMessageQueueApplication {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Gson gson(){
        return new Gson();
    }

    public static void main(String[] args) {
        SpringApplication.run(JavaLabMessageQueueApplication.class, args);

    }

}
