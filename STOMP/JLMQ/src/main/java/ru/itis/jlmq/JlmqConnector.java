package ru.itis.jlmq;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PACKAGE)
public class JlmqConnector {
    private String port;

    public JlmqConnector.ProducerBuilder producer(){
        return new ProducerBuilder();
    }

    public JlmqConnector.ConsumerBuilder consumer(){
        return new ConsumerBuilder();
    }

    public class ProducerBuilder{
        private String queue = "defaultQueue";

        public ProducerBuilder toQueue(String queue){
            this.queue = queue;
            return this;
        }

        public JlmqProducer create(){
            return new JlmqProducer(port, queue);
        }

    }

    public class ConsumerBuilder{
        private String queue = "defaultQueue";
        private MessageReceiver messageReceiver = message -> System.out.println(message.getBody());

        public ConsumerBuilder subscribe(String queue){
            this.queue = queue;
            return this;
        }

        public ConsumerBuilder onReceive(MessageReceiver messageReceiver){
            this.messageReceiver = messageReceiver;
            return this;
        }
        public JlmqConsumer create(){
            return new JlmqConsumer(port,queue,messageReceiver);
        }

    }


}
