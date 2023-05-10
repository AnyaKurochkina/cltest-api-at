package ui.t1.pages.S3Storage.CORS;

import io.qameta.allure.Step;
import ui.elements.*;
import ui.t1.pages.S3Storage.AbstractLayerS3;
import ui.t1.pages.cloudDirector.VMwareOrganizationPage;

import static com.codeborne.selenide.Selenide.$x;

public class CORSLayer extends AbstractLayerS3<CORSLayer> {

    private DataTable CORSList;
    private final Integer menuIdx = 1;

    public CORSLayer(String name)
    {
        super(name);
    }

    public CORSLayer()
    {

    }

    @Step("Открытие модального окна добавления настроек CORS")
    public CORSModal addCORS(){
        Button.byText("Добавить").click();
        return new CORSModal();
    }

    @Step("Удаление бакета со значением Origins '{origins}'")
    public CORSLayer deleteCORS(String origins){
        CORSList = new DataTable("Origins");

        Menu.byElement(CORSList.getRowByColumnValue("Origins", origins)
                                .getElementByColumnIndex(menuIdx)
                                .$x(".//button")).select("Удалить");

        Dialog.byTitle("Удалить правило доступа").clickButton("Удалить");
        return new CORSLayer("CORS");
    }

    @Step("Редактирование бакета со значением Origins '{origins}'")
    public CORSModal editCORS(String origins){
        CORSList = new DataTable("Origins");

        Menu.byElement(CORSList.getRowByColumnValue("Origins", origins)
                .getElementByColumnIndex(menuIdx)
                .$x(".//button")).select("Редактировать");

        return new CORSModal();
    }


}
