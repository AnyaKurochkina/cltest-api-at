package steps.t1.s3_storage;

import models.t1.s3_storage.S3OldEntity;

import static core.helper.Configure.s3StorageOld;

public class S3StorageClientOld extends AbstractS3StorageClient<S3OldEntity> {


    @Override
    String getS3StorageUrl() {
        return s3StorageOld;
    }

    @Override
    public S3OldEntity getS3Entity(String name, String projectId) {
        return new S3OldEntity(name, projectId);
    }
}
