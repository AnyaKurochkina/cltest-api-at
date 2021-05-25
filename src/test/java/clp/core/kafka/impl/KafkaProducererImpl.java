package clp.core.kafka.impl;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import clp.core.helpers.Configurier;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class KafkaProducererImpl {
    private static final Configurier configer = Configurier.getInstance();
    private static final String BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";
    private static final String ACKS = "kafka.acks";
    private static final String RETRIES = "kafka.retries";
    private static final String BATCH_SIZE = "kafka.batch.size";
    private static final String LINGER_MS = "kafka.linger.ms";
    private static final String BUFFER_MEMORY = "kafka.buffer.memory";
    private static final String KEY_SERIALIZER = "kafka.key.serializer";
    private static final String VALUE_SERIALIZER = "kafka.value.serializer";
    private String topic;
    private String data;
    private Map<String,String> headersmap;

    public KafkaProducererImpl(String data, String topic) {

        this.data = data;
        this.topic = topic;
        this.headersmap = new HashMap<String, String>();
    }

    public KafkaProducererImpl(String data, String topic, Map<String,String> headers) {

        this.data = data;
        this.topic = topic;
        this.headersmap = headers;
    }

    public void doProduce() {
        Properties props = new Properties();
        props.put("bootstrap.servers", configer.getAppProp(BOOTSTRAP_SERVERS));
        props.put("acks", configer.getAppProp(ACKS));
        props.put("retries", Integer.parseInt(configer.getAppProp(RETRIES)));
        props.put("batch.size", Integer.parseInt(configer.getAppProp(BATCH_SIZE)));
        props.put("linger.ms", Integer.parseInt(configer.getAppProp(LINGER_MS)));
        props.put("buffer.memory", Integer.parseInt(configer.getAppProp(BUFFER_MEMORY)));
        props.put("key.serializer", configer.getAppProp(KEY_SERIALIZER));
        props.put("value.serializer", configer.getAppProp(VALUE_SERIALIZER));

        Producer<String, String> producer = new KafkaProducer<>(props);
        RecordHeaders headers = new RecordHeaders();
        for (String head: headersmap.keySet()){
            headers.add(head,headersmap.get(head).getBytes());
        }
        producer.send(new ProducerRecord<>(this.topic,0, UUID.randomUUID().toString(), this.data,headers));
//        producer.send(new ProducerRecord<>(this.topic, UUID.randomUUID().toString(), this.data));

        producer.close();
    }


}
