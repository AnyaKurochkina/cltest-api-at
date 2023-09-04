package ui.t1.pages.S3Storage.CORS;

import io.qameta.allure.Step;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.*;
import ui.t1.pages.S3Storage.AbstractLayerS3;
import ui.t1.pages.S3Storage.AccessRules.AccessRulesLayer;
import ui.t1.pages.cloudDirector.VMwareOrganizationPage;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CORSLayer extends AbstractLayerS3<CORSLayer> {

    private DataTable CORSList;
    private final Integer menuIdx = 1;
    private final String fCORSName = "Origins";

    public CORSLayer(String name)
    {
        super(name);
    }

    public CORSLayer()
    {

    }

    @Step("Открытие модального окна добавления настроек CORS")
    public CORSModal addCORS(){
        TestUtils.wait(2000);
        Button.byText("Добавить").click();
        return new CORSModal();
    }

    @Step("Удаление бакета со значением Origins '{origins}'")
    public CORSLayer deleteCORS(String origins){
        CORSList = new DataTable(fCORSName);

        Menu.byElement(CORSList.getRowByColumnValue(fCORSName, origins)
                                .getElementByColumnIndex(menuIdx)
                                .$x(".//button")).select("Удалить");

        Dialog.byTitle("Удалить правило доступа").clickButton("Удалить");
        return new CORSLayer("CORS");
    }

    @Step("Редактирование бакета со значением Origins '{origins}'")
    public CORSModal editCORS(String origins){
        CORSList = new DataTable(fCORSName);

        Menu.byElement(CORSList.getRowByColumnValue(fCORSName, origins)
                .getElementByColumnIndex(menuIdx)
                .$x(".//button")).select("Редактировать");

        return new CORSModal();
    }

    @Step("Проверка наличия CORS '{CORSName}' в списке - '{isExists}'")
    public CORSLayer checkCORSExists(String CORSName, Boolean isExists){
        CORSList = new DataTable(fCORSName);
        if (isExists)
            assertTrue(CORSList.isColumnValueEquals(fCORSName, CORSName));
        else
            assertFalse(CORSList.isColumnValueEquals(fCORSName, CORSName));
        return this;
    }
}
