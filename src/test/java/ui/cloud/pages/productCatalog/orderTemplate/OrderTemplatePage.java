package ui.cloud.pages.productCatalog.orderTemplate;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.productCatalog.visualTeamplate.ItemVisualTemplate;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.BasePage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.*;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTemplatePage extends BasePage {

    private final SelenideElement previewButton = $x("//button[.//span[text()='Просмотр']]");
    private final Input titleInput = Input.byName("title");
    private final Input nameInput = Input.byName("name");
    private final Input descriptionInput = Input.byName("description");
    private final MultiSelect typeSelect = MultiSelect.byLabel("Тип");
    private final MultiSelect providerSelect = MultiSelect.byLabel("Провайдер");
    private final SelenideElement orderTemplatesLink = $x("//a[text()='Шаблоны отображения' and not(@href)]");
    private final Switch isEnabled = Switch.byInputName("is_active");

    public OrderTemplatePage() {
        previewButton.shouldBe(Condition.visible);
    }

    @Step("Проверка атрибутов шаблона '{template.name}'")
    public OrderTemplatePage checkAttributes(ItemVisualTemplate template) {
        nameInput.getInput().shouldHave(Condition.exactValue(template.getName()));
        titleInput.getInput().shouldHave(Condition.exactValue(template.getTitle()));
        descriptionInput.getInput().shouldHave(Condition.exactValue(template.getDescription()));
        Assertions.assertEquals(template.getEventType().get(0), typeSelect.getValue());
        Assertions.assertEquals(template.getEventProvider().get(0), providerSelect.getValue());
        return this;
    }

    @Step("Удаление шаблона")
    public void deleteTemplate() {
        deleteButton.click();
        new DeleteDialog().submitAndDelete("Шаблон удален");
    }

    @Step("Задание значения в поле 'Описание'")
    public OrderTemplatePage setDescription(String value) {
        descriptionInput.setValue(value);
        return this;
    }

    @Step("Назад в браузере и отмена в баннере о несохранённых изменениях")
    public OrderTemplatePage backAndDismissAlert() {
        back();
        dismissBrowserAlert();
        return this;
    }

    @Step("Возврат в список шаблонов и отмена в баннере о несохранённых изменениях")
    public OrderTemplatePage goToTemplatesListAndDismissAlert() {
        TestUtils.scrollToTheTop();
        orderTemplatesLink.click();
        dismissBrowserAlert();
        return this;
    }

    @Step("Отмена в баннере о несохранённых изменениях")
    public void dismissBrowserAlert() {
        assertEquals("Внесенные изменения не сохранятся. Покинуть страницу?", switchTo().alert().getText());
        dismiss();
        TestUtils.wait(500);
    }

    @Step("Создание шаблона отображения '{template.name}'")
    public OrderTemplatePage createOrderTemplate(ItemVisualTemplate template) {
        titleInput.setValue(template.getTitle());
        nameInput.setValue(template.getName());
        descriptionInput.setValue(template.getDescription());
        typeSelect.set(template.getEventType().get(0));
        providerSelect.set(template.getEventProvider().get(0));
        createButton.click();
        Alert.green("Шаблон успешно создан");
        return new OrderTemplatePage();
    }

    @Step("Проверка недоступности удаления включенного шаблона отображения")
    public void checkDeleteEnabledTemplate() {
        isEnabled.getLabel().scrollIntoView(true);
        isEnabled.setEnabled(true);
        deleteButton.getButton().hover();
        Assertions.assertEquals("Недоступно для включённого шаблона отображения", new Tooltip().toString());
        deleteButton.getButton().shouldBe(Condition.disabled);
        isEnabled.setEnabled(false);
        deleteButton.getButton().hover();
        Assertions.assertFalse(Tooltip.isVisible());
        deleteButton.getButton().shouldBe(Condition.enabled);
    }

    @Step("Проверка баннера о несохранённых изменениях. Отмена")
    public OrderTemplatePage checkUnsavedChangesAlertDismiss() {
        String newValue = "new";
        titleInput.setValue(newValue);
        back();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        orderTemplatesLink.click();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        mainPage.click();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        return this;
    }

    @Step("Проверка баннера о несохранённых изменениях. Ок")
    public OrderTemplatePage checkUnsavedChangesAlertAccept(ItemVisualTemplate template) {
        String newValue = "new title";
        titleInput.setValue(newValue);
        back();
        acceptAlert(unsavedChangesAlertText);
        new OrderTemplatesListPage().openTemplatePage(template.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(template.getTitle()));
        titleInput.setValue(newValue);
        orderTemplatesLink.click();
        acceptAlert(unsavedChangesAlertText);
        new OrderTemplatesListPage().openTemplatePage(template.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(template.getTitle()));
        titleInput.setValue(newValue);
        mainPage.click();
        acceptAlert(unsavedChangesAlertText);
        new ControlPanelIndexPage().goToOrderTemplatesPage().openTemplatePage(template.getName());
        titleInput.getInput().shouldHave(Condition.exactValue(template.getTitle()));
        return this;
    }
}
