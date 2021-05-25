package clp.core.messages;

import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

public class KafkaMessage extends Message {
    private String key;
    private Headers headers;

    public String getKey() {
        return key;
    }


    public Header[] getKafkaHeaders() {
        return headers.toArray();
    }

    public KafkaMessage(String body) {
        super(body);
    }

    public KafkaMessage(String body,String key,Headers headers){
        super(body);
        this.key=key;
        this.headers=headers;
    }


}
