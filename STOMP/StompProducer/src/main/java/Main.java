import ru.itis.jlmq.Jlmq;
import ru.itis.jlmq.JlmqConnector;
import ru.itis.jlmq.JlmqProducer;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        JlmqConnector connector = Jlmq.connector().port("8080").connect();
        JlmqProducer jlmqProducer = connector.producer()
                .toQueue("documents_for_generate")
                .create();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String message = scanner.next();
            jlmqProducer.send(message);
        }
    }
}
