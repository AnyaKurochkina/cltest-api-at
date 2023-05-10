package ui.t1.pages.S3Storage.LifeCycle;

import io.qameta.allure.Step;
import ui.elements.Button;
import ui.elements.DataTable;
import ui.t1.pages.S3Storage.AbstractLayerS3;

public class LifeCycleLayer extends AbstractLayerS3<LifeCycleLayer> {

    private DataTable lifeCycleList;

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


}
