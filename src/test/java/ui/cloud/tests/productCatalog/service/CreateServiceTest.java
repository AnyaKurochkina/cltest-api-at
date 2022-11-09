package ui.cloud.tests.productCatalog.service;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.IndexPage;

import java.util.UUID;

@Feature("Создание сервиса")
public class CreateServiceTest extends ServiceBaseTest {

    @Test
    @TmsLink("972716")
    @DisplayName("Создание сервиса без графа")
    public void createServiceTest() {
        checkServiceNameValidation();
        createServiceWithoutRequiredParameters();
        createService();
        createServiceWithNonUniqueName();
    }

    @Step("Создание сервиса без заполнения обязательных полей")
    public void createServiceWithoutRequiredParameters() {
        new IndexPage().goToServicesListPagePC()
                .checkCreateServiceDisabled(Service.builder().serviceName("").title(TITLE).build())
                .checkCreateServiceDisabled(Service.builder().serviceName(NAME).title("").build());
    }

    @Step("Создание сервиса с неуникальным кодом")
    public void createServiceWithNonUniqueName() {
        new IndexPage().goToServicesListPagePC()
                .checkNonUniqueNameValidation(Service.builder().serviceName(NAME).title(TITLE).build());
    }

    @Step("Создание сервиса с недопустимым кодом")
    public void checkServiceNameValidation() {
        new IndexPage().goToServicesListPagePC()
                .checkNameValidation(new String[]{"Test_name", "test name", "тест", "test_name$"});
    }

    @Step("Создание сервиса")
    public void createService() {
        service.setServiceName(UUID.randomUUID().toString());
        new IndexPage().goToServicesListPagePC()
                .createService(service)
                .checkAttributes(service);
        deleteService(service.getServiceName());
    }
}
