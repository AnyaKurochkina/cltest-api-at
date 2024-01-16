package ui.cloud.tests.services;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import lombok.extern.log4j.Log4j2;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.productCatalog.service.Service;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.services.ServicesListPage;
import ui.cloud.pages.services.SmokeLinearTestPage;
import ui.cloud.pages.services.SmokeTestPage;
import ui.extesions.ConfigExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ServiceSteps.createService;
import static steps.productCatalog.ServiceSteps.partialUpdateServiceByName;
import static ui.elements.TypifiedElement.refreshPage;

@ExtendWith(ConfigExtension.class)
@Epic("Сервисы")
@Feature("Test services")
@Tags({@Tag("ui_services")})
@Log4j2
public class ServicesTest extends Tests {
    Project project = Project.builder().isForOrders(true).projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("DEV")).build().createObject();

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new CloudLoginPage(project.getId())
                .signIn(Role.DAY2_SERVICE_MANAGER);
    }

    @Test
    @TmsLink("706965")
    @DisplayName("Сервисы. Проверка работы компонентов графов. (Параллельная)")
    void runSmokeTest() {
        SmokeTestPage smokeTestPage = new SmokeTestPage();
        new IndexPage().goToServicesListPage().selectProduct(smokeTestPage.getServiceName());
        smokeTestPage.run();
        smokeTestPage.checkGraph();
    }

    @Test
    @TmsLink("842450")
    @DisplayName("Сервисы. Проверка работы компонентов графов. (Последовательная)")
    void runSmokeTestLinear() {
        SmokeLinearTestPage smokeLinearTestPage = new SmokeLinearTestPage();
        new IndexPage().goToServicesListPage().selectProduct(smokeLinearTestPage.getServiceName());
        smokeLinearTestPage.run();
        smokeLinearTestPage.checkGraph();
    }
}
