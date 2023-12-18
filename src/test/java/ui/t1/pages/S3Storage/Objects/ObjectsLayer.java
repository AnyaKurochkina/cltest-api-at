package ui.t1.pages.S3Storage.Objects;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.NoArgsConstructor;
import ui.elements.*;
import ui.t1.pages.S3Storage.AbstractLayerS3;

import java.util.Arrays;

import static core.helper.StringUtils.$x;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NoArgsConstructor
public class ObjectsLayer extends AbstractLayerS3<ObjectsLayer> {

    private Table objectList;
    private final Integer delIdx = 5;
    private final String fObjectColumn = "Название";
    private final Integer checkIdx = 0;
    private SelenideElement deleteAllBucketObjects = $x("//*[text()='Удалить']");

    public ObjectsLayer(String name)
    {
        super(name);
    }

    @Step("Открытие модального окна выбора объекта для загрузки")
    public ObjectsModal clickUploadObject(){
        Button.byText("Загрузить объект").click();
        return new ObjectsModal();
    }

    @Step("Удаление объекта с именем '{name}'")
    public ObjectsLayer deleteObject(String name){
        objectList = new DataTable(fObjectColumn);

        Menu.byElement(objectList.getRowByColumnValue(fObjectColumn, name)
                .getElementByColumnIndex(delIdx)
                .$x(".//button")).select("Удалить");

        Dialog.byTitle("Удалить объекты?").clickButton("Удалить");
        Alert.green("Объект успешно удален");
        return this;
    }

    @Step("Удаление нескольких объектов'")
    public ObjectsLayer deleteObjects(String... names){
        objectList = new Table($x("(.//table[@class=\"MuiTable-root\"])[2]"));

        Arrays.stream(names)
                .forEach(name -> new CheckBox(objectList.getRowByColumnValue(fObjectColumn, name)
                        .getElementByColumn(fObjectColumn)
                        .$x(".//preceding-sibling::td/label"))
                        .setChecked(true));

        deleteAllBucketObjects.shouldBe(Condition.visible.because("Кнопка удалить все объекты должна отображаться"))
                        .click();
        Dialog.byTitle("Удалить объекты?").clickButton("Удалить");
        Alert.green("Объекты успешно удалены");
        return this;
    }

    @Step("Получение ссылки на объект '{name}'")
    public ObjectsLayer getObjectLink(String name){
        objectList = new DataTable(fObjectColumn);

        Menu.byElement(objectList.getRowByColumnValue(fObjectColumn, name)
                .getElementByColumnIndex(delIdx)
                .$x(".//button")).select("Получить ссылку");

        Alert.green("Ссылка на объект успешно скопирована");
        return this;
    }

    @Step("Изменение имени объекта '{name}' на '{newName}'")
    public ObjectsLayer updateObjectName(String name, String newName){
        objectList = new DataTable(fObjectColumn);

        Menu.byElement(objectList.getRowByColumnValue(fObjectColumn, name)
                .getElementByColumnIndex(delIdx)
                .$x(".//button")).select("Переименовать");

        Dialog.byTitle("Переименование объекта")
                .setInputValue("Новое название", newName)
                .clickButton("Сохранить");

        Alert.green("Объект успешно обновлен");
        return this;
    }

    @Step("Проверка наличия объекта '{objectName}' в списке - '{isExists}'")
    public ObjectsLayer checkObjectExists(String objectName, Boolean isExists){
        objectList = new DataTable(fObjectColumn);
        if (isExists)
            assertTrue(objectList.isColumnValueEquals(fObjectColumn, objectName));
        else
            assertFalse(objectList.isColumnValueEquals(fObjectColumn, objectName));
        return this;
    }

    @Step("Восстановление объекта '{objectName}'")
    public ObjectsLayer restoreObject(String objectName){
        objectList = new DataTable(fObjectColumn);

        objectList.getRowByColumnValue(fObjectColumn, objectName)
                .getElementByColumnIndex(delIdx)
                .$x("(.//button)[1]").click();

        Alert.green("Объект успешно восстановлен");
        return this;
    }

    @Step("Открытие доступа к объекту '{objectName}'")
    public ObjectsLayer openObjectAccess(String objectName){
        objectList = new DataTable(fObjectColumn);

        Menu.byElement(objectList.getRowByColumnValue(fObjectColumn, objectName)
                .getElementByColumnIndex(delIdx)
                .$x(".//button")).select("Открыть доступ");

        Dialog.byTitle("Открыть публичный доступ")
                .clickButton("Открыть публичный доступ");

        Alert.green("Публичный доступ успешно открыт");
        return this;
    }

    @Step("Установка режима отображения удалённых объектов")
    public ObjectsLayer showHideDeleted(Boolean isVisible){
        Switch.byText("Показать удаленные").setEnabled(isVisible);
        return this;
    }

    @Step("Открытие доступа к объекту '{objectName}'")
    public ObjectsLayer downloadObject(String objectName){
        objectList = new DataTable(fObjectColumn);

        Menu.byElement(objectList.getRowByColumnValue(fObjectColumn, objectName)
                .getElementByColumnIndex(delIdx)
                .$x(".//button")).select("Скачать");
        Alert.green("Объект успешно скачан");
        return this;
    }
}
