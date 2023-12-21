package ui.t1.pages.S3Storage;

import io.qameta.allure.Step;
import ui.elements.DataTable;

public class CloudStorageS3 extends DataTable {


    private final BucketList bList = new BucketList();

    public CloudStorageS3() {
        super("Название бакета");
    }

    @Step("Добавление бакета '{bucketName}' с версионированием '{isVersioned}'")
    public CreateBucketForm addBucket(String bucketName, Boolean isVersioned) {
        clickAdd();
        return new CreateBucketForm(bucketName, isVersioned);
    }

    @Step("Открытие страницы бакета '{bucketName}'")
    public BucketPage openBucket(String bucketName) {
        bList.openBucket(bucketName);
        return new BucketPage();
    }

    public CloudStorageS3 checkBucketExistence(String bucketName, Boolean isExists) {
        bList.checkBucket(bucketName, isExists);
        return this;
    }

    @Step("Открытие страницы удаления бакета '{bucketName}'")
    public CloudStorageS3 deleteBucket(String bucketName) {
        DeleteBucketForm delForm = bList.deleteBucket(bucketName);
        delForm.deleteBucket(bucketName);
        return this;
    }
}
