package ui.t1.pages.S3Storage.Objects;

import io.qameta.allure.Step;
import ui.elements.*;
import ui.t1.pages.S3Storage.AbstractLayerS3;
import ui.t1.pages.S3Storage.CORS.CORSLayer;

public class ObjectsModal extends AbstractLayerS3<ObjectsModal> {

    public ObjectsModal()
    {

    }

    @Step("Загрузка объекта '{path}'")
    public ObjectsLayer addObject(String path)
    {
        new InputFile(path).importFile();
        Button.byText("Загрузить").click();
        Alert.green("Файлов успешно загружено: 1");
        new Dialog("Загрузить объект").clickButton("Закрыть");
        return new ObjectsLayer("Объекты");
    }

    @Step("Загрузка объектов '{path}'")
    public ObjectsLayer addObjects(String... pathes)
    {
        for  (String path:pathes)
            new InputFile(path).importFile();
        Button.byText("Загрузить").click();
        Alert.green("Файлов успешно загружено: " + pathes.length);
        new Dialog("Загрузить объект").clickButton("Закрыть");
        return new ObjectsLayer("Объекты");
    }
}
