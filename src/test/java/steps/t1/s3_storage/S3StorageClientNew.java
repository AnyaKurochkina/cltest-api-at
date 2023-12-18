package steps.t1.s3_storage;

import steps.Steps;

import static core.helper.Configure.*;

public class S3StorageClientNew extends S3StorageClient {


    @Override
    String getS3StorageUrl() {
        return S3StorageNew;
    }
}
