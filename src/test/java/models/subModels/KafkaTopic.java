package models.subModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class KafkaTopic {
    String cleanupPolicy;
//    int minInsyncReplicas;
    int partitionsNumber;
//    int replicationFactor;
    long retentionMs;
    String topicName;
    @JsonProperty("_cleanup^limit_by")
    final String cleanupLimitBy = "time";
}
