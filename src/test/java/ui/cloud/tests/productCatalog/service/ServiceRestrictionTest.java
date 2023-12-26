package ui.cloud.tests.productCatalog.service;

import api.Tests;
import core.enums.Role;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.productCatalog.service.Service;
import org.json.JSONObject;
import org.junit.DisabledIfEnv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.testit.annotations.Title;
import ui.cloud.pages.CloudLoginPage;
import ui.cloud.pages.IndexPage;
import ui.cloud.pages.services.ServicesListPage;
import ui.extesions.ConfigExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ServiceSteps.createService;
import static steps.productCatalog.ServiceSteps.partialUpdateServiceByName;
import static ui.elements.TypifiedElement.refreshPage;

@Feature("Ограничения сервисов")
@DisabledIfEnv("prod")
@ExtendWith(ConfigExtension.class)
public class ServiceRestrictionTest extends Tests {

    Project project = Project.builder().isForOrders(true).projectEnvironmentPrefix(ProjectEnvironmentPrefix.byType("DEV"))
            .build().createObject();

    @BeforeEach
    @Title("Авторизация на портале")
    public void beforeEach() {
        new CloudLoginPage(project.getId())
                .signIn(Role.DAY2_SERVICE_MANAGER);
    }

    @Test
    @TmsLink("SOUL-5765")
    @DisplayName("Публикация сервиса")
    void viewPublishedService() {
        String name = "at_ui_view_published_service";
        Service service = createService(name, name);
        new IndexPage().goToServicesListPage();
        ServicesListPage page = new ServicesListPage();
        assertFalse(page.isProductDisplayed(service.getTitle()));
        partialUpdateServiceByName(name, new JSONObject().put("is_published", "true"));
        refreshPage();
        assertTrue(page.isProductDisplayed(service.getTitle()));
        partialUpdateServiceByName(name, new JSONObject().put("is_published", "false"));
    }
}
