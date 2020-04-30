package ru.itis.javalabmessagequeqe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.javalabmessagequeqe.models.Task;

import java.util.Optional;

public interface TasksRepository extends JpaRepository<Task, Long> {
    default void accepted(Long id){
        Optional<Task> taskCandidate = findById(id);
        if (taskCandidate.isPresent()){
            Task task = taskCandidate.get();
            task.setAccepted(true);
            save(task);
        }
    };

    default void completed(Long id){
        Optional<Task> taskCandidate = findById(id);
        if (taskCandidate.isPresent()){
            Task task = taskCandidate.get();
            task.setCompleted(true);
            save(task);
        }
    };
}
