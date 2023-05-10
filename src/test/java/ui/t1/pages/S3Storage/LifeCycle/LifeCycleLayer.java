package ui.t1.pages.S3Storage.LifeCycle;

import io.qameta.allure.Step;
import ui.elements.*;
import ui.t1.pages.S3Storage.AbstractLayerS3;
import ui.t1.pages.S3Storage.Objects.ObjectsLayer;

public class LifeCycleLayer extends AbstractLayerS3<LifeCycleLayer> {

    private DataTable lifeCycleList;
    private final Integer menuIdx = 2;

    public LifeCycleLayer(String name)
    {
        super(name);
    }

    public LifeCycleLayer()
    {

    }

    @Step("Открытие содального окна жизненного цикла")
    public LifeCycleModal addLifeCycle()
    {
        Button.byText("Добавить").click();
        return new LifeCycleModal();
    }

    @Step("Удаление ЖЦ с именем '{name}'")
    public LifeCycleLayer deleteLifeCycle(String name){
        lifeCycleList = new DataTable("Название");

        Menu.byElement(lifeCycleList.getRowByColumnValue("Название", name)
                .getElementByColumnIndex(menuIdx)
                .$x(".//button")).select("Удалить");

        Dialog.byTitle("Удалить правило доступа").clickButton("Удалить");
        Alert.green("Правило жизненного цикла успешно удалено");
        return this;
    }

    @Step("Редактирование ЖЦ с именем '{name}'")
    public LifeCycleModal editLifeCycle(String name){
        lifeCycleList = new DataTable("Название");

        Menu.byElement(lifeCycleList.getRowByColumnValue("Название", name)
                .getElementByColumnIndex(menuIdx)
                .$x(".//button")).select("Редактировать");
        return new LifeCycleModal();
    }


}
