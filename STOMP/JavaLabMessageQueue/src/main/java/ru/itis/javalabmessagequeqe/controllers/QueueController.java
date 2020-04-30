package ru.itis.javalabmessagequeqe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.javalabmessagequeqe.dto.QueueDto;
import ru.itis.javalabmessagequeqe.models.Queue;
import ru.itis.javalabmessagequeqe.services.QueueService;

@RestController
public class QueueController {
    @Autowired
    private QueueService queueService;

    @PostMapping("/queue")
    public Long createQueue(@RequestBody QueueDto queueDto){
        return queueService.save(Queue.builder().name(queueDto.getName()).build());
    }
}
