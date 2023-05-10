package ui.t1.pages.S3Storage;

import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import ui.elements.*;

public class CreateBucketForm {

    public CreateBucketForm(String bucketName, Boolean isVersioned){
        setBucketName(bucketName);
        clickVersionity(isVersioned);
    }

    @Step("Установка имени бакета '{name}'")
    private void setBucketName(String name){
        Input.byName("name").setValue(name);
    }

    @Step("Установка версиониования бакета")
    private void clickVersionity(Boolean isVersioned){
        Switch.byText("Версионирование").setEnabled(isVersioned);
//        CheckBox.byLabel("Версионирование").setChecked(isVersioned);
    }

    @Step("Создание бакета")
    public CloudStorageS3 createBucket(){
        Button.byText("Создать").click();
        Waiting.sleep(5000);
        return new CloudStorageS3();
    }

    @Step("Закрытие формы создания бакета")
    public void closeBucketForm(){
        Button.byText("Закрыть").click();
    }
}
