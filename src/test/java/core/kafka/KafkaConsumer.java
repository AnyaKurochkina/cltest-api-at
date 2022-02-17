package core.kafka;

import core.helper.Configure;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

@Log4j2
public class KafkaConsumer implements Runnable {

    public List<ConsumerRecord<String, String>> getConsumerRecordList() {
        return consumerRecordList;
    }

    private final List<ConsumerRecord<String,String>> consumerRecordList = new ArrayList<>();
    private static final String ACKS = "kafka.acks";
    private static final String RETRIES = "kafka.retries";
    private static final String BATCH_SIZE = "kafka.batch.size";
    private static final String LINGER_MS = "kafka.linger.ms";
    private static final String BUFFER_MEMORY = "kafka.buffer.memory";
    private static final String KEY_SERIALIZER = "kafka.key.serializer";
    private static final String VALUE_SERIALIZER = "kafka.value.serializer";
    private static final String KEY_DESERIALIZER = "kafka.key.deserializer";
    private static final String VALUE_DESERIALIZER = "kafka.value.deserializer";
    private final String topic;
    private org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer;
    private final boolean readAllMessages;
    @Setter
    private String bootstrapServers;

    public KafkaConsumer(String topic, String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
        this.topic = topic;
        this.readAllMessages = false;
    }

    public KafkaConsumer(String topic, String bootstrapServers, boolean readAllMessages){
        this.bootstrapServers = bootstrapServers;
        this.topic = topic;
        this.readAllMessages = readAllMessages;

    }


    public void doConsume(){
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("acks", Configure.getAppProp(ACKS));
        props.put("retries", Integer.parseInt(Configure.getAppProp(RETRIES)));
        props.put("batch.size", Integer.parseInt(Configure.getAppProp(BATCH_SIZE)));
        props.put("linger.ms", Integer.parseInt(Configure.getAppProp(LINGER_MS)));
        props.put("buffer.memory", Integer.parseInt(Configure.getAppProp(BUFFER_MEMORY)));
        props.put("key.serializer", Configure.getAppProp(KEY_SERIALIZER));
        props.put("value.serializer", Configure.getAppProp(VALUE_SERIALIZER));
        props.put("key.deserializer", Configure.getAppProp(KEY_DESERIALIZER));
        props.put("value.deserializer", Configure.getAppProp(VALUE_DESERIALIZER));
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
        consumer.subscribe(Collections.singletonList(topic));
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
