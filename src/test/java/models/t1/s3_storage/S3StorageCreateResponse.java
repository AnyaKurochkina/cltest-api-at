package models.t1.s3_storage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

@Getter
@Builder
public class S3StorageCreateResponse {

        public String name;
        @JsonProperty("created_at")
        public LocalDateTime createdAt;
        @JsonProperty("total_objects")
        public int totalObjects;
        @JsonProperty("total_size")
        public int totalSize;
        @JsonProperty("owner_id")
        public String ownerId;
        public Object website;
}
