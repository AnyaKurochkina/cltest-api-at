package ui.uiSteps;

import io.qameta.allure.Step;
import ui.pages.DataCenterPage;
import ui.pages.OrganizationPage;
import ui.pages.VmWareDataCentreCreationPage;

import static core.enums.DataCentreStatus.*;

public class DataCenterSteps {

    @Step("Создание Дата центра с именем - {nameOfDataCenter}, в организации - {fullOrgName}")
    public String createDataCenter(String fullOrgName, String nameOfDataCenter){
        OrganizationPage organizationPage = new OrganizationPage();
        //Создаем виртуальный дата центр внутри нашей организации
        organizationPage.createVirtualDataCentre();
        //Заполняем поля при создании дата центра
        VmWareDataCentreCreationPage vmWareDataCentreCreationPage = new VmWareDataCentreCreationPage();
        vmWareDataCentreCreationPage
                .setNameDataCentre(nameOfDataCenter)
                .setIpV4Value(10)
                .setCpuValue(14)
                .setWidthValue(15)
                .setRamValue(35)
                .confirmOrder();
        organizationPage.checkLoader();
        organizationPage.checkPage(fullOrgName);
        //Проверяем статус созданного дата центра
        organizationPage.checkStatusTransitionOfDataCenter(nameOfDataCenter, PROCESSING, READY);
        return nameOfDataCenter;
    }

    @Step("Удаление Дата центра с именем - {nameOfDataCentre}")
    public void deleteDataCenter(String nameOfDataCentre){
        DataCenterPage dataCenterPage = new DataCenterPage(nameOfDataCentre);
        //Убираем защиту от удаления и удаляем дата центр
        dataCenterPage.removeDeletionProtection();
        dataCenterPage.deleteDataCenter();
        //Возвращаемся к организации
        dataCenterPage.backToVmWareOrganization();
        OrganizationPage organizationPage = new OrganizationPage();
        //Проверяем статус дата центра
        organizationPage.checkStatusTransitionOfDataCenter(nameOfDataCentre, DELETING, DELETED);
    }

    @Step("Переход в Дата центр")
    public void stepInDataCenter(String nameOfDataCentre){
        OrganizationPage organizationPage = new OrganizationPage();
        organizationPage.stepInDataCenter(nameOfDataCentre);
        new DataCenterPage(nameOfDataCentre);
    }
}
