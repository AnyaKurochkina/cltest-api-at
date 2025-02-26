package ui.cloud.tests.productCatalog.orderTemplate;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.cloud.productCatalog.visualTeamplate.ItemVisualTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ui.cloud.pages.ControlPanelIndexPage;

import java.util.UUID;

@Feature("Создание шаблона отображения")
public class CreateOrderTemplateTest extends OrderTemplateBaseTest {

    @Test
    @TmsLink("646676")
    @DisplayName("Создание шаблона отображения")
    public void createOrderTemplateTest() {
        checkTemplateNameValidation();
        createTemplateWithoutRequiredParameters();
        createTemplate();
        createTemplateWithNonUniqueName();
    }

    @Step("Создание шаблона без заполнения обязательных полей")
    public void createTemplateWithoutRequiredParameters() {
        new ControlPanelIndexPage().goToOrderTemplatesPage()
                .checkCreateTemplateDisabled(ItemVisualTemplate.builder().name("").title(TITLE).build())
                .checkCreateTemplateDisabled(ItemVisualTemplate.builder().name(NAME).title("").build());
    }

    @Step("Создание шаблона с неуникальным кодом")
    public void createTemplateWithNonUniqueName() {
        new ControlPanelIndexPage().goToOrderTemplatesPage()
                .checkNonUniqueNameValidation(ItemVisualTemplate.builder().name(NAME).title(TITLE).build());
    }

    @Step("Создание шаблона с недопустимым кодом")
    public void checkTemplateNameValidation() {
        new ControlPanelIndexPage().goToOrderTemplatesPage()
                .checkNameValidation(new String[]{"Test_name", "test name", "тест", "test_name$"});
    }

    @Step("Создание шаблона отображения")
    public void createTemplate() {
        orderTemplate.setName(UUID.randomUUID().toString());
        new ControlPanelIndexPage().goToOrderTemplatesPage()
                .createOrderTemplate(orderTemplate)
                .checkAttributes(orderTemplate);
        deleteOrderTemplate(orderTemplate.getName());
    }
}
