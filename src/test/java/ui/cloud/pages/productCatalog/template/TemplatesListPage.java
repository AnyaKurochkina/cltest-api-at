package ui.cloud.pages.productCatalog.template;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.cloud.pages.productCatalog.BaseList;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Alert;
import ui.elements.DropDown;
import ui.elements.Input;
import ui.elements.Table;
import ui.uiModels.Template;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemplatesListPage {
    private static final String templateNameColumn = "Код шаблона";
    private final SelenideElement pageTitle = $x("//div[text() = 'Шаблоны узлов']");
    private final SelenideElement createTemplateButton = $x("//div[@data-testid = 'add-button']//button");
    private final Input nameInput = Input.byLabel("Код шаблона");
    private final Input titleInput = Input.byLabel("Наименование");
    private final SelenideElement descriptionInput = $x("//textarea[@name='description']");
    private final Input runQueueInput = Input.byLabel("Название очереди для старта задачи");
    private final Input rollbackQueueInput = Input.byLabel("Название очереди для отката");
    private final DropDown typeDropDown = DropDown.byLabel("Тип");
    private final Input searchInput = Input.byPlaceholder("Поиск");
    private final SelenideElement deleteAction = $x("//li[text() = 'Удалить']");
    private final SelenideElement cancelButton = $x("//span[text()='Отмена']/parent::button");
    private final SelenideElement noDataFound = $x("//td[text()='Нет данных для отображения']");
    private final SelenideElement templateNameValidationHint = $x("//p[text()='Поле может содержать только символы: \"a-z\", \"0-9\", \"_\", \"-\", \":\", \".\"']");
    private final SelenideElement titleRequiredFieldHint = $x("//input[@name='title']/parent::div/following-sibling::p[text()='Поле обязательно для заполнения']");
    private final SelenideElement nameRequiredFieldHint = $x("//input[@name='name']/parent::div/following-sibling::p[text()='Поле обязательно для заполнения']");
    private final SelenideElement runQueueRequiredFieldHint =
            $x("//input[@name='run']/parent::div/following-sibling::p[text()='Должно быть заполнено поле \"Название очереди для старта задачи\" и/или \"Название очереди для отката\"']");
    private final SelenideElement rollbackQueueRequiredFieldHint =
            $x("//input[@name='run']/parent::div/following-sibling::p[text()='Должно быть заполнено поле \"Название очереди для старта задачи\" и/или \"Название очереди для отката\"']");
    private final SelenideElement nonuniqueNameValidationHint = $x("//input[@name='name']/parent::div/following-sibling::p[text()='Шаблон с таким именем уже существует']");
    private final SelenideElement sortByCreateDate = $x("//div[text()='Дата создания']");
    private final SelenideElement saveButton = $x("//span[text()='Сохранить']/parent::button");
    private final SelenideElement dialogSaveButton = $x("//div[@role='dialog']//span[text()='Сохранить']/parent::button");


    public TemplatesListPage() {
        pageTitle.shouldBe(Condition.visible);
    }

    @Step("Создание шаблона узлов '{template.name}'")
    public TemplatePage createTemplate(Template template) {
        createTemplateButton.click();
        titleInput.setValue(template.getTitle());
        nameInput.setValue(template.getName());
        descriptionInput.setValue(template.getDescription());
        runQueueInput.setValue(template.getRunQueue());
        rollbackQueueInput.setValue(template.getRollbackQueue());
        typeDropDown.select(template.getType());
        saveButton.shouldBe(Condition.enabled).click();
        dialogSaveButton.click();
        new Alert().close();
        return new TemplatePage();
    }

    @Step("Проверка, что шаблон '{template.name}' найден при поиске по значению '{value}'")
    public TemplatesListPage findTemplateByValue(String value, Template template) {
        searchInput.setValue(value);
        TestUtils.wait(1000);
        new Table(templateNameColumn).isColumnValueEquals(templateNameColumn, template.getName());
        return this;
    }

    @Step("Проверка, что шаблоны не найдены при поиске по '{value}'")
    public TemplatesListPage checkTemplateNotFound(String value) {
        searchInput.setValue(value);
        TestUtils.wait(1000);
        noDataFound.shouldBe(Condition.visible);
        return this;
    }

    @Step("Удаление шаблона '{name}'")
    public TemplatesListPage deleteTemplate(String name) {
        BaseList.openActionMenu(templateNameColumn, name);
        deleteAction.click();
        new DeleteDialog().inputValidIdAndDelete();
        return this;
    }

    @Step("Проверка заголовков списка графов")
    public TemplatesListPage checkTemplatesListHeaders() {
        Table templatesList = new Table(templateNameColumn);
        assertEquals(0, templatesList.getHeaderIndex("Наименование"));
        assertEquals(1, templatesList.getHeaderIndex(templateNameColumn));
        assertEquals(2, templatesList.getHeaderIndex("Дата создания"));
        assertEquals(3, templatesList.getHeaderIndex("Описание"));
        return this;
    }

    @Step("Проверка валидации некорректных параметров при создании шаблона")
    public TemplatesListPage checkCreateTemplateDisabled(Template template) {
        createTemplateButton.click();
        titleInput.setValue(template.getTitle());
        nameInput.setValue(template.getName());
        descriptionInput.setValue(template.getDescription());
        runQueueInput.setValue(template.getRunQueue());
        rollbackQueueInput.setValue(template.getRollbackQueue());
        typeDropDown.select(template.getType());
        if (template.getTitle().isEmpty()) {
            titleRequiredFieldHint.shouldBe(Condition.visible);
        }
        if (template.getName().isEmpty()) {
            nameRequiredFieldHint.shouldBe(Condition.visible);
        }
        if (template.getRunQueue().isEmpty() && template.getRollbackQueue().isEmpty()) {
            runQueueRequiredFieldHint.shouldBe(Condition.visible);
            rollbackQueueRequiredFieldHint.shouldBe(Condition.visible);
        }
        saveButton.shouldBe(Condition.disabled);
        cancelButton.click();
        return this;
    }

    @Step("Проверка валидации неуникального имени шаблона узла 'template.name'")
    public TemplatesListPage checkNonUniqueNameValidation(Template template) {
        createTemplateButton.click();
        nameInput.setValue(template.getName());
        titleInput.setValue(template.getTitle());
        nonuniqueNameValidationHint.shouldBe(Condition.visible);
        saveButton.shouldBe(Condition.disabled);
        cancelButton.click();
        return this;
    }

    @Step("Открытие страницы шаблона '{name}'")
    public TemplatePage openTemplatePage(String name) {
        new Table(templateNameColumn).getRowElementByColumnValue(templateNameColumn, name).click();
        return new TemplatePage();
    }

    @Step("Проверка валидации недопустимых значений в коде шаблона")
    public TemplatesListPage checkTemplateNameValidation(String[] names) {
        createTemplateButton.shouldBe(Condition.visible).click();
        for (String name : names) {
            nameInput.setValue(name);
            TestUtils.wait(600);
            if (!templateNameValidationHint.exists()) {
                nameInput.getInput().sendKeys("t");
            }
            templateNameValidationHint.shouldBe(Condition.visible);
        }
        cancelButton.click();
        return this;
    }

    @Step("Проверка сортировки по наименованию")
    public TemplatesListPage checkSortingByTitle() {
        BaseList.checkSortingByStringField("Наименование");
        return this;
    }

    @Step("Проверка сортировки по коду шаблона")
    public TemplatesListPage checkSortingByName() {
        BaseList.checkSortingByStringField(templateNameColumn);
        return this;
    }

    @Step("Проверка сортировки по дате создания")
    public TemplatesListPage checkSortingByCreateDate() {
        BaseList.checkSortingByDateField("Дата создания");
        return this;
    }

    @Step("Сортировка по дате создания")
    public TemplatesListPage sortByCreateDate() {
        sortByCreateDate.click();
        return this;
    }

    @Step("Переход на последнюю страницу списка")
    public TemplatesListPage lastPage() {
        BaseList.lastPage();
        return this;
    }

    @Step("Проверка, что подсвечен шаблон 'name'")
    public void checkTemplateIsHighlighted(String name) {
        BaseList.checkRowIsHighlighted(templateNameColumn, name);
    }

    @Step("Поиск и открытие страницы шаблона '{name}'")
    public TemplatePage findAndOpenTemplatePage(String name) {
        searchInput.setValue(name);
        TestUtils.wait(500);
        new Table(templateNameColumn).getRowElementByColumnValue(templateNameColumn, name).click();
        return new TemplatePage();
    }
}