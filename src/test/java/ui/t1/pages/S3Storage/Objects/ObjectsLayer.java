package ui.t1.pages.S3Storage.Objects;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.elements.*;
import ui.t1.pages.S3Storage.AbstractLayerS3;

import java.util.ArrayList;
import java.util.List;

import static core.helper.StringUtils.$x;

public class ObjectsLayer extends AbstractLayerS3<ObjectsLayer> {

    private Table objectList;
    private final Integer delIdx = 5;
    private final String fObjectColumn = "Название";
    private final Integer checkIdx = 0;

    public ObjectsLayer(String name)
    {
        super(name);
    }

    public ObjectsLayer()
    {

    }

    @Step("Открытие модального окна выбора объекта для загрузки")
    public ObjectsModal uploadObject(){
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
        Alert.green("Объект успешно удалён");
        return this;
    }

    @Step("Удаление нескольких объектов'")
    public ObjectsLayer deleteObjects(String... names){
        //TODO:
        //Обойти проблему с изменением количество хежеров в таблице
        objectList = new Table($x("(.//table[@class=\"MuiTable-root\"])[2]"));
//        objectList = new DataTable("Название");

        List<SelenideElement> rowsToCheck = new ArrayList<SelenideElement>();

        for(String name:names)
        {
            rowsToCheck.add(
                    objectList.getRowByColumnValue(fObjectColumn, name)
                            .getElementByColumn(fObjectColumn).$x(".//input"));
        }

        for(SelenideElement row:rowsToCheck) {
            CheckBox r = new CheckBox(row.parent());
            r.setChecked(true);
        }

//        for(String name:names) {
//            objectList.getRowByColumnValue("Название", name)
//                    .getElementByColumn("Название").click();
//        }
        Button.byText("Удалить").click();
        Dialog.byTitle("Удалить объекты?").clickButton("Удалить");
//        Alert.green("Объект успешно удалён");
        return this;
    }

    @Step("Удаление объекта с именем '{name}'")
    public ObjectsLayer getObjectLink(String name){
        objectList = new DataTable(fObjectColumn);

        Menu.byElement(objectList.getRowByColumnValue(fObjectColumn, name)
                .getElementByColumnIndex(delIdx)
                .$x(".//button")).select("Получить ссылку");

        Alert.green("Ссылка на объект успешно скопирована");
        return this;
    }
}
