package ui.cloud.pages.productCatalog.jinja2Template;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.google.gson.Gson;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import models.cloud.productCatalog.jinja2.Jinja2Template;
import models.cloud.productCatalog.template.Template;
import ui.cloud.pages.ControlPanelIndexPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.EntityPage;
import ui.elements.Button;
import ui.elements.SearchSelect;
import ui.elements.Tab;
import ui.elements.TextArea;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.back;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static steps.productCatalog.TemplateSteps.getTemplateById;

@Getter
public class Jinja2TemplatePage extends EntityPage {

    private final TextArea descriptionTextArea = TextArea.byLabel("Описание");
    private final TextArea templateTextArea = TextArea.byLabel("Jinja2 шаблон");
    private final TextArea dataTextArea = TextArea.byLabel("Тестовые данные");
    private final TextArea resultTextArea = TextArea.byLabel("Результат");
    private final SelenideElement nameRequiredFieldHint =
            nameInput.getInput().$x("./following::*[text()='Необходимо ввести код шаблона']");
    private final SelenideElement nonUniqueNameValidationHint =
            nameInput.getInput().$x("./following::*[text()='Шаблон Jinja2 с таким именем уже существует']");
    private final SelenideElement titleRequiredFieldHint =
            titleInput.getInput().$x("./following::*[text()='Необходимо ввести наименование шаблона']");
    private final SelenideElement templateRequiredFieldHint =
            templateTextArea.getElement().$x("./following::p[text()='Поле шаблона не может быть пустым']");
    private final SelenideElement dataIncorrectJsonHint =
            dataTextArea.getElement().$x("./following::p[text()='Некорректный JSON']");
    private final SelenideElement nameValidationHint =
            Selenide.$x("//*[text()='Поле может содержать только символы: \"a-z\", \"0-9\", \"_\", \"-\", \":\", \".\"']");
    private final Button testTemplateButton = Button.byText("Протестировать шаблон");
    private final SelenideElement templatesListLink = Selenide.$x("//a[text()='Шаблоны Jinja2']");
    private final Button formatButton = Button.byText("Форматировать текст");
    private final Button copyToClipboardButton = Button.byText("Копировать шаблон в буфер");
    private final Button clearResultButton = Button.byText("Очистить поле");
    private final Tab mainTab = Tab.byText("Основное");
    private final Tab paramsTab = Tab.byText("Параметры данных");
    private final Tab templateTab = Tab.byText("Шаблон узлов");
    private final SearchSelect templateSelect = SearchSelect.byLabel("Шаблон узлов");

    public Jinja2TemplatePage() {
        templatesListLink.shouldBe(Condition.visible);
    }

    @Step("Удаление шаблона Jinja2")
    public Jinja2TemplatesListPage delete() {
        deleteButton.click();
        new DeleteDialog().submitAndDelete("Шаблон удален");
        return new Jinja2TemplatesListPage();
    }

    @Step("Проверка валидации недопустимых значений в коде шаблона Jinja2")
    public Jinja2TemplatesListPage checkNameValidation(String[] names) {
        mainTab.switchTo();
        for (String name : names) {
            nameInput.setValue(name);
            Waiting.findWithAction(() -> nameValidationHint.isDisplayed(),
                    () -> nameInput.getInput().sendKeys("t"), Duration.ofSeconds(3));
            nameValidationHint.shouldBe(Condition.visible);
        }
        cancelButton.click();
        return new Jinja2TemplatesListPage();
    }

    @Step("Проверка валидации обязательных параметров при создании шаблона Jinja2")
    public Jinja2TemplatesListPage checkRequiredParams(Jinja2Template jinja2Template) {
        mainTab.switchTo();
        descriptionTextArea.setValue("test");
        createButton.getButton().shouldBe(Condition.disabled);
        nameRequiredFieldHint.shouldBe(Condition.visible);
        nameInput.setValue(jinja2Template.getName());
        nameRequiredFieldHint.shouldNotBe(Condition.visible);
        titleRequiredFieldHint.shouldBe(Condition.visible);
        titleInput.setValue(jinja2Template.getTitle());
        titleRequiredFieldHint.shouldNotBe(Condition.visible);
        paramsTab.switchTo();
        templateRequiredFieldHint.shouldBe(Condition.visible);
        templateTextArea.setValue(jinja2Template.getJinja2Template());
        templateRequiredFieldHint.shouldNotBe(Condition.visible);
        createButton.getButton().shouldBe(Condition.enabled);
        cancelButton.click();
        switchTo().alert().accept();
        return new Jinja2TemplatesListPage();
    }

