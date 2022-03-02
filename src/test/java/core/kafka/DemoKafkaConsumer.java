package core.kafka;

import core.helper.Configure;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.header.internals.RecordHeaders;

import java.io.File;
import java.time.Duration;
import java.util.*;

import static core.utils.Waiting.sleep;

@Log4j2
public class DemoKafkaConsumer {
    private static final String BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";
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
    private static final String KEY_DESERIALIZER = "kafka.key.deserializer";
    private static final String VALUE_DESERIALIZER = "kafka.value.deserializer";


    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "dhzorg-kfc001lk.corp.dev.vtb:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group2");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, Configure.getAppProp(SECURITY_PROTOCOL));
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, KAFKA_PATH + "\\" + Configure.getAppProp(KAFKA_KEYSTORE_PATH));
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, Configure.getAppProp(KAFKA_KEYSTORE_PASSWORD));
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, KAFKA_PATH + "\\" + Configure.getAppProp(KAFKA_TRUSTSTORE_PATH));
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, Configure.getAppProp(KAFKA_TRUSTSTORE_PASSWORD));
        props.put("acks", Configure.getAppProp(ACKS));
        props.put("retries", Integer.parseInt(Configure.getAppProp(RETRIES)));
        props.put("batch.size", Integer.parseInt(Configure.getAppProp(BATCH_SIZE)));
        props.put("linger.ms", Integer.parseInt(Configure.getAppProp(LINGER_MS)));
        props.put("buffer.memory", Integer.parseInt(Configure.getAppProp(BUFFER_MEMORY)));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Configure.getAppProp(KEY_DESERIALIZER));
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Configure.getAppProp(VALUE_DESERIALIZER));
        if (true) {
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10000000");
            props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "10000");
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
            props.put("consumer.timeout.ms", "5000");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
            props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, "2147483646");
        }

        org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer;
        consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(props);
        List<ConsumerRecord<String, String>> consumerRecordList = new ArrayList<>();

        consumer.subscribe(Collections.singletonList("TutorialTopic"));
        boolean isMessageNotEmpty = true;
        int counter = 10;
        while (isMessageNotEmpty && counter > 0) {
            log.debug("Попытка #: " + (11 - counter) + " считать сообщения из топика ");
            counter--;
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(3000));
            for (ConsumerRecord<String, String> record : records) {
                log.debug("========================================");
                log.debug(String.format("offset = %d, key = %s, value = %s, Date=%s%n",
                        record.offset(), record.key(), record.value(), new Date(record.timestamp())));
                log.debug("========================================");
                consumerRecordList.add(record);
                if (!((record.value()) ==null)) {
                    isMessageNotEmpty = false;
                }
            }
            consumer.commitAsync();
            consumer.commitSync();
        }
        consumer.close();
    }
}