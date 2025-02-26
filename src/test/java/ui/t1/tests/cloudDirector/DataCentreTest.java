package ui.t1.tests.cloudDirector;

import core.utils.Waiting;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.cloud.pages.CompareType;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.TypifiedElement;
import ui.extesions.InterceptTestExtension;
import ui.models.cloudDirector.StorageProfile;
import ui.models.cloudDirector.Vdc;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudDirector.DataCentrePage;
import ui.t1.pages.cloudDirector.VMwareOrganizationPage;

import static ui.t1.pages.cloudDirector.DataCentrePage.INFO_DATA_CENTRE;

@BlockTests
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("VMWare организация. Виртуальный дата-центр.")

public class DataCentreTest extends AbstractCloudDirectorTest {

    @Test
    @Order(1)
    @DisplayName("VMware. Создание VDC.")
    public void createDataCentre() {
        new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .addDataCentre(testVdc)
                .waitChangeStatus()
                .selectDataCentre(dataCentreName)
                .checkCreate(false)
                .goToVMwareOrgPage()
                .checkDataCentreExist(dataCentreName);
        new VMwareOrganizationPage()
                .selectDataCentre(testVdc.getName())
                .checkVdcParams(testVdc);
    }

    @Test
    @Order(2)
    @TmsLink("147532")
    @DisplayName("VMware. Создание второго VDC  в одной организации")
    public void createSecondDataCentreTest() {
        String secondDataCentreName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "-at-ui";
        Vdc secondVdc = new Vdc(secondDataCentreName, "2", "4", new StorageProfile("High", "10"), "200");
        new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .addDataCentre(secondVdc)
                .waitChangeStatus()
                .selectDataCentre(secondDataCentreName)
                .checkCreate(false)
                .goToVMwareOrgPage()
                .checkDataCentreExist(secondDataCentreName);
        DataCentrePage dataCentrePage = new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .selectDataCentre(secondDataCentreName);
        dataCentrePage.runActionWithCheckCost(CompareType.ZERO, dataCentrePage::delete);
    }

    @Test
    @Order(4)
    @TmsLink("570222")
    @DisplayName("VMware. Проверка уникальности имени VDC")
    public void createDataCentreWithSameNameTest() {
        new IndexPage()
                .goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .addDataCentreWithExistName(dataCentreName);
    }

    @Test
    @Order(5)
    @TmsLinks({@TmsLink("158901"), @TmsLink("767870")})
    @DisplayName("VMware. Зарезервировать/отозвать внешние IP адреса")
    public void reserveExternalIPAddressesTest() {
        DataCentrePage dataCentrePage = new IndexPage()
                .goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .selectDataCentre(dataCentreName);
        dataCentrePage.runActionWithCheckCost(CompareType.MORE, () -> dataCentrePage.addIpAddresses(2));
        dataCentrePage.runActionWithCheckCost(CompareType.LESS, dataCentrePage::removeIpAddresses);
    }

    @Test
    @Order(6)
    @TmsLink("559036")
    @DisplayName("VMware. Изменение конфигурации VDC Allocation Pool")
    public void changeConfigVDCAllocationPoolTest() {
        DataCentrePage dataCentrePage = new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .selectDataCentre(dataCentreName);
        dataCentrePage.runActionWithCheckCost(CompareType.MORE, () -> dataCentrePage.changeConfig(4, 16));
    }

    @Test
    @Order(7)
    @TmsLinks({@TmsLink("692723"), @TmsLink("692724")})
    @DisplayName("VMware. Управление дисковой подсистемой VDC. Добавление/Удаление профиля")
    public void addProfileTest() {
        StorageProfile profile = new StorageProfile("SP-High", "11");
        DataCentrePage dataCentrePage = new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .selectDataCentre(dataCentreName);
        dataCentrePage.runActionWithCheckCost(CompareType.MORE, () -> dataCentrePage.addProfile(profile));
        dataCentrePage.runActionWithCheckCost(CompareType.LESS, () -> dataCentrePage.deleteProfile(profile));
    }

    @Test
    @Order(8)
    @TmsLink("1115943")
    @DisplayName("VMware. VDC. Изменение конфигурации маршрутизатора")
    public void changeRouterConfigTest() {
        DataCentrePage dataCentrePage = new IndexPage()
                .goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .selectDataCentre(dataCentreName);
        dataCentrePage.runActionWithCheckCost(CompareType.MORE, () -> dataCentrePage.changeRouterConfig("500", "Compact"));
    }

    @Test
    @Order(99)
    @TmsLink("559157")
    @DisplayName("VMware. Защита от удаления VDC")
    public void protectedDataCentreFromDelete() {
        DataCentrePage dataCentrePage = new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .selectDataCentre(dataCentreName);
        Waiting.sleep(10000);
        dataCentrePage.switchProtectOrder(true);
        try {
            new DataCentrePage().runActionWithParameters(INFO_DATA_CENTRE, "Удалить VDC", "Удалить", () -> {
                Dialog dlgActions = Dialog.byTitle("Удаление");
                dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
            }, ActionParameters.builder().checkLastAction(false).checkPreBilling(false).checkAlert(false).waitChangeStatus(false).build());
            Alert.red("Заказ защищен от удаления");
            TypifiedElement.refreshPage();
        } finally {
            Waiting.sleep(10000);
            dataCentrePage.switchProtectOrder(false);
        }
    }

    @Test
    @Order(100)
    @TmsLinks({@TmsLink("158903"), @TmsLink("158904")})
    @DisplayName("VMware. Удаление VDC и Тоггл \"Показывать удаленные\"")
    public void deleteDataCentre() {
        DataCentrePage dataCentrePage = new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .selectDataCentre(dataCentreName);
        dataCentrePage.runActionWithCheckCost(CompareType.ZERO, dataCentrePage::delete);
        dataCentrePage
                .goToVMwareOrgPage()
                .checkDataCentreNotExist(dataCentreName);

        new VMwareOrganizationPage()
                .showDeletedDataCentres(true)
                .checkDataCentreExist(dataCentreName);
    }
}
