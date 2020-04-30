package ru.itis.javalabmessagequeqe.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.itis.javalabmessagequeqe.models.Task;
import ru.itis.javalabmessagequeqe.repositories.TasksRepository;

@Component
public class TasksServiceImpl implements TasksService {
    @Autowired
    private TasksRepository tasksRepository;

    @Override
    public Task save(Task task) {
        return tasksRepository.save(task);
    }

    @Override
    public void accept(Long id) {
        tasksRepository.accepted(id);
    }

    @Override
    public void complete(Long id) {
        tasksRepository.completed(id);
    }
}
