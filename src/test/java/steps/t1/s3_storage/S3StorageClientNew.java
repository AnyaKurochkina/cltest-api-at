package steps.t1.s3_storage;

import models.t1.s3_storage.S3NewEntity;

import static core.helper.Configure.s3StorageNew;

public class S3StorageClientNew extends AbstractS3StorageClient<S3NewEntity> {


    @Override
    String getS3StorageUrl() {
        return s3StorageNew;
    }

    @Override
    S3NewEntity getS3Entity(String name, String projectId) {
        return new S3NewEntity(name, projectId);
    }
}
