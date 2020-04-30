package ru.itis.jlmq;


import ru.itis.models.Message;

public interface MessageReceiver{
    void receive(Message message);
}