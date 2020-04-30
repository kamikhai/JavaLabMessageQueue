import ru.itis.jlmq.Jlmq;
import ru.itis.jlmq.JlmqConnector;
import ru.itis.jlmq.JlmqConsumer;

public class Main {
    public static void main(String[] args) {
        JlmqConnector connector = Jlmq.connector().port("8080").connect();
        JlmqConsumer jlmqConsumer = connector.consumer()
                .subscribe("documents_for_generate")
                .onReceive(message -> {
                    System.out.println(message);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new IllegalStateException(e);
                    }
                    System.out.println(message);
                })
                .create();
    }
}
