package ui.t1.pages.S3Storage.Objects;

import io.qameta.allure.Step;
import ui.elements.*;
import ui.t1.pages.S3Storage.AbstractLayerS3;

public class ObjectsModal extends AbstractLayerS3<ObjectsModal> {

    public enum AccessLevel {
        OWNERONLY("Доступ только у владельца"),
        READFORALL("Чтение для всех пользователей"),
        READANDWRITEFORALL("Запись и чтение для всех пользователей"),
        READFORAUTH("Чтение для аутентифицированных пользователей");

        private final String level;

        AccessLevel(String level) {
            this.level = level;
        }

        public String getAccess() {
            return level;
        }
    }

    public ObjectsModal()
    {

    }

    @Step("Загрузка объекта '{path}'")
    public ObjectsLayer addObject(String path, AccessLevel access)
    {
        Select.byLabel("Доступ").set(access.getAccess());
        new InputFile(path).importFile();
        Button.byText("Загрузить").click();
        Alert.green("Файлов успешно загружено: 1");
        new Dialog("Загрузить объект").clickButton("Закрыть");
        return new ObjectsLayer("Объекты");
    }

    @Step("Загрузка объектов '{path}'")
    public ObjectsLayer addObjects(AccessLevel access, String... pathes)
    {
        Select.byLabel("Доступ").set(access.getAccess());
        for  (String path:pathes)
            new InputFile(path).importFile();
        Button.byText("Загрузить").click();
        Alert.green("Файлов успешно загружено: " + pathes.length);
        new Dialog("Загрузить объект").clickButton("Закрыть");
        return new ObjectsLayer("Объекты");
    }
}
