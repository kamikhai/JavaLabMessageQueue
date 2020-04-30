package ru.itis.models;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
public class Message {
    private String queueName;
    private Object body;
    private Long messageId;
}
