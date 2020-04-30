package ru.itis.jlmq;

import com.google.gson.Gson;
import ru.itis.models.Message;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class JlmqProducer {
    private String queue;

    private Gson gson = new Gson();

    private String uri = "ws://localhost:%s/message";
    private Session session;

    protected JlmqProducer(String port, String queue) {
        uri = String.format(uri, port);
        this.queue = queue;
        try {
            WebSocketContainer container = ContainerProvider.
                    getWebSocketContainer();
            container.connectToServer(this, new URI(uri));

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        Message m = gson.fromJson(message, Message.class);
        System.out.println("Ответ сервера: " + m.getBody());
    }

    public void send(Object body) {
        Message message = Message.builder().command("send").queueName(queue).body(body).build();
        try {
            session.getBasicRemote().sendText(gson.toJson(message));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
