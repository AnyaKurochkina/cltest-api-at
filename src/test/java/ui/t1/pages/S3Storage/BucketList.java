package ui.t1.pages.S3Storage;

import io.qameta.allure.Step;
import ui.elements.DataTable;
import ui.elements.Table;

public class BucketList {
    private final String fBucketName = "Название бакета";
    private final Integer delIdx = 4;

//    private DataTable bucketList = new DataTable(fBucketName);

    @Step("Открытие страницы просмотра данных бакета '{bucketName}'")
    public void OpenBucket(String bucketName){
        DataTable bucketList = new DataTable(fBucketName);
        bucketList.getRowByColumnValue(fBucketName, bucketName)
                  .getElementByColumn(fBucketName)
                  .click();
    }

    @Step("Открытие страницы просмотра данных бакета '{bucketName}'")
    public DeleteBucketForm DeleteBucket(String bucketName){
        DataTable bucketList = new DataTable(fBucketName);
        bucketList.getRowByColumnValue(fBucketName, bucketName)
                .getElementByColumnIndex(delIdx).$x("..//button").click();
        return new DeleteBucketForm();
    }
}
