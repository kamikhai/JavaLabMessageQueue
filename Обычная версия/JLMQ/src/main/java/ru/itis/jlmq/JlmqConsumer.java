package ru.itis.jlmq;

import com.google.gson.Gson;
import ru.itis.models.Message;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class JlmqConsumer {
    private String queue;
    private MessageReceiver messageReceiver;
    private Gson gson = new Gson();

    private String uri = "ws://localhost:%s/message";
    private Session session;

    protected JlmqConsumer(String port, String queue, MessageReceiver messageReceiver) {
        uri = String.format(uri, port);
        this.queue = queue;
        this.messageReceiver = messageReceiver;
        try {
            WebSocketContainer container = ContainerProvider.
                    getWebSocketContainer();
            container.connectToServer(this, new URI(uri));

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        send(Message.builder().command("subscribe").queueName(queue).build());
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        Message m = gson.fromJson(message, Message.class);
        if (m.getCommand().equals("receive")){
            send(Message.builder().command("accepted").messageId(m.getMessageId()).build());
            messageReceiver.receive(m);
            send(Message.builder().command("completed").messageId(m.getMessageId()).queueName(queue).build());
        } else {
            System.out.println("Ответ сервера: " + m.getBody());
        }
    }

    public void send(Message message) {
        try {
            session.getBasicRemote().sendText(gson.toJson(message));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
