package models.t1.s3_storage;

import steps.t1.s3_storage.AbstractS3StorageClient;
import steps.t1.s3_storage.S3StorageClientOld;

public class S3OldEntity extends S3Entity {

    public S3OldEntity(String name, String projectId) {
        super(name, projectId);
    }

    @Override
    AbstractS3StorageClient<?> getS3Client() {
        return new S3StorageClientOld();
    }
}
