package ru.itis.javalabmessagequeqe.controllers;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import ru.itis.javalabmessagequeqe.models.Message;
import ru.itis.javalabmessagequeqe.models.Task;
import ru.itis.javalabmessagequeqe.services.QueueService;
import ru.itis.javalabmessagequeqe.services.TasksService;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@Controller
public class StompController {
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private Gson gson;
    @Autowired
    private QueueService queueService;
    @Autowired
    private TasksService tasksService;

    static final Map<String, Queue<Task>> tasks = new HashMap<>();
    private static final Map<String, Consumer> consumers = new HashMap<>();

    @SneakyThrows
    @MessageMapping("/{queue}")
    public void sendToConsumer(@DestinationVariable String queue, @Payload String payload) {
        Message m = gson.fromJson(payload, Message.class);
        save(m, queue);
    }

    @MessageMapping("/subscribe")
    public void subscribe(@Payload String queue) {
        if (queueService.contains(queue)) {
            if (!tasks.containsKey(queue)) {
                    tasks.put(queue, new ArrayDeque<>());
            }
            Consumer consumer = new Consumer(queue);
            consumer.start();
            consumers.put(queue, consumer);
        }

    }

    @MessageMapping("/accepted")
    public void accept(@Payload String payload) {
        Message m = gson.fromJson(payload, Message.class);
        System.out.println("Сообщение " + m.getMessageId() + " доставлено");
        tasksService.accept(m.getMessageId());
    }

    @MessageMapping("/completed")
    public void complete(@Payload String payload) {
        Message m = gson.fromJson(payload, Message.class);
        System.out.println("Сообщение " + m.getMessageId() + " выполнено");
        tasksService.complete(m.getMessageId());
        synchronized (consumers.get(m.getQueueName())) {
            consumers.get(m.getQueueName()).notify();
        }
    }

    private void save(Message m, String queue) {
        if (!tasks.containsKey(queue)) {
            if (queueService.contains(queue)) {
                tasks.put(queue, new ArrayDeque<>());
            } else {
                return;
            }
        }
        Task task = tasksService.save(Task.builder()
                .queue(queueService.findByName(queue).get())
                .body(gson.toJson(m.getBody()))
                .build());
        tasks.get(queue).add(task);
        synchronized (tasks.get(queue)) {
            tasks.get(queue).notify();
        }
    }

    private class Consumer extends Thread {
        private String queueName;

        private Consumer(String queueName) {
            this.queueName = queueName;
        }


        @SneakyThrows
        @Override
        public void run() {
            while (true) {
                synchronized (tasks.get(queueName)) {
                    if (tasks.get(queueName).isEmpty()) {
                        try {
                            tasks.get(queueName).wait();
                        } catch (InterruptedException e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }
                Task task = tasks.get(queueName).poll();
                send("/queue/" + queueName, gson.toJson(Message.builder()
                        .queueName(queueName)
                        .messageId(task.getMessageId())
                        .body(task.getBody())
                        .build()));
                synchronized (this) {
                    this.wait();
                }
            }
        }
    }

    private void send(String url, String message) {
        System.out.println(url);
        System.out.println(message);
        template.convertAndSend(url, message);
    }
}
