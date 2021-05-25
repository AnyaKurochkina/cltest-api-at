package clp.core.kafka.impl;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clp.core.helpers.Configurier;
import java.time.Duration;
import java.util.*;

public class KafkaConsumerImpl implements Runnable {
    private static final Configurier configer = Configurier.getInstance();

    public List<ConsumerRecord<String, String>> getConsumerRecordList() {
        return consumerRecordList;
    }

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerImpl.class);
    private List<ConsumerRecord<String,String>> consumerRecordList = new ArrayList<>();
    private static final String BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";
    private static final String ACKS = "kafka.acks";
    private static final String RETRIES = "kafka.retries";
    private static final String BATCH_SIZE = "kafka.batch.size";
    private static final String LINGER_MS = "kafka.linger.ms";
    private static final String BUFFER_MEMORY = "kafka.buffer.memory";
    private static final String KEY_SERIALIZER = "kafka.key.serializer";
    private static final String VALUE_SERIALIZER = "kafka.value.serializer";
    private static final String KEY_DESERIALIZER = "kafka.key.deserializer";
    private static final String VALUE_DESERIALIZER = "kafka.value.deserializer";
    private String topic;
    private org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer;
    private boolean readAllMessages;

    public KafkaConsumerImpl(String topic) {
        this.topic = topic;
        this.readAllMessages = false;
    }

    public KafkaConsumerImpl(String topic, boolean readAllMessages){
        this.topic = topic;
        this.readAllMessages = readAllMessages;

    }


    public void doConsume(){
        Properties props = new Properties();
        props.put("bootstrap.servers", configer.getAppProp(BOOTSTRAP_SERVERS));
        props.put("acks", configer.getAppProp(ACKS));
        props.put("retries", Integer.parseInt(configer.getAppProp(RETRIES)));
        props.put("batch.size", Integer.parseInt(configer.getAppProp(BATCH_SIZE)));
        props.put("linger.ms", Integer.parseInt(configer.getAppProp(LINGER_MS)));
        props.put("buffer.memory", Integer.parseInt(configer.getAppProp(BUFFER_MEMORY)));
        props.put("key.serializer", configer.getAppProp(KEY_SERIALIZER));
        props.put("value.serializer", configer.getAppProp(VALUE_SERIALIZER));
        props.put("key.deserializer", configer.getAppProp(KEY_DESERIALIZER));
        props.put("value.deserializer", configer.getAppProp(VALUE_DESERIALIZER));
        props.put("group.id",topic);
        if (readAllMessages){
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
            props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,"10000000");
            props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,"10000");
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);
            props.put("consumer.timeout.ms","5000");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
            props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,"2147483646");
        }


        consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topic));
        boolean isMessageNotEmpty = true;
        while (isMessageNotEmpty) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10000));
            for (ConsumerRecord<String, String> record : records) {
                log.debug("========================================");
                log.debug(String.format("offset = %d, key = %s, value = %s, Date=%s%n", record.offset(), record.key(), record.value(),new Date(record.timestamp())));
                log.debug("========================================");
                consumerRecordList.add(record);
                if (!record.value().equals("")) {
                    isMessageNotEmpty = false;
                }
            }
            consumer.commitAsync();
            consumer.commitSync();
        }
    }

    public void stopConsume(){
        if (consumer != null) {
            consumer.close();
        }
    }

    @Override
    public void run() {
        doConsume();
    }
}
