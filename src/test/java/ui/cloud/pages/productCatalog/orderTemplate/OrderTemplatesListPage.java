package ui.cloud.pages.productCatalog.orderTemplate;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import core.utils.AssertUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import models.cloud.productCatalog.visualTeamplate.ItemVisualTemplate;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.EntityListPage;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderTemplatesListPage extends EntityListPage {

    public static final String ORDER_TEMPLATE_NAME_COLUMN = "Код шаблона";
    private final Input titleInput = Input.byName("title");
    private final Input nameInput = Input.byName("name");
    private final Input descriptionInput = Input.byName("description");
    private final SelenideElement titleRequiredFieldHint =
            titleInput.getInput().$x("./following::div[text()='Необходимо ввести наименование шаблона']");
    private final SelenideElement nameRequiredFieldHint =
            nameInput.getInput().$x("./following::div[text()='Необходимо ввести код шаблона']");
    private final SelenideElement nonUniqueNameValidationHint =
            nameInput.getInput().$x("./following::div[text()='Шаблон отображения с таким именем уже существует']");
    private final SelenideElement nameValidationHint =
            nameInput.getInput().$x("./following::div[text()='Поле может содержать только символы: \"a-z\", \"0-9\", \"_\", \"-\", \":\", \".\"']");
    private final Select typeDropDown = Select.byLabel("Тип");
    private final Select providerDropDown = Select.byLabel("Провайдер");
    private final Select stateDropDown = Select.byLabel("Состояние");
    private final Button applyFiltersButton = Button.byText("Применить");
    private final Button clearFiltersButton = Button.byText("Сбросить фильтры");

    @Step("Проверка заголовков списка шаблонов отображения")
    public OrderTemplatesListPage checkHeaders() {
        AssertUtils.assertHeaders(new Table(ORDER_TEMPLATE_NAME_COLUMN),
                "Наименование", ORDER_TEMPLATE_NAME_COLUMN, "Дата создания", "Дата изменения", "Описание", "Тип", "Провайдер", "Состояние",
                "Теги", "", "");
        return this;
    }

    @Step("Проверка сортировки по наименованию")
    public OrderTemplatesListPage checkSortingByTitle() {
        EntityListPage.checkSortingByStringField("Наименование");
        return this;
    }

    @Step("Проверка сортировки по коду шаблона")
    public OrderTemplatesListPage checkSortingByName() {
        EntityListPage.checkSortingByStringField(ORDER_TEMPLATE_NAME_COLUMN);
        return this;
    }

    @Step("Проверка сортировки по дате создания")
    public OrderTemplatesListPage checkSortingByCreateDate() {
        EntityListPage.checkSortingByDateField("Дата создания");
        return this;
    }

    @Step("Проверка сортировки по состоянию")
    public OrderTemplatesListPage checkSortingByState() {
        String onStateColor = "#4caf50";
        String offStateColor = "#e0e0e0";
        String header = "Состояние";
        Table table = new Table(ORDER_TEMPLATE_NAME_COLUMN);
        SelenideElement columnHeader = StringUtils.$x("//div[text()='{}']/parent::div", header);
        SelenideElement arrowIcon = StringUtils.$x("//div[text()='{}']/following-sibling::*[name()='svg']", header);
        columnHeader.click();
        TestUtils.wait(1000);
        arrowIcon.shouldBe(Condition.visible);
        String firstElementState = table.getValueByColumnInFirstRow(header).$x(".//*[name()='svg']")
                .getAttribute("color");
        Assertions.assertEquals(offStateColor, firstElementState);
        columnHeader.click();
        TestUtils.wait(1000);
        arrowIcon.shouldBe(Condition.visible);
        firstElementState = table.getValueByColumnInFirstRow(header).$x(".//*[name()='svg']")
                .getAttribute("color");
        Assertions.assertEquals(onStateColor, firstElementState);
        return this;
    }

    @Step("Создание шаблона узлов '{template.name}'")
    public OrderTemplatePage createOrderTemplate(ItemVisualTemplate template) {
        addNewObjectButton.click();
        new OrderTemplatePage().createOrderTemplate(template);
        return new OrderTemplatePage();
    }

    @Step("Проверка валидации обязательных параметров при создании шаблона")
    public OrderTemplatesListPage checkCreateTemplateDisabled(ItemVisualTemplate template) {
        addNewObjectButton.getButton().scrollIntoView(false).click();
        titleInput.setValue(template.getTitle());
        nameInput.setValue(template.getName());
        descriptionInput.setValue(template.getDescription());
        if (template.getTitle().isEmpty()) {
            titleRequiredFieldHint.shouldBe(Condition.visible);
        }
        if (template.getName().isEmpty()) {
            nameRequiredFieldHint.shouldBe(Condition.visible);
        }
        createButton.getButton().shouldBe(Condition.disabled);
        cancelButton.click();
        return this;
    }

    @Step("Проверка валидации неуникального имени шаблона узла '{template.name}'")
    public OrderTemplatesListPage checkNonUniqueNameValidation(ItemVisualTemplate template) {
        TestUtils.scrollToTheTop();
        addNewObjectButton.click();
        nameInput.setValue(template.getName());
        titleInput.setValue(template.getTitle());
        nonUniqueNameValidationHint.shouldBe(Condition.visible);
        createButton.getButton().shouldBe(Condition.disabled);
        cancelButton.click();
        return this;
    }

    @Step("Проверка валидации недопустимых значений в коде шаблона")
    public OrderTemplatesListPage checkNameValidation(String[] names) {
        addNewObjectButton.click();
        for (String name : names) {
            nameInput.setValue(name);
            Waiting.findWithAction(() -> nameValidationHint.isDisplayed(),
                    () -> nameInput.getInput().sendKeys("t"), Duration.ofSeconds(3));
            nameValidationHint.shouldBe(Condition.visible);
        }
        cancelButton.click();
        return this;
    }

    @Step("Удаление шаблона '{name}'")
    public OrderTemplatesListPage deleteTemplate(String name) {
        search(name);
        EntityListPage.delete(ORDER_TEMPLATE_NAME_COLUMN, name);
        new DeleteDialog().submitAndDelete("Удаление выполнено успешно");
        return this;
    }

    @Step("Проверка, что шаблоны не найдены при поиске по '{value}'")
    public OrderTemplatesListPage checkTemplateNotFound(String value) {
        search(value);
        assertTrue(new Table(ORDER_TEMPLATE_NAME_COLUMN).isEmpty());
        return this;
    }

    @Step("Поиск и открытие страницы шаблона '{name}'")
    public OrderTemplatePage findAndOpenTemplatePage(String name) {
        search(name);
        new Table(ORDER_TEMPLATE_NAME_COLUMN).getRowByColumnValue(ORDER_TEMPLATE_NAME_COLUMN, name).get().click();
        TestUtils.wait(600);
        return new OrderTemplatePage();
    }

    @Step("Копирование шаблона '{name}'")
    public OrderTemplatePage copyTemplate(String name) {
        new EntityListPage().copy(ORDER_TEMPLATE_NAME_COLUMN, name);
        Alert.green("Шаблон скопирован");
        TestUtils.wait(500);
        return new OrderTemplatePage();
    }

    @Step("Проверка, что шаблон '{template.name}' найден при поиске по значению '{value}'")
    public OrderTemplatesListPage findTemplateByValue(String value, ItemVisualTemplate template) {
        search(value);
        assertTrue(new Table(ORDER_TEMPLATE_NAME_COLUMN).isColumnValueEquals(ORDER_TEMPLATE_NAME_COLUMN, template.getName()));
        return this;
    }

    @Step("Выбор в фильтре по типу значения '{value}'")
    public OrderTemplatesListPage setTypeFilter(String value) {
        typeDropDown.set(value);
        return this;
    }

    @Step("Выбор в фильтре по провайдеру значения '{value}'")
    public OrderTemplatesListPage setProviderFilter(String value) {
        providerDropDown.set(value);
        return this;
    }

    @Step("Выбор в фильтре по состоянию значения '{value}'")
    public OrderTemplatesListPage setStateFilter(String value) {
        stateDropDown.set(value);
        return this;
    }

    @Step("Применение фильтров")
    public OrderTemplatesListPage applyFilters() {
        applyFiltersButton.click();
        Waiting.sleep(500);
        return this;
    }

    @Step("Удаление заданного значения фильтра '{value}'")
    public OrderTemplatesListPage removeFilterTag(String value) {
        StringUtils.$x("//span[text()='{}']/following-sibling::*[name()='svg']", value).click();
        TestUtils.wait(500);
        return this;
    }

    @Step("Сброс фильтров")
    public OrderTemplatesListPage clearFilters() {
        clearFiltersButton.click();
        TestUtils.wait(500);
        return this;
    }

    @Step("Проверка, что шаблон '{template.name}' отображается в списке")
    public OrderTemplatesListPage checkTemplateIsDisplayed(ItemVisualTemplate template) {
        assertTrue(new Table(ORDER_TEMPLATE_NAME_COLUMN).isColumnValueEquals(ORDER_TEMPLATE_NAME_COLUMN, template.getName()));
        return this;
    }

    @Step("Проверка, что шаблон '{template.name}' не отображается в списке")
    public OrderTemplatesListPage checkTemplateIsNotDisplayed(ItemVisualTemplate template) {
        Assertions.assertFalse(new Table(ORDER_TEMPLATE_NAME_COLUMN).isColumnValueEquals(ORDER_TEMPLATE_NAME_COLUMN, template.getName()));
        return this;
    }

    @Step("Открытие страницы шаблона '{name}'")
    public OrderTemplatePage openTemplatePage(String name) {
        new Table(ORDER_TEMPLATE_NAME_COLUMN).getRowByColumnValue(ORDER_TEMPLATE_NAME_COLUMN, name).get().click();
        return new OrderTemplatePage();
    }
}
