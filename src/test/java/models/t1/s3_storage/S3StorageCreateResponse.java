package models.t1.s3_storage;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class S3StorageCreateResponse {

        public String name;
        public LocalDateTime createdAt;
        public int totalObjects;
        public int totalSize;
        public String ownerId;
        public Object website;
}
