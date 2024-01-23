package ui.cloud.tests.productCatalog.service;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.service.Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

import java.util.UUID;

import static steps.productCatalog.ServiceSteps.deleteServiceByName;
import static ui.cloud.pages.productCatalog.EntityPage.CALCULATED_VERSION_TITLE;

@Feature("Создание сервиса")
public class CreateServiceTest extends ServiceBaseTest {

    @Test
    @TmsLink("972716")
    @DisplayName("Создание сервиса без графа")
    public void createServiceTest() {
        checkServiceNameValidation();
        createServiceWithoutRequiredParameters();
        createServiceWithoutGraph();
        createServiceWithNonUniqueName();
    }

    @Step("Создание сервиса без заполнения обязательных полей")
    public void createServiceWithoutRequiredParameters() {
        new ControlPanelIndexPage().goToServicesListPagePC()
                .checkCreateServiceDisabled(Service.builder().name("").title(TITLE).build())
                .checkCreateServiceDisabled(Service.builder().name(NAME).title("").build());
    }

    @Step("Создание сервиса с неуникальным кодом")
    public void createServiceWithNonUniqueName() {
        new ControlPanelIndexPage().goToServicesListPagePC()
                .checkNonUniqueNameValidation(Service.builder().name(NAME).title(TITLE).build());
    }

    @Step("Создание сервиса с недопустимым кодом")
    public void checkServiceNameValidation() {
        new ControlPanelIndexPage().goToServicesListPagePC()
                .checkNameValidation(new String[]{"Test_name", "test name", "тест", "test_name$"});
    }

    @Step("Создание сервиса без графа")
    public void createServiceWithoutGraph() {
        service.setGraphId(null);
        service.setName(UUID.randomUUID().toString());
        new ControlPanelIndexPage().goToServicesListPagePC()
                .createService(service)
                .checkAttributes(service);
        deleteServiceByName(service.getName());
    }

    @Test
    @TmsLink("504746")
    @DisplayName("Создание сервиса c графом")
    public void createServiceWithGraphTest() {
        service.setName(UUID.randomUUID().toString());
        service.setGraphVersion(CALCULATED_VERSION_TITLE);
        new ControlPanelIndexPage().goToServicesListPagePC()
                .createService(service)
                .checkAttributes(service);
        deleteServiceByName(service.getName());
    }
}
