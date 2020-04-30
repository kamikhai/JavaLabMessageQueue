package ru.itis.javalabmessagequeqe.services;

import ru.itis.javalabmessagequeqe.models.Task;

public interface TasksService {
    Task save(Task task);
    void accept(Long id);
    void complete(Long id);
}
