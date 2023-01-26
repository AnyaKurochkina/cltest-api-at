package ui.cloud.pages.productCatalog.orderTemplate;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.productCatalog.visualTeamplate.ItemVisualTemplate;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.BasePage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Alert;
import ui.elements.Input;
import ui.elements.Select;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTemplatePage extends BasePage {

    private final SelenideElement previewButton = $x("//button[.//span[text()='Просмотр']]");
    private final Input titleInput = Input.byName("title");
    private final Input nameInput = Input.byName("name");
    private final Input descriptionInput = Input.byName("description");
    private final Select typeSelect = Select.byLabel("Тип");
    private final Select providerSelect = Select.byLabel("Провайдер");
    private final SelenideElement deleteButton = $x("//div[text()='Удалить']/parent::button");
    private final SelenideElement orderTemplatesLink = $x("//a[text()='Шаблоны отображения' and not(@href)]");

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

    @Step("Создание шаблона узлов '{template.name}'")
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
}
