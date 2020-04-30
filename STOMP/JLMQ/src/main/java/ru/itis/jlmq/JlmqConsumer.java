package ru.itis.jlmq;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import ru.itis.models.Message;

import javax.websocket.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JlmqConsumer {
    private String queue;
    private MessageReceiver messageReceiver;
    private Gson gson = new Gson();

    private String uri = "ws://localhost:%s/messages";

    @SneakyThrows
    protected JlmqConsumer(String port, String queue, MessageReceiver messageReceiver) {
        uri = String.format(uri, port);
        this.queue = queue;
        this.messageReceiver = messageReceiver;
        List<Transport> transports = new ArrayList<Transport>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketClient webSocketClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new StringMessageConverter());
        StompSessionHandler handler = new ConsumerStompSessionHandler();
        stompClient.connect(uri, handler);
        new Scanner(System.in).nextLine();
    }

    public class ConsumerStompSessionHandler extends StompSessionHandlerAdapter {
        StompSession session;

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            this.session = session;
            session.subscribe("/queue/" + queue, this);
            session.send("/app/subscribe", queue);
        }


        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            Message m = gson.fromJson((String) payload, Message.class);
            sendMessage("/app/accepted", Message.builder().messageId(m.getMessageId()).build());
            messageReceiver.receive(m);
            sendMessage("/app/completed", Message.builder().messageId(m.getMessageId()).queueName(queue).build());
        }

        private void sendMessage(String url, Message m) {
            session.send(url, gson.toJson(m));
        }
    }

}
