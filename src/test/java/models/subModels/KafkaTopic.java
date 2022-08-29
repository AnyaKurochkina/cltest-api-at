package models.subModels;

import com.google.gson.annotations.SerializedName;
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
    @SerializedName("_cleanup^limit_by")
    final String cleanupLimitBy = "time";
}
