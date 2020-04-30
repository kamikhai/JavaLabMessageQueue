package ru.itis.javalabmessagequeqe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.itis.javalabmessagequeqe.models.Queue;

import java.util.Optional;

public interface QueueRepository extends JpaRepository<Queue, Long> {
    boolean existsByName(String queueName);
    Optional<Queue> findByName(String queueName);
}
