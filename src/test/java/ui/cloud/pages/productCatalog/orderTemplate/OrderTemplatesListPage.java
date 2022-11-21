package ui.cloud.pages.productCatalog.orderTemplate;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import io.qameta.allure.Step;
import models.cloud.productCatalog.visualTeamplate.ItemVisualTemplate;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebElement;
import ui.cloud.pages.productCatalog.BaseListPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Alert;
import ui.elements.DropDown;
import ui.elements.Input;
import ui.elements.Table;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTemplatesListPage {

    private static final String columnName = "Код шаблона";
    private final Input searchInput = Input.byPlaceholder("Поиск");
    private final SelenideElement createTemplateButton = $x("//div[@data-testid = 'add-button']//button");
    private final Input titleInput = Input.byName("title");
    private final Input nameInput = Input.byName("name");
    private final Input descriptionInput = Input.byName("description");
    private final DropDown typeInput = DropDown.byInputName("event_type");
    private final DropDown providerInput = DropDown.byInputName("event_provider");
    private final SelenideElement titleRequiredFieldHint =
            $x("//input[@name='title']/parent::div/following-sibling::p[text()='Необходимо ввести наименование шаблона']");
    private final SelenideElement nameRequiredFieldHint =
            $x("//input[@name='name']/parent::div/following-sibling::p[text()='Необходимо ввести код шаблона']");
    private final SelenideElement nonUniqueNameValidationHint =
            $x("//input[@name='name']/parent::div/following-sibling::p[text()='Шаблон с таким кодом уже существует']");
    private final SelenideElement nameValidationHint =
            $x("//p[text()='Поле может содержать только символы: \"a-z\", \"0-9\", \"_\", \"-\", \":\", \".\"']");
    private final SelenideElement createButton = $x("//div[text()='Создать']/parent::button");
    private final SelenideElement cancelButton = $x("//div[text()='Отмена']/parent::button");
    private final SelenideElement noDataFound = $x("//td[text()='Нет данных для отображения']");
    private final DropDown typeDropDown = DropDown.byLabel("Тип");
    private final DropDown providerDropDown = DropDown.byLabel("Провайдер");
    private final DropDown stateDropDown = DropDown.byLabel("Состояние");
    private final WebElement applyFiltersButton = $x("//button[div[text()='Применить']]");
    private final WebElement clearFiltersButton = $x("//button[text()='Сбросить фильтры']");

    @Step("Проверка заголовков списка графов")
    public OrderTemplatesListPage checkHeaders() {
        Table templatesList = new Table(columnName);
        assertEquals(0, templatesList.getHeaderIndex("Наименование"));
        assertEquals(1, templatesList.getHeaderIndex(columnName));
        assertEquals(2, templatesList.getHeaderIndex("Дата создания"));
        assertEquals(3, templatesList.getHeaderIndex("Описание"));
        assertEquals(4, templatesList.getHeaderIndex("Тип"));
        assertEquals(5, templatesList.getHeaderIndex("Провайдер"));
        assertEquals(6, templatesList.getHeaderIndex("Состояние"));
        return this;
    }

    @Step("Проверка сортировки по наименованию")
    public OrderTemplatesListPage checkSortingByTitle() {
        BaseListPage.checkSortingByStringField("Наименование");
        return this;
    }

    @Step("Проверка сортировки по коду шаблона")
    public OrderTemplatesListPage checkSortingByName() {
        BaseListPage.checkSortingByStringField(columnName);
        return this;
    }

    @Step("Проверка сортировки по дате создания")
    public OrderTemplatesListPage checkSortingByCreateDate() {
        BaseListPage.checkSortingByDateField("Дата создания");
        return this;
    }

    @Step("Проверка сортировки по состоянию")
    public OrderTemplatesListPage checkSortingByState() {
        String onStateColor = "#4caf50";
        String offStateColor = "#e0e0e0";
        String header = "Состояние";
        Table table = new Table(columnName);
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
        createTemplateButton.click();
        titleInput.setValue(template.getTitle());
        nameInput.setValue(template.getName());
        descriptionInput.setValue(template.getDescription());
        typeInput.select(template.getEventType().get(0));
        providerInput.select(template.getEventProvider().get(0));
        createButton.click();
        new Alert().checkText("Шаблон успешно создан").checkColor(Alert.Color.GREEN).close();
        return new OrderTemplatePage();
    }

    @Step("Проверка валидации обязательных параметров при создании шаблона")
    public OrderTemplatesListPage checkCreateTemplateDisabled(ItemVisualTemplate template) {
        TestUtils.scrollToTheTop();
        createTemplateButton.click();
        titleInput.setValue(template.getTitle());
        nameInput.setValue(template.getName());
        descriptionInput.setValue(template.getDescription());
        if (template.getTitle().isEmpty()) {
            titleRequiredFieldHint.shouldBe(Condition.visible);
        }
        if (template.getName().isEmpty()) {
            nameRequiredFieldHint.shouldBe(Condition.visible);
        }
        createButton.shouldBe(Condition.disabled);
        cancelButton.click();
        return this;
    }

    @Step("Проверка валидации неуникального имени шаблона узла '{template.name}'")
    public OrderTemplatesListPage checkNonUniqueNameValidation(ItemVisualTemplate template) {
        TestUtils.scrollToTheTop();
        createTemplateButton.click();
        nameInput.setValue(template.getName());
        titleInput.setValue(template.getTitle());
        nonUniqueNameValidationHint.shouldBe(Condition.visible);
        createButton.shouldBe(Condition.disabled);
        cancelButton.click();
        return this;
    }

    @Step("Проверка валидации недопустимых значений в коде шаблона")
    public OrderTemplatesListPage checkNameValidation(String[] names) {
        createTemplateButton.shouldBe(Condition.visible).click();
        for (String name : names) {
            nameInput.setValue(name);
            TestUtils.wait(500);
            if (!nameValidationHint.exists()) {
                TestUtils.wait(500);
                nameInput.getInput().sendKeys("t");
            }
            nameValidationHint.shouldBe(Condition.visible);
        }
        cancelButton.click();
        return this;
    }

    @Step("Поиск шаблона по значению 'value'")
    private OrderTemplatesListPage search(String value) {
        searchInput.setValue(value);
        TestUtils.wait(1000);
        return this;
    }

    @Step("Удаление шаблона '{name}'")
    public OrderTemplatesListPage deleteTemplate(String name) {
        search(name);
        BaseListPage.delete(columnName, name);
        new DeleteDialog().submitAndDelete("Удаление выполнено успешно");
        return this;
    }

    @Step("Проверка, что шаблоны не найдены при поиске по '{value}'")
    public OrderTemplatesListPage checkTemplateNotFound(String value) {
        search(value);
        noDataFound.shouldBe(Condition.visible);
        return this;
    }

    @Step("Поиск и открытие страницы шаблона '{name}'")
    public OrderTemplatePage findAndOpenTemplatePage(String name) {
        search(name);
        new Table(columnName).getRowElementByColumnValue(columnName, name).click();
        TestUtils.wait(600);
        return new OrderTemplatePage();
    }

    @Step("Копирование шаблона '{name}'")
    public OrderTemplatePage copyTemplate(String name) {
        new BaseListPage().copy(columnName, name);
        new Alert().checkText("Шаблон скопирован").checkColor(Alert.Color.GREEN).close();
        TestUtils.wait(500);
        return new OrderTemplatePage();
    }

    @Step("Проверка, что шаблон '{template.name}' найден при поиске по значению '{value}'")
    public OrderTemplatesListPage findTemplateByValue(String value, ItemVisualTemplate template) {
        search(value);
        Assertions.assertTrue(new Table(columnName).isColumnValueEquals(columnName, template.getName()));
        return this;
    }

    @Step("Выбор в фильтре по типу значения '{value}'")
    public OrderTemplatesListPage setTypeFilter(String value) {
        typeDropDown.selectByDivText(value);
        return this;
    }

    @Step("Выбор в фильтре по провайдеру значения '{value}'")
    public OrderTemplatesListPage setProviderFilter(String value) {
        providerDropDown.selectByDivText(value);
        return this;
    }

    @Step("Выбор в фильтре по состоянию значения '{value}'")
    public OrderTemplatesListPage setStateFilter(String value) {
        stateDropDown.selectByDivText(value);
        return this;
    }

    @Step("Применение фильтров")
    public OrderTemplatesListPage applyFilters() {
        applyFiltersButton.click();
        TestUtils.wait(500);
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
        Assertions.assertTrue(new Table(columnName).isColumnValueEquals(columnName, template.getName()));
        return this;
    }

    @Step("Проверка, что шаблон '{template.name}' не отображается в списке")
    public OrderTemplatesListPage checkTemplateIsNotDisplayed(ItemVisualTemplate template) {
        Assertions.assertFalse(new Table(columnName).isColumnValueEquals(columnName, template.getName()));
        return this;
    }
}