    @Step("Проверка валидации неуникального имени шаблона Jinja2 '{jinja2Template.name}'")
    public Jinja2TemplatesListPage checkNonUniqueNameValidation(Jinja2Template jinja2Template) {
        mainTab.switchTo();
        titleInput.setValue(jinja2Template.getTitle());
        nameInput.setValue(jinja2Template.getName());
        Waiting.find(() -> nonUniqueNameValidationHint.isDisplayed(), Duration.ofSeconds(5));
        nonUniqueNameValidationHint.shouldBe(Condition.visible);
        createButton.getButton().shouldBe(Condition.disabled);
        cancelButton.click();
        return new Jinja2TemplatesListPage();
    }

    @Step("Заполнение атрибутов шаблона Jinja2 '{jinja2Template.name}'")
    public Jinja2TemplatePage setAttributes(Jinja2Template jinja2Template) {
        mainTab.switchTo();
        descriptionTextArea.setValue(jinja2Template.getDescription());
        nameInput.setValue(jinja2Template.getName());
        nameRequiredFieldHint.shouldNotBe(Condition.visible);
        titleInput.setValue(jinja2Template.getTitle());
        templateTab.switchTo();
        Template template = getTemplateById(jinja2Template.getTemplateId());
        Waiting.find(() -> templateSelect.getValue().contains(template.getName()), Duration.ofSeconds(10));
        paramsTab.switchTo();
        templateTextArea.setValue(jinja2Template.getJinja2Template());
        templateRequiredFieldHint.shouldNotBe(Condition.visible);
        dataTextArea.setValue(new Gson().toJson(jinja2Template.getJinja2Data()));
        dataIncorrectJsonHint.shouldNotBe(Condition.visible);
        return this;
    }

    @Step("Проверка атрибутов шаблона Jinja2 '{jinja2Template.name}'")
    public Jinja2TemplatePage checkAttributes(Jinja2Template jinja2Template) {
        mainTab.switchTo();
        nameInput.getInput().shouldHave(Condition.exactValue(jinja2Template.getName()));
        titleInput.getInput().shouldHave(Condition.exactValue(jinja2Template.getTitle()));
        descriptionTextArea.getElement().shouldHave(Condition.exactValue(jinja2Template.getDescription()));
        paramsTab.switchTo();
        assertEquals(jinja2Template.getJinja2Template(), templateTextArea.getValue());
        assertEquals(new Gson().toJson(jinja2Template.getJinja2Data()), dataTextArea.getWhitespacesRemovedValue());
        return this;
    }

    @Step("Проверка баннера о несохранённых изменениях. Отмена")
    public Jinja2TemplatePage checkUnsavedChangesAlertDismiss() {
        String newValue = "new value";
        titleInput.setValue(newValue);
        back();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        templatesListLink.click();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        mainPage.click();
        dismissAlert(unsavedChangesAlertText);
        titleInput.getInput().shouldHave(Condition.exactValue(newValue));
        return this;
    }

    @Step("Проверка баннера о несохранённых изменениях. Ок")
    public Jinja2TemplatePage checkUnsavedChangesAlertAccept(Jinja2Template jinja2Template) {
        String newValue = "new value";
        mainTab.switchTo();
        titleInput.setValue(newValue);
        back();
        acceptAlert(unsavedChangesAlertText);
        new Jinja2TemplatesListPage().openJinja2TemplatePage(jinja2Template.getName());
        mainTab.switchTo();
        titleInput.getInput().shouldHave(Condition.exactValue(jinja2Template.getTitle()));
        titleInput.setValue(newValue);
        templatesListLink.click();
        acceptAlert(unsavedChangesAlertText);
        new Jinja2TemplatesListPage().openJinja2TemplatePage(jinja2Template.getName());
        mainTab.switchTo();
        titleInput.getInput().shouldHave(Condition.exactValue(jinja2Template.getTitle()));
        descriptionTextArea.setValue(newValue);
        mainPage.click();
        acceptAlert(unsavedChangesAlertText);
        new ControlPanelIndexPage().goToJinja2TemplatesListPage().findAndOpenJinja2TemplatePage(jinja2Template.getName());
        mainTab.switchTo();
        descriptionTextArea.getElement().shouldHave(Condition.exactValue(jinja2Template.getDescription()));
        return this;
    }
}
