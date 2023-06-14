package ui.t1.tests.cloudDirector;

import core.helper.Configure;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.CompareType;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.TypifiedElement;
import ui.models.StorageProfile;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudDirector.DataCentrePage;
import ui.t1.pages.cloudDirector.VMwareOrganizationPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ui.t1.pages.cloudDirector.DataCentrePage.INFO_DATA_CENTRE;

//@BlockTests
//@ExtendWith(InterceptTestExtension.class)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("VMWare организация. Виртуальный дата-центр.")

public class DataCentreTest extends AbstractCloudDirectorTest {

    @Test
  //  @Order(1)
    @TmsLink("147532")
    @DisplayName("VMware. Создание второго VDC  в одной организации")
    public void createDataCentre() {
        assertTrue(new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .addDataCentre(dataCentreName)
                .waitChangeStatus()
                .selectDataCentre(dataCentreName)
                .checkCreate()
                .goToVMwareOrgPage()
                .isDataCentreExist(dataCentreName));

        String secondDataCentreName = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "-at-ui";
        assertTrue(new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .addDataCentre(secondDataCentreName)
                .waitChangeStatus()
                .selectDataCentre(secondDataCentreName)
                .checkCreate()
                .goToVMwareOrgPage()
                .isDataCentreExist(secondDataCentreName));
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
        new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .addDataCentreWithExistName(dataCentreName);
    }

    @Test
    @Order(5)
    @TmsLinks({@TmsLink("158901"), @TmsLink("767870")})
    @DisplayName("VMware. Зарезервировать/отозвать внешние IP адреса")
    public void reserveExternalIPAddressesTest() {
        DataCentrePage dataCentrePage = new IndexPage().goToCloudDirector()
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
        String name = "";
        if (Configure.ENV.equals("t1prod")) {
            name = "Veeam-InstantVMRecovery";
        } else {
            name = "SP-Standart";
        }
        StorageProfile profile = new StorageProfile(name, "11");
        DataCentrePage dataCentrePage = new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .selectDataCentre(dataCentreName);
        dataCentrePage.runActionWithCheckCost(CompareType.MORE, () -> dataCentrePage.addProfile(profile));
        dataCentrePage.runActionWithCheckCost(CompareType.LESS, () -> dataCentrePage.deleteProfile(profile));
    }

    @Test
  //  @Order(8)
    @TmsLink("1115943")
    @DisplayName("VMware. VDC. Изменение конфигурации маршрутизатора")
    public void changeRouterConfigTest() {
//        DataCentrePage dataCentrePage = new IndexPage()
//                .goToCloudDirector()
//                .goToOrganization(vmWareOrganization.getName())
//                .selectDataCentre(dataCentreName);
        DataCentrePage dataCentrePage = new IndexPage()
                .goToCloudDirector()
                .goToOrganization("ift-for-tests")
                .selectDataCentre("test_data");
        dataCentrePage.runActionWithCheckCost(CompareType.MORE, () -> dataCentrePage.changeRouterConfig("500", "Large"));
    }

    @Test
    @Order(99)
    @TmsLink("559157")
    @DisplayName("VMware. Защита от удаления VDC")
    public void protectedDataCentreFromDelete() {
        DataCentrePage dataCentrePage = new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .selectDataCentre(dataCentreName);
        dataCentrePage.switchProtectOrder(true);
        try {
            new DataCentrePage().runActionWithParameters(INFO_DATA_CENTRE, "Удалить", "Удалить", () -> {
                Dialog dlgActions = Dialog.byTitle("Удаление");
                dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
            }, ActionParameters.builder().checkLastAction(false).checkPreBilling(false).checkAlert(false).waitChangeStatus(false).build());
            Alert.red("Заказ защищен от удаления");
            TypifiedElement.refresh();
        } finally {
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
        assertFalse(dataCentrePage
                .goToVMwareOrgPage()
                .isDataCentreExist(dataCentreName));
        assertTrue(new VMwareOrganizationPage()
                .showDeletedDataCentres(true)
                .isDataCentreExist(dataCentreName));
    }
}
