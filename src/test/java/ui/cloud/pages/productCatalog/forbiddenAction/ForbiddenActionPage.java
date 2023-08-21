package ui.cloud.pages.productCatalog.forbiddenAction;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import models.cloud.productCatalog.action.Action;
import models.cloud.productCatalog.forbiddenAction.ForbiddenAction;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.EntityPage;
import ui.elements.*;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.switchTo;
import static core.helper.StringUtils.$x;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static steps.productCatalog.ActionSteps.getActionById;

public class ForbiddenActionPage extends EntityPage {

    private final TextArea descriptionTextArea = TextArea.byLabel("Описание");
    private final SearchSelect actionSelect = SearchSelect.byLabel("Действие");
    private final SelenideElement nameRequiredFieldHint =
            nameInput.getInput().$x("./following::div[text()='Необходимо ввести код запрещенного действия']");
    private final SelenideElement nonUniqueNameValidationHint =
            nameInput.getInput().$x("./following::div[text()='Запрещенное действие с таким кодом уже существует. " +
                    "Выберите другое действие или направление разрешения']");
    private final SelenideElement titleRequiredFieldHint =
            titleInput.getInput().$x("./following::div[text()='Необходимо добавить минимум одну запись']");
    private final SelenideElement actionRequiredFieldHint =
            actionSelect.getElement().$x(".//div[text()='Поле обязательно для заполнения']");
    private final SelenideElement nameValidationHint =
            Selenide.$x("//div[text()='Поле может содержать только символы: " +
                    "\"a-z\", \"0-9\", \"_\", \"-\", \":\", \".\"']");
    private final SelenideElement paramsRequiredAlert =
            Selenide.$x("//div[@role='alert']//div[text()='Необходимо добавить минимум одну запись']");
    private final Button addButton = Button.byText("Добавить");
    private final Tab mainTab = Tab.byText("Основное");
    private final Tab paramsTab = Tab.byText("Параметры");

    public ForbiddenActionPage() {
        $x("//a[text()='Запрещенные действия']").shouldBe(Condition.visible);
    }

    @Step("Удаление запрещенного действия")
    public ForbiddenActionsListPage delete() {
        deleteButton.click();
        new DeleteDialog().inputValidIdAndDelete("Удаление выполнено успешно");
        return new ForbiddenActionsListPage();
    }

    @Step("Проверка валидации недопустимых значений в коде запрещенного действия")
    public ForbiddenActionsListPage checkNameValidation(String[] names) {
        for (String name : names) {
            nameInput.setValue(name);
            Waiting.findWithAction(() -> nameValidationHint.isDisplayed(),
                    () -> nameInput.getInput().sendKeys("t"), Duration.ofSeconds(3));
            nameValidationHint.shouldBe(Condition.visible);
        }
        cancelButton.click();
        return new ForbiddenActionsListPage();
    }

    @Step("Проверка валидации обязательных параметров при создании запрещенного действия")
    public ForbiddenActionsListPage checkRequiredParams(ForbiddenAction forbiddenAction) {
        descriptionTextArea.setValue("test");
        saveButton.getButton().shouldBe(Condition.disabled);
        nameRequiredFieldHint.shouldBe(Condition.visible);
        titleRequiredFieldHint.shouldBe(Condition.visible);
        titleInput.setValue(forbiddenAction.getTitle());
        titleRequiredFieldHint.shouldNotBe(Condition.visible);
        actionRequiredFieldHint.shouldBe(Condition.visible);
        Action action = getActionById(forbiddenAction.getActionId());
        actionSelect.setContains(action.getName());
        actionRequiredFieldHint.shouldNotBe(Condition.visible);
        nameRequiredFieldHint.shouldNotBe(Condition.visible);
        paramsTab.switchTo();
        paramsRequiredAlert.shouldBe(Condition.visible);
        addTypeProvider(forbiddenAction.getEventTypeProvider().get(0).getEvent_type(),
                forbiddenAction.getEventTypeProvider().get(0).getEvent_provider());
        paramsRequiredAlert.shouldNotBe(Condition.visible);
        saveButton.getButton().shouldBe(Condition.enabled);
        cancelButton.click();
        switchTo().alert().accept();
        return new ForbiddenActionsListPage();
    }

    @Step("Добавление типа/провайдера")
    private void addTypeProvider(String type, String provider) {
        addButton.click();
        Waiting.sleep(3000);
        Table table = new Table("Тип");
        SearchSelect typeSelect = new SearchSelect(table.getRow(0).get().$x("(.//div[select])[1]"));
        SearchSelect providerSelect = new SearchSelect(table.getRow(0).get().$x("(.//div[select])[2]"));
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

    @Step("Проверка валидации неуникального имени запрещенного действия '{forbiddenAction.name}'")
    public ForbiddenActionsListPage checkNonUniqueNameValidation(ForbiddenAction forbiddenAction) {
        Action action = getActionById(forbiddenAction.getActionId());
        actionSelect.setContains(action.getName());
        titleInput.setValue(forbiddenAction.getTitle());
        nonUniqueNameValidationHint.shouldBe(Condition.visible);
        saveButton.getButton().shouldBe(Condition.disabled);
        cancelButton.click();
        return new ForbiddenActionsListPage();
    }

    @Step("Заполнение атрибутов запрещенного действия '{forbiddenAction.name}'")
    public ForbiddenActionPage setAttributes(ForbiddenAction forbiddenAction) {
        Action action = getActionById(forbiddenAction.getActionId());
        actionSelect.setContains(action.getName());
        titleInput.setValue(forbiddenAction.getTitle());
        descriptionTextArea.setValue(forbiddenAction.getDescription());
        assertAll(() -> nameRequiredFieldHint.shouldNotBe(Condition.visible),
                () -> titleRequiredFieldHint.shouldNotBe(Condition.visible),
                () -> actionRequiredFieldHint.shouldNotBe(Condition.visible));
        paramsTab.switchTo();
        Table table = new Table("Тип");
        if (!table.isEmpty()) clearTypeProvider();
        addTypeProvider(forbiddenAction.getEventTypeProvider().get(0).getEvent_type(),
                forbiddenAction.getEventTypeProvider().get(0).getEvent_provider());
        return this;
    }

    @Step("Проверка атрибутов запрещенного действия '{forbiddenAction.name}'")
    public ForbiddenActionPage checkAttributes(ForbiddenAction forbiddenAction) {
        mainTab.switchTo();
        Action action = getActionById(forbiddenAction.getActionId());
        nameInput.getInput().shouldHave(Condition.exactValue(action.getName() + "__parent_to_child"));
        titleInput.getInput().shouldHave(Condition.exactValue(forbiddenAction.getTitle()));
        assertTrue(actionSelect.getValue().contains(action.getName()));
        descriptionTextArea.getElement().shouldHave(Condition.exactValue(forbiddenAction.getDescription()));
        paramsTab.switchTo();
        Table table = new Table("Тип");
        table.getRowByColumnValue("Тип", forbiddenAction.getEventTypeProvider().get(0).getEvent_type());
        table.getRowByColumnValue("Провайдер", forbiddenAction.getEventTypeProvider().get(0).getEvent_provider());
        return this;
    }
}
