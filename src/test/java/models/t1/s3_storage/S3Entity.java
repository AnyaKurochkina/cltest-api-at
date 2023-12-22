package models.t1.s3_storage;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import models.AbstractEntity;
import steps.t1.s3_storage.AbstractS3StorageClient;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
public abstract class S3Entity extends AbstractEntity {

    abstract AbstractS3StorageClient<?> getS3Client();

    private String name;
    private String projectId;

    @Override
    public void delete() {
        getS3Client().deleteS3(name, projectId);
    }
}
