package ui.t1.tests.cloudDirector;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import lombok.extern.log4j.Log4j2;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.cloud.pages.CompareType;
import ui.extesions.InterceptTestExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudDirector.DataCentrePage;
import ui.t1.pages.cloudDirector.VMwareOrganizationPage;

@BlockTests
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Epic("Cloud Director")
@Feature("VMWare организация. Пользователи.")
@Tags({@Tag("ui_cloud_director")})
@Log4j2
public class VdcWithOutEdgeActionTest extends AbstractCloudDirectorTest {

    @Test
    @TmsLink("SOUL-8390")
    @Order(1)
    @DisplayName("VMware. Создание VDC без Edge.")
    public void createDataCentreWithOutEdge() {
        testVdc.setRootRouterBandwidth(null);

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
    @TmsLinks({@TmsLink("SOUL-8391"), @TmsLink("SOUL-8393")})
    @DisplayName("VMware. VDC. Создать/Удалить маршрутизатор (Edge).")
    public void createEdge() {
        DataCentrePage dataCentrePage = new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .selectDataCentre(dataCentreName);
        dataCentrePage.runActionWithCheckCost(CompareType.MORE, () -> dataCentrePage.addEdge("500"));
        dataCentrePage.runActionWithCheckCost(CompareType.LESS, dataCentrePage::deleteEdge);
    }

    @Test
    @Order(4)
    @DisplayName("VMware. Удалить Vdc")
    public void deleteDataCentre() {
        DataCentrePage dataCentrePage = new IndexPage().goToCloudDirector()
                .goToOrganization(vmWareOrganization.getName())
                .selectDataCentre(dataCentreName);
        dataCentrePage.runActionWithCheckCost(CompareType.ZERO, dataCentrePage::delete);
    }
}
