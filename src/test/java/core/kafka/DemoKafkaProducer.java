package core.kafka;

import core.helper.Configure;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;

import static core.utils.Waiting.sleep;

@Log4j2
public class DemoKafkaProducer {
    private static final String ACKS = "kafka.acks";
    private static final String RETRIES = "kafka.retries";
    private static final String BATCH_SIZE = "kafka.batch.size";
    private static final String LINGER_MS = "kafka.linger.ms";
    private static final String BUFFER_MEMORY = "kafka.buffer.memory";


    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "dhzorg-kfc001lk.corp.dev.vtb:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        props.put("acks", Configure.getAppProp(ACKS));
//        props.put("retries", Integer.parseInt(Configure.getAppProp(RETRIES)));
//        props.put("batch.size", Integer.parseInt(Configure.getAppProp(BATCH_SIZE)));
//        props.put("linger.ms", Integer.parseInt(Configure.getAppProp(LINGER_MS)));
//        props.put("buffer.memory", Integer.parseInt(Configure.getAppProp(BUFFER_MEMORY)));

        org.apache.kafka.clients.producer.KafkaProducer<Integer, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 10; i++) {
            ProducerRecord<Integer, String> record = new ProducerRecord<>("TutorialTopic", 0, i % 3, "" + i);
            producer.send(record, ((recordMetadata, exception) ->
            {
                if (exception == null) {
                    log.info("Отправленно новое сообщение топик " + recordMetadata.topic() +
                            " партишн " + recordMetadata.partition() + " офсет " + recordMetadata.offset()
                            + " время " + recordMetadata.timestamp());
                } else {
                    log.error("Произошла ошибка при отправке \n" + exception);
                }
            }
            ));
            sleep(1000);
            producer.flush();
            producer.close();
        }
    }
}
