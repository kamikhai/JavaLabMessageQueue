package ru.itis.jlmq;

import com.google.gson.Gson;
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

public class JlmqProducer {
    private String queue;

    private Gson gson = new Gson();
    private ProducerStompSessionHandler handler;

    private String uri = "ws://localhost:%s/messages";

    protected JlmqProducer(String port, String queue) {
        uri = String.format(uri, port);
        this.queue = queue;
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketClient transport = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(transport);
        stompClient.setMessageConverter(new StringMessageConverter());
        handler = new ProducerStompSessionHandler();
        stompClient.connect(uri, handler);
    }


    public class ProducerStompSessionHandler extends StompSessionHandlerAdapter {
        StompSession session;
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            this.session = session;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            String msg = (String) payload;
            System.out.println("Producer Received : " + msg);
        }

        private void sendMessage(String message){
            session.send("/app/" + queue, message);
        }

    }
    public void send(Object body){
        handler.sendMessage(gson.toJson(Message.builder().body(body).build()));
    }

}
