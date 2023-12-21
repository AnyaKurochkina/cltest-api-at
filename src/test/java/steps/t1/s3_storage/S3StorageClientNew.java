package steps.t1.s3_storage;

import static core.helper.Configure.s3StorageNew;

public class S3StorageClientNew extends S3StorageClient {


    @Override
    String getS3StorageUrl() {
        return s3StorageNew;
    }
}
