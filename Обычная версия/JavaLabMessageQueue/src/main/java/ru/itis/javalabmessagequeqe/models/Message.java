package ru.itis.javalabmessagequeqe.models;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Message {
    private String command;
    private String queueName;
    private Object body;
    private Long messageId;
}
