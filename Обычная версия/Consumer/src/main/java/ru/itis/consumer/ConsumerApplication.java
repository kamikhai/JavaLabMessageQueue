package ru.itis.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.itis.jlmq.Jlmq;
import ru.itis.jlmq.JlmqConnector;
import ru.itis.jlmq.JlmqConsumer;

import java.util.Scanner;

@SpringBootApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
        JlmqConnector connector = Jlmq
                .connector()
                .port("8080")
                .connect();
        System.out.println("Введите название очереди:");
        Scanner scanner = new Scanner(System.in);
        String queue = scanner.nextLine();
        JlmqConsumer consumer = connector
                .consumer()
                .subscribe(queue)
                .onReceive(message -> {
                    System.out.println("Сообщение: " + message.getBody());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                       throw new IllegalStateException(e);
                    }
                    System.out.println("Сообщение: " + message.getBody());
                })
                .create();


    }

}
