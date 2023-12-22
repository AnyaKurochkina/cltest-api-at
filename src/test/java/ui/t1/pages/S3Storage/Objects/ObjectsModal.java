package ui.t1.pages.S3Storage.Objects;

import io.qameta.allure.Step;
import ui.elements.*;
import ui.t1.pages.S3Storage.AbstractLayerS3;

import java.util.Arrays;

public class ObjectsModal extends AbstractLayerS3<ObjectsModal> {

    @Step("Загрузка объекта '{path}'")
    public ObjectsLayer addObject(String path, AccessBucketLevel access)
    {
        Select.byLabel("Доступ").set(access.getValue());
        new FileImportDialog(path).importFile();
        Button.byText("Загрузить").click();
        Alert.green("Файлов успешно загружено: 1");
        new Dialog("Загрузить объект").clickButton("Закрыть");
        return new ObjectsLayer("Объекты");
    }

    @Step("Загрузка объектов {1}")
    public ObjectsLayer addObjects(AccessBucketLevel access, String... paths)
    {
        Select.byLabel("Доступ").set(access.getValue());
        Arrays.stream(paths)
                .forEach(path -> new FileImportDialog(path).importFile());
        Button.byText("Загрузить").click();
        Alert.green("Файлов успешно загружено: " + paths.length);
        new Dialog("Загрузить объект").clickButton("Закрыть");
        return new ObjectsLayer("Объекты");
    }
}
