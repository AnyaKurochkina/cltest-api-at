package ui.t1.pages.S3Storage;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static core.helper.StringUtils.$x;

public class CloudStorageS3 {

    private SelenideElement btnAdd = $x("//*[contains(@data-testid,'add-button')]//button");

    private   BucketList bList = new BucketList();

    @Step("Добавление бакета '{bucketName}' с версионированием '{isVersioned}'")
    public CreateBucketForm addBucket(String bucketName, Boolean isVersioned){
        btnAdd.click();
        return new CreateBucketForm(bucketName, isVersioned);
    }

    @Step("Открытие страницы бакета '{bucketName}'")
    public BucketPage openBucket(String bucketName){
        bList.OpenBucket(bucketName);
        return new BucketPage();
    }

    @Step("Открытие страницы удаления бакета '{bucketName}'")
    public void deleteBucket(String bucketName){
        DeleteBucketForm delForm = bList.DeleteBucket(bucketName);
        delForm.deleteBucket(bucketName);
    }
}
