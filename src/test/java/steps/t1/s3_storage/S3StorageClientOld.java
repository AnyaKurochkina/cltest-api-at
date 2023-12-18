package steps.t1.s3_storage;

import static core.helper.Configure.S3StorageOld;

public class S3StorageClientOld extends S3StorageClient {


    @Override
    String getS3StorageUrl() {
        return S3StorageOld;
    }
}
