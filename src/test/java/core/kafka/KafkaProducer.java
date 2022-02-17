package core.kafka;

import core.helper.Configure;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

@Log4j2
public class KafkaProducer {
    private static final String BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";
    private static final String ACKS = "kafka.acks";
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

    public KafkaProducer(String data, String bootstrapServers, String topic) {
        this.bootstrapServers = bootstrapServers;
        this.data = data;
        this.topic = topic;
        this.headersmap = new HashMap<String, String>();
    }

    public KafkaProducer(String data, String bootstrapServers, String topic, Map<String, String> headers) {
        this.bootstrapServers = bootstrapServers;
        this.data = data;
        this.topic = topic;
        this.headersmap = headers;
    }

    public void doProduce() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("acks", Configure.getAppProp(ACKS));
        props.put("retries", Integer.parseInt(Configure.getAppProp(RETRIES)));
        props.put("batch.size", Integer.parseInt(Configure.getAppProp(BATCH_SIZE)));
        props.put("linger.ms", Integer.parseInt(Configure.getAppProp(LINGER_MS)));
        props.put("buffer.memory", Integer.parseInt(Configure.getAppProp(BUFFER_MEMORY)));
        props.put("key.serializer", Configure.getAppProp(KEY_SERIALIZER));
        props.put("value.serializer", Configure.getAppProp(VALUE_SERIALIZER));

        Producer<String, String> producer = new org.apache.kafka.clients.producer.KafkaProducer<>(props);
        RecordHeaders headers = new RecordHeaders();
        for (String head : headersmap.keySet()) {
            headers.add(head, headersmap.get(head).getBytes());
        }
        producer.send(new ProducerRecord<>(this.topic, 0, UUID.randomUUID().toString(), this.data, headers), ((recordMetadata, exception) ->
        {
            if (exception == null) {
                log.info("Отправлено новое сообщение, топик " + recordMetadata.topic() + ", Сообщение " + data);

            }else {
                log.error("Ошибка отправки ", exception);
            }
        }));

        producer.close();
    }


}
