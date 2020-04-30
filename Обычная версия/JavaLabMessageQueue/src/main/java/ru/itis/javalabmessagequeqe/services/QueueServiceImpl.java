package ru.itis.javalabmessagequeqe.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.itis.javalabmessagequeqe.models.Queue;
import ru.itis.javalabmessagequeqe.repositories.QueueRepository;

import java.util.Optional;

@Component
public class QueueServiceImpl implements QueueService {
    @Autowired
    private QueueRepository queueRepository;

    @Override
    public Long save(Queue queue) {
        return queueRepository.save(queue).getId();
    }

    @Override
    public boolean contains(String queueName) {
        return queueRepository.existsByName(queueName);
    }

    @Override
    public Optional<Queue> findByName(String queueName) {
        return queueRepository.findByName(queueName);
    }
}
