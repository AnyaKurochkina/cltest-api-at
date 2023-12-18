package ui.t1.pages.S3Storage;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static core.helper.StringUtils.$x;

public class CloudStorageS3 {

    private final SelenideElement addButton = $x("//*[contains(@data-testid,'add-button')]//button");

    private BucketList bList = new BucketList();

    @Step("Добавление бакета '{bucketName}' с версионированием '{isVersioned}'")
    public CreateBucketForm addBucket(String bucketName, Boolean isVersioned) {
        addButton.click();
        return new CreateBucketForm(bucketName, isVersioned);
    }

    @Step("Открытие страницы бакета '{bucketName}'")
    public BucketPage openBucket(String bucketName) {
        bList.OpenBucket(bucketName);
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
