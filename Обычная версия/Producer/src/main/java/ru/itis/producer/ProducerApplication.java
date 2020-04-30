package ru.itis.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.itis.jlmq.Jlmq;
import ru.itis.jlmq.JlmqConnector;
import ru.itis.jlmq.JlmqProducer;

import java.util.Scanner;

@SpringBootApplication
public class ProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
        JlmqConnector connector = Jlmq
                .connector()
                .port("8080")
                .connect();
        JlmqProducer producer = connector
                .producer()
                .toQueue("documents_for_generate")
                .create();

        producer.send(Email.builder().email("123@mail.ru").userData("Kamilla").build());
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.println("Введите сообщение:");
            String s = scanner.nextLine();
            producer.send(s);
        }
    }

}
