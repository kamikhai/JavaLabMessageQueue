package ru.itis.javalabmessagequeqe.services;

import ru.itis.javalabmessagequeqe.models.Queue;

import java.util.Optional;

public interface QueueService {
    Long save(Queue queue);
    boolean contains(String queueName);
    Optional<Queue> findByName(String queueName);
}
