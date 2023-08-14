package ui.cloud.pages.productCatalog.allowedAction;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.allowedAction.AllowedAction;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.EntityPage;
import ui.elements.*;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.switchTo;
import static core.helper.StringUtils.$x;
import static org.junit.jupiter.api.Assertions.assertAll;
import static steps.productCatalog.ActionSteps.getActionById;

public class AllowedActionPage extends EntityPage {

    private final TextArea descriptionTextArea = TextArea.byLabel("Описание");
    private final SearchSelect actionSelect = SearchSelect.byLabel("Действие");
    private final SelenideElement nameRequiredFieldHint =
            nameInput.getInput().$x("./following::div[text()='Необходимо ввести код разрешенного действия']");
    private final SelenideElement nonUniqueNameValidationHint =
            nameInput.getInput().$x("./following::div[text()='Разрешенное действие с таким кодом уже существует. " +
                    "Выберите другое действие или направление разрешения']");
    private final SelenideElement titleRequiredFieldHint =
            titleInput.getInput().$x("./following::div[text()='Необходимо ввести наименование разрешенного действия']");
    private final SelenideElement actionRequiredFieldHint =
            actionSelect.getElement().$x(".//div[text()='Поле обязательно для заполнения']");
    private final SelenideElement nameValidationHint =
            Selenide.$x("//div[text()='Поле может содержать только символы: \"a-z\", \"0-9\", \"_\", \"-\", \":\", \".\"']");
    private final SelenideElement paramsRequiredAlert =
            Selenide.$x("//div[@role='alert']//div[text()='Необходимо добавить минимум одну запись']");
    private final Button addButton = Button.byText("Добавить");
    private final Tab mainTab = Tab.byText("Основное");
    private final Tab paramsTab = Tab.byText("Параметры");

    public AllowedActionPage() {
        $x("//a[text()='Разрешенные действия']").shouldBe(Condition.visible);
    }

    @Step("Удаление разрешенного действия")
    public AllowedActionsListPage delete() {
        deleteButton.click();
        new DeleteDialog().inputValidIdAndDelete("Удаление выполнено успешно");
        return new AllowedActionsListPage();
    }

    @Step("Проверка валидации недопустимых значений в коде запрещенного действия")
    public AllowedActionsListPage checkNameValidation(String[] names) {
        for (String name : names) {
            nameInput.setValue(name);
            Waiting.findWithAction(() -> nameValidationHint.isDisplayed(),
                    () -> nameInput.getInput().sendKeys("t"), Duration.ofSeconds(3));
            nameValidationHint.shouldBe(Condition.visible);
        }
        cancelButton.click();
        return new AllowedActionsListPage();
    }

    @Step("Проверка валидации обязательных параметров при создании запрещенного действия")
    public AllowedActionsListPage checkRequiredParams(AllowedAction allowedAction) {
        descriptionTextArea.setValue("test");
        saveButton.getButton().shouldBe(Condition.disabled);
        nameRequiredFieldHint.shouldBe(Condition.visible);
        titleRequiredFieldHint.shouldBe(Condition.visible);
        titleInput.setValue(allowedAction.getTitle());
        titleRequiredFieldHint.shouldNotBe(Condition.visible);
        actionRequiredFieldHint.shouldBe(Condition.visible);
        Action action = getActionById(allowedAction.getActionId());
        actionSelect.setContains(action.getName());
        actionRequiredFieldHint.shouldNotBe(Condition.visible);
        nameRequiredFieldHint.shouldNotBe(Condition.visible);
        paramsTab.switchTo();
        paramsRequiredAlert.shouldBe(Condition.visible);
        addTypeProvider(allowedAction.getEventTypeProvider().get(0).getEvent_type(),
                allowedAction.getEventTypeProvider().get(0).getEvent_provider());
        paramsRequiredAlert.shouldNotBe(Condition.visible);
        saveButton.getButton().shouldBe(Condition.enabled);
        cancelButton.click();
        switchTo().alert().accept();
        return new AllowedActionsListPage();
    }

    @Step("Добавление типа/провайдера")
    private void addTypeProvider(String type, String provider) {
        addButton.click();
        Waiting.sleep(3500);
        Table table = new Table("Тип");
        Select typeSelect = new Select(table.getRow(0).get().$x("(.//div[select])[1]"));
        Select providerSelect = new Select(table.getRow(0).get().$x("(.//div[select])[2]"));
        typeSelect.set(type);
        providerSelect.set(provider);
        table.getRow(0).get().$x(".//button[.='Сохранить']").click();
    }

    @Step("Очистка таблицы типов/провайдеров")
    private void clearTypeProvider() {
        Table table = new Table("Тип");
        table.getRow(0).get().$$x(".//button").get(1).click();
        new DeleteDialog().submitAndDelete();
    }

    @Step("Проверка валидации неуникального имени разрешенного действия '{allowedAction.name}'")
    public AllowedActionsListPage checkNonUniqueNameValidation(AllowedAction allowedAction) {
        Action action = getActionById(allowedAction.getActionId());
        actionSelect.setContains(action.getName());
        titleInput.setValue(allowedAction.getTitle());
        nonUniqueNameValidationHint.shouldBe(Condition.visible);
        saveButton.getButton().shouldBe(Condition.disabled);
        cancelButton.click();
        return new AllowedActionsListPage();
    }

    @Step("Заполнение атрибутов разрешенного действия '{allowedAction.name}'")
    public AllowedActionPage setAttributes(AllowedAction allowedAction) {
        titleInput.setValue(allowedAction.getTitle());
        Action action = getActionById(allowedAction.getActionId());
        actionSelect.setContains(action.getName());
        descriptionTextArea.setValue(allowedAction.getDescription());
        assertAll(() -> nameRequiredFieldHint.shouldNotBe(Condition.visible),
                () -> titleRequiredFieldHint.shouldNotBe(Condition.visible),
                () -> actionRequiredFieldHint.shouldNotBe(Condition.visible));
        paramsTab.switchTo();
        Table table = new Table("Тип");
        if (!table.isEmpty()) clearTypeProvider();
        addTypeProvider(allowedAction.getEventTypeProvider().get(0).getEvent_type(),
                allowedAction.getEventTypeProvider().get(0).getEvent_provider());
        return this;
    }

    @Step("Проверка атрибутов разрешенного действия '{allowedAction.name}'")
    public AllowedActionPage checkAttributes(AllowedAction allowedAction) {
        Action action = getActionById(allowedAction.getActionId());
        mainTab.switchTo();
        nameInput.getInput().shouldHave(Condition.exactValue(action.getName() + "__parent_to_child"));
        titleInput.getInput().shouldHave(Condition.exactValue(allowedAction.getTitle()));
        Waiting.find(() -> actionSelect.getValue().contains(action.getName()), Duration.ofSeconds(5));
        descriptionTextArea.getElement().shouldHave(Condition.exactValue(allowedAction.getDescription()));
        paramsTab.switchTo();
        Table table = new Table("Тип");
        table.getRowByColumnValue("Тип", allowedAction.getEventTypeProvider().get(0).getEvent_type());
        table.getRowByColumnValue("Провайдер", allowedAction.getEventTypeProvider().get(0).getEvent_provider());
        return this;
    }
}
