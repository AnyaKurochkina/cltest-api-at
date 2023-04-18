package ui.t1.tests.cloudDirector;

import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.cloud.pages.CompareType;
import ui.extesions.InterceptTestExtension;
import ui.models.StorageProfile;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudDirector.DataCentrePage;
import ui.t1.pages.cloudDirector.VMwareOrganizationPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@BlockTests
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Feature("VMWare организация. Виртуальный дата-центр.")

public class DataCentreTest extends AbstractCloudDirectorTest {

    @Test
    @Order(1)
    @TmsLink("147532")
    @DisplayName("VMware. Создание VDC. Allocation Pool")
    public void createDataCentre() {
        new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .addDataCentre(dataCentreName)
                .waitChangeStatus()
                .selectDataCentre(dataCentreName)
                .checkCreate();
    }

    @Test
    @Order(2)
    @TmsLink("570222")
    @DisplayName("VMware. Проверка уникальности имени VDC")
    public void createDataCentreWithSameNameTest() {
        new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .addDataCentreWithExistName(dataCentreName);
    }

    @Test
    @Order(3)
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
    @Order(5)
    @TmsLink("559036")
    @DisplayName("VMware. Изменение конфигурации VDC Allocation Pool")
    public void changeConfigVDCAllocationPoolTest() {
        DataCentrePage dataCentrePage = new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .selectDataCentre(dataCentreName);
        dataCentrePage.runActionWithCheckCost(CompareType.MORE, () -> dataCentrePage.changeConfig(4, 16));
    }

    @Test
    @Order(6)
    @TmsLink("692723")
    @DisplayName("VMware. Управление дисковой подсистемой VDC. Добавление профиля")
    public void addProfileTest() {
        StorageProfile profile = new StorageProfile("SP-High", "11");
        DataCentrePage dataCentrePage = new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .selectDataCentre(dataCentreName);
        dataCentrePage.runActionWithCheckCost(CompareType.MORE, () -> dataCentrePage.addProfile(profile));
    }

    @Test
    @Order(100)
    @TmsLinks({@TmsLink("158903"), @TmsLink("158904")})
    @DisplayName("VMware. Удаление VDC")
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
