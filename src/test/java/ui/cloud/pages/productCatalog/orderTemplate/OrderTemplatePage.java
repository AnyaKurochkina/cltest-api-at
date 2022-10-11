package ui.cloud.pages.productCatalog.orderTemplate;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.productCatalog.ItemVisualTemplate;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.elements.DropDown;
import ui.elements.Input;

import static com.codeborne.selenide.Selenide.$x;

public class OrderTemplatePage {

    private final SelenideElement previewButton = $x("//button[.//span[text()='Просмотр']]");
    private final Input titleInput = Input.byName("title");
    private final Input nameInput = Input.byName("name");
    private final Input descriptionInput = Input.byName("description");
    private final DropDown typeInput = DropDown.byInputName("event_type");
    private final DropDown providerInput = DropDown.byInputName("event_provider");
    private final SelenideElement deleteButton = $x("//div[text()='Удалить']/parent::button");

    public OrderTemplatePage() {
        previewButton.shouldBe(Condition.visible);
    }

    @Step("Проверка атрибутов шаблона '{template.name}'")
    public OrderTemplatePage checkAttributes(ItemVisualTemplate template) {
        nameInput.getInput().shouldHave(Condition.exactValue(template.getName()));
        titleInput.getInput().shouldHave(Condition.exactValue(template.getTitle()));
        descriptionInput.getInput().shouldHave(Condition.exactValue(template.getDescription()));
        Assertions.assertEquals(template.getEventType().get(0), typeInput.getElement().getText());
        Assertions.assertEquals(template.getEventProvider().get(0), providerInput.getElement().getText());
        return this;
    }

    @Step("Удаление шаблона")
    public void deleteTemplate() {
        deleteButton.click();
        new DeleteDialog().submitAndDelete("Шаблон удален");
    }
}
