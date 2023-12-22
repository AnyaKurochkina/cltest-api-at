package models.t1.s3_storage;

import steps.t1.s3_storage.AbstractS3StorageClient;
import steps.t1.s3_storage.S3StorageClientNew;

public class S3NewEntity extends S3Entity {

    public S3NewEntity(String name, String projectId) {
        super(name, projectId);
    }

    @Override
    AbstractS3StorageClient<?> getS3Client() {
        return new S3StorageClientNew();
    }
}
