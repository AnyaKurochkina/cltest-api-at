package core.kafka;

import core.helper.Configure;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.header.internals.RecordHeaders;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.fail;

@Log4j2
public class CustomKafkaProducer {

    private static final String KAFKA_PATH = new File("src/test/java/core/kafka").getAbsolutePath();
    private static final String KAFKA_KEYSTORE_PATH = "kafka.keystore.cert";
    private static final String KAFKA_TRUSTSTORE_PATH = "kafka.truststore.cert";
    private static final String KAFKA_KEYSTORE_PASSWORD = "keystore.password";
    private static final String KAFKA_TRUSTSTORE_PASSWORD = "truststore.password";
    private static final String ACKS = "kafka.acks";
    private static final String SECURITY_PROTOCOL = "security.protocol";
    private static final String RETRIES = "kafka.retries";
    private static final String BATCH_SIZE = "kafka.batch.size";
    private static final String LINGER_MS = "kafka.linger.ms";
    private static final String BUFFER_MEMORY = "kafka.buffer.memory";
    private static final String KEY_SERIALIZER = "kafka.key.serializer";
    private static final String VALUE_SERIALIZER = "kafka.value.serializer";
    private final String topic;
    private final String data;
    private final Map<String, String> headersmap;
    @Setter
    private String bootstrapServers;

    public CustomKafkaProducer(String data, String bootstrapServers, String topic) {
        this.bootstrapServers = bootstrapServers;
        this.data = data;
        this.topic = topic;
        this.headersmap = new HashMap<String, String>();
    }

    public CustomKafkaProducer(String data, String bootstrapServers, String topic, Map<String, String> headers) {
        this.bootstrapServers = bootstrapServers;
        this.data = data;
        this.topic = topic;
        this.headersmap = headers;
    }

    public void doProduce() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
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

        Producer<String, String> producer = new org.apache.kafka.clients.producer.KafkaProducer<>(props);
        RecordHeaders headers = new RecordHeaders();
        for (String head : headersmap.keySet()) {
            headers.add(head, headersmap.get(head).getBytes());
        }
        producer.send(new ProducerRecord<>(this.topic, 0, UUID.randomUUID().toString(), this.data, headers), ((recordMetadata, exception) ->
        {
            if (exception == null) {
                log.info("Отправлено новое сообщение, в топик: " + recordMetadata.topic() + ", Сообщение: " + data);

            }else {
                log.error("Ошибка отправки: ", exception);
            }
        }));

        producer.close();
    }


}
