package ui.t1.pages.S3Storage;

import io.qameta.allure.Step;
import ui.elements.*;

public class CreateBucketForm {

    public CreateBucketForm(String bucketName, Boolean isVersioned){
        setBucketName(bucketName);
        clickVersionity(isVersioned);
    }

    private Boolean isVers = false;

    @Step("Установка имени бакета '{name}'")
    private void setBucketName(String name){
        Input.byName("name").setValue(name);
    }

    @Step("Установка версиониования бакета")
    private void clickVersionity(Boolean isVersioned){
        Switch.byText("Версионирование").setEnabled(isVersioned);
        this.isVers = isVersioned;
    }

    @Step("Создание бакета")
    public CloudStorageS3 createBucket(){
        Button.byText("Создать").click();
        Alert.green("Бакет успешно добавлен");
        if (this.isVers)
            Alert.green("Версионирование включено");
        return new CloudStorageS3();
    }

    @Step("Создание бакета")
    public CreateBucketForm createSameBucket(){
        Button.byText("Создать").click();
        Alert.red("Бакет с таким именем уже существует");
        return this;
    }

    @Step("Закрытие формы создания бакета")
    public CloudStorageS3 closeBucketForm(){
        Button.byText("Закрыть").click();
        return new CloudStorageS3();
    }
}
