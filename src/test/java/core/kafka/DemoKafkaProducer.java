package core.kafka;

import core.helper.Configure;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.File;
import java.util.Properties;
import java.util.Random;

import static core.utils.Waiting.sleep;

@Log4j2
public class DemoKafkaProducer {
    private static final String BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";
    private static final String KAFKA_PATH = new File("src/test/java/core/kafka").getAbsolutePath();
    private static final String KAFKA_KEYSTORE_PATH = "kafka.keystore.cert";
    private static final String KAFKA_TRUSTSTORE_PATH = "kafka.truststore.cert";
    private static final String KAFKA_KEYSTORE_PASSWORD = "keystore.password";
    private static final String KAFKA_TRUSTSTORE_PASSWORD = "truststore.password";
    private static final String SECURITY_PROTOCOL = "security.protocol";
    private static final String ACKS = "kafka.acks";
    private static final String RETRIES = "kafka.retries";
    private static final String BATCH_SIZE = "kafka.batch.size";
    private static final String LINGER_MS = "kafka.linger.ms";
    private static final String BUFFER_MEMORY = "kafka.buffer.memory";
    private static final String KEY_SERIALIZER = "kafka.key.serializer";
    private static final String VALUE_SERIALIZER = "kafka.value.serializer";


    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "dhzorg-kfc001lk.corp.dev.vtb:9092");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, Configure.getAppProp(SECURITY_PROTOCOL));
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, KAFKA_PATH + "\\" + Configure.getAppProp(KAFKA_KEYSTORE_PATH));
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, Configure.getAppProp(KAFKA_KEYSTORE_PASSWORD));
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, KAFKA_PATH + "\\" + Configure.getAppProp(KAFKA_TRUSTSTORE_PATH));
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, Configure.getAppProp(KAFKA_TRUSTSTORE_PASSWORD));
        props.put(ProducerConfig.ACKS_CONFIG, Configure.getAppProp(ACKS));
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.parseInt(Configure.getAppProp(RETRIES)));
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.parseInt(Configure.getAppProp(BATCH_SIZE)));
        props.put(ProducerConfig.LINGER_MS_CONFIG, Integer.parseInt(Configure.getAppProp(LINGER_MS)));
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, Integer.parseInt(Configure.getAppProp(BUFFER_MEMORY)));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Configure.getAppProp(KEY_SERIALIZER));
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Configure.getAppProp(VALUE_SERIALIZER));

        org.apache.kafka.clients.producer.KafkaProducer<Integer, String> producer = new KafkaProducer<>(props);
        RecordHeaders headers = new RecordHeaders();

        ProducerRecord<Integer, String> record = new ProducerRecord<>("TutorialTopic", 0, new Random().nextInt(25), "Some message from autotest", headers);
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

//    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        String server = "localhost:9092";
//        String topicName = "to-do-list";
//
//        final Properties props = new Properties();
//
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                server);
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
//                IntegerSerializer.class.getName());
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
//                StringSerializer.class.getName());
//
//        final Producer<Integer, String> producer =
//                new KafkaProducer<>(props);
//
//        for (int i = 0; i < 10; i++) {
//            ProducerRecord<Integer, String> record = new ProducerRecord<>("to-do-list", 1, i % 3, "" + i);
//            producer.send(record, ((recordMetadata, exception) ->
//            {
//                if (exception == null) {
//                    log.info("Отправленно новое сообщение топик " + recordMetadata.topic() +
//                            " партишн " + recordMetadata.partition() + " офсет " + recordMetadata.offset()
//                            + " время " + recordMetadata.timestamp());
//                } else {
//                    log.error("Произошла ошибка при отправке \n" + exception);
//                }
//            }
//            ));
//            sleep(1000);
////            producer.flush();
////            producer.close();
//        }
//    }
}
