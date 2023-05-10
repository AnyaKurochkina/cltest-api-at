package ui.t1.pages.S3Storage;

import core.utils.Waiting;
import io.qameta.allure.Step;
import ui.elements.Button;
import ui.elements.Input;

public class DeleteBucketForm {

    @Step("Установка имени удаляемого бакета '{name}'")
    private void setBucketName(String name){
        Input.byName("id").setValue(name);
    }

    @Step("Удаление бакета '{name}'")
    public CloudStorageS3 deleteBucket(String name){
        setBucketName(name);
        Button.byText("Удалить").click();
        Waiting.sleep(5000);
        return new CloudStorageS3();
    }

    @Step("Закрытие формы удаления бакета")
    public void closeBucketForm(){
        Button.byText("Отмена").click();
    }
}
