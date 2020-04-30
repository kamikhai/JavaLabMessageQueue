package ru.itis.javalabmessagequeqe.handlers;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.itis.javalabmessagequeqe.models.Message;
import ru.itis.javalabmessagequeqe.models.Task;
import ru.itis.javalabmessagequeqe.services.QueueService;
import ru.itis.javalabmessagequeqe.services.TasksService;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

@Component
@EnableWebSocket
public class QueueMessagesHandler extends TextWebSocketHandler {

    static final Map<String, Queue<Task>> tasks = new HashMap<>();
    private static final Map<String, Consumer> consumers = new HashMap<>();

    @Autowired
    private Gson gson;
    @Autowired
    private QueueService queueService;
    @Autowired
    private TasksService tasksService;

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String messageText = (String) message.getPayload();
        System.out.println(messageText);
        Message m = gson.fromJson(messageText, Message.class);

        switch (m.getCommand()) {
            case "send": {
                send(m, session);
                break;
            }
            case "subscribe": {
                if (queueService.contains(m.getQueueName())){
                    Consumer consumer = new Consumer(m.getQueueName(), session);
                    consumer.start();
                    consumers.put(m.getQueueName(), consumer);
                } else {
                    session.sendMessage(new TextMessage(gson.toJson(Message.builder()
                            .command("answer")
                            .body("Очередь отстуствует! Создайте ее с помощью POST запроса на /queue")
                            .build())));
                }
                break;
            }
            case "accepted": {
                tasksService.accept(m.getMessageId());
                break;
            }
            case "completed": {
                tasksService.complete(m.getMessageId());
                synchronized (consumers.get(m.getQueueName())) {
                    consumers.get(m.getQueueName()).notify();
                }
                break;
            }
        }

    }

    private void send(Message m, WebSocketSession session) throws Exception {
        if (!tasks.containsKey(m.getQueueName())) {
            if (queueService.contains(m.getQueueName())) {
                tasks.put(m.getQueueName(), new ArrayDeque<>());
            } else {
                session.sendMessage(new TextMessage(gson.toJson(Message.builder()
                        .command("answer")
                        .body("Очередь отстуствует! Создайте ее с помощью POST запроса на /queue")
                        .build())));
                return;
            }
        }
        Task task = tasksService.save(Task.builder()
                .queue(queueService.findByName(m.getQueueName()).get())
                .body(gson.toJson(m.getBody()))
                .build());
        tasks.get(m.getQueueName()).add(task);
        session.sendMessage(new TextMessage(gson.toJson(Message.builder()
                .command("answer")
                .body("Задача уcпешно добавлена в очередь на выполнение")
                .build())));
        synchronized (tasks.get(m.getQueueName())) {
            tasks.get(m.getQueueName()).notify();
        }
    }

    private class Consumer extends Thread {
        private String queueName;
        private WebSocketSession session;

        private Consumer(String queueName, WebSocketSession session) {
            this.queueName = queueName;
            this.session = session;
        }


        @SneakyThrows
        @Override
        public void run() {
            while (true) {
                if (tasks.containsKey(queueName)) {
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
                    try {
                        session.sendMessage(new TextMessage(gson.toJson(Message.builder()
                                .command("receive")
                                .queueName(queueName)
                                .messageId(task.getMessageId())
                                .body(task.getBody())
                                .build())));
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                    synchronized (this) {
                        this.wait();
                    }
                }
            }
        }
    }
}

