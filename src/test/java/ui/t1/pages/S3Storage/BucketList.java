package ui.t1.pages.S3Storage;

import io.qameta.allure.Step;
import ui.elements.DataTable;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BucketList {
    private final String fBucketName = "Название бакета";

    @Step("Открытие страницы просмотра данных бакета '{bucketName}'")
    public void openBucket(String bucketName) {
        DataTable bucketList = new DataTable(fBucketName);
        bucketList.getRowByColumnValue(fBucketName, bucketName)
                  .getElementByColumn(fBucketName)
                  .click();
    }

    @Step("Проверка наличия бакета '{bucketName}' в списке - '{isExists}'")
    public void checkBucket(String bucketName, Boolean isExists){
        DataTable bucketList = new DataTable(fBucketName);
        if (isExists)
            assertTrue(bucketList.isColumnValueEquals(fBucketName,bucketName));
        else
            assertFalse(bucketList.isColumnValueEquals(fBucketName,bucketName));
    }

    @Step("Открытие страницы просмотра данных бакета '{bucketName}'")
    public DeleteBucketForm deleteBucket(String bucketName){
        DataTable bucketList = new DataTable(fBucketName);
        bucketList.getRowByColumnValue(fBucketName, bucketName)
                .getElementByColumnIndex(4).$x("..//button").click();
        return new DeleteBucketForm();
    }
}
