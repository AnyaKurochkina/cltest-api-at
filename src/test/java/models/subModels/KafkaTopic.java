package models.subModels;

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
}
