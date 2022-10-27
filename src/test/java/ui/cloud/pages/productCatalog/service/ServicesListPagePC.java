package ui.cloud.pages.productCatalog.service;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import io.qameta.allure.Step;
import models.productCatalog.Service;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebElement;
import ui.cloud.pages.productCatalog.BaseList;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.pages.productCatalog.template.TemplatesListPage;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.*;
import ui.uiModels.Template;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServicesListPagePC {

    private static final String columnName = "Код сервиса";
    private final Input searchInput = Input.byPlaceholder("Поиск");
    private final SelenideElement createServiceButton = $x("//div[@data-testid = 'add-button']//button");
    private final DropDown directionDropDown = DropDown.byLabel("Направление");
    private final Input titleInput = Input.byName("title");
    private final Input nameInput = Input.byName("name");
    private final TextArea descriptionInput = TextArea.byName("description");
    private final SelenideElement titleRequiredFieldHint =
            $x("//input[@name='title']/following::div[text()='Поле обязательно для заполнения']");
    private final SelenideElement nameRequiredFieldHint =
            $x("//input[@name='name']/following::div[text()='Поле обязательно для заполнения']");
    private final SelenideElement nonUniqueNameValidationHint =
            $x("//input[@name='name']/following::div[text()='Сервис с таким именем уже существует']");
    private final SelenideElement nameValidationHint =
            $x("//div[text()='Поле может содержать только символы: \"a-z\", \"0-9\", \"_\", \"-\", \":\", \".\"']");
    private final SelenideElement createButton = $x("//div[text()='Создать']/parent::button");
    private final SelenideElement cancelButton = $x("//div[text()='Отменить']/parent::button");
    private final SelenideElement noDataFound = $x("//td[text()='Нет данных для отображения']");
    private final DropDown statusDropDown = DropDown.byLabel("Статус");
    private final WebElement applyFiltersButton = $x("//button[div[text()='Применить']]");
    private final WebElement clearFiltersButton = $x("//button[text()='Сбросить фильтры']");

    @Step("Проверка заголовков списка графов")
    public ServicesListPagePC checkHeaders() {
        Table servicesList = new Table(columnName);
        assertEquals(0, servicesList.getHeaderIndex("Наименование"));
        assertEquals(1, servicesList.getHeaderIndex(columnName));
        assertEquals(2, servicesList.getHeaderIndex("Дата создания"));
        assertEquals(3, servicesList.getHeaderIndex("Описание"));
        assertEquals(4, servicesList.getHeaderIndex("Статус"));
        return this;
    }

    @Step("Проверка сортировки по наименованию")
    public ServicesListPagePC checkSortingByTitle() {
        BaseList.checkSortingByStringField("Наименование");
        return this;
    }

    @Step("Проверка сортировки по коду сервиса")
    public ServicesListPagePC checkSortingByName() {
        BaseList.checkSortingByStringField(columnName);
        return this;
    }

    @Step("Проверка сортировки по дате создания")
    public ServicesListPagePC checkSortingByCreateDate() {
        BaseList.checkSortingByDateField("Дата создания");
        return this;
    }

    @Step("Проверка сортировки по состоянию")
    public ServicesListPagePC checkSortingByStatus() {
        String header = "Статус";
        Table table = new Table(columnName);
        SelenideElement columnHeader = StringUtils.$x("//div[text()='{}']/parent::div", header);
        SelenideElement arrowIcon = StringUtils.$x("//div[text()='{}']/following-sibling::*[name()='svg']", header);
        columnHeader.click();
        TestUtils.wait(1000);
        arrowIcon.shouldBe(Condition.visible);
        String firstElementStatus = table.getFirstValueByColumn(header);
        Assertions.assertEquals("Скрыт", firstElementStatus);
        columnHeader.click();
        TestUtils.wait(1000);
        arrowIcon.shouldBe(Condition.visible);
        firstElementStatus = table.getFirstValueByColumn(header);
        Assertions.assertEquals("Опубликован", firstElementStatus);
        return this;
    }

    @Step("Создание сервиса '{service.serviceName}'")
    public ServicePage createService(Service service) {
        createServiceButton.click();
        directionDropDown.selectByTitle(service.getDirectionName());
        titleInput.setValue(service.getTitle());
        nameInput.setValue(service.getServiceName());
        descriptionInput.setValue(service.getDescription());
        createButton.click();
        new Alert().checkText("Сервис успешно создан").checkColor(Alert.Color.GREEN).close();
        return new ServicePage();
    }

    @Step("Проверка валидации обязательных параметров при создании сервиса")
    public ServicesListPagePC checkCreateServiceDisabled(Service service) {
        TestUtils.scrollToTheTop();
        createServiceButton.click();
        nameInput.setValue(service.getServiceName());
        titleInput.setValue(service.getTitle());
        descriptionInput.setValue(service.getDescription());
        if (service.getTitle().isEmpty()) {
            titleRequiredFieldHint.shouldBe(Condition.visible);
        }
        if (service.getServiceName().isEmpty()) {
            nameRequiredFieldHint.shouldBe(Condition.visible);
        }
        createButton.shouldBe(Condition.disabled);
        cancelButton.click();
        return this;
    }

    @Step("Проверка валидации неуникального имени сервиса '{service.serviceName}'")
    public ServicesListPagePC checkNonUniqueNameValidation(Service service) {
        TestUtils.scrollToTheTop();
        createServiceButton.click();
        nameInput.setValue(service.getServiceName());
        titleInput.setValue(service.getTitle());
        nonUniqueNameValidationHint.shouldBe(Condition.visible);
        createButton.shouldBe(Condition.disabled);
        cancelButton.click();
        return this;
    }

    @Step("Проверка валидации недопустимых значений в коде сервиса")
    public ServicesListPagePC checkNameValidation(String[] names) {
        createServiceButton.shouldBe(Condition.visible).click();
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

    @Step("Поиск сервиса по значению 'value'")
    private ServicesListPagePC search(String value) {
        searchInput.setValue(value);
        TestUtils.wait(1000);
        return this;
    }

    @Step("Удаление сервиса '{name}'")
    public ServicesListPagePC deleteService(String name) {
        search(name);
        BaseList.delete(columnName, name);
        new DeleteDialog().inputValidIdAndDelete("Удаление выполнено успешно");
        return this;
    }

    @Step("Проверка, что сервисы не найдены при поиске по '{value}'")
    public ServicesListPagePC checkServiceNotFound(String value) {
        search(value);
        noDataFound.shouldBe(Condition.visible);
        return this;
    }

    @Step("Поиск и открытие страницы сервиса '{name}'")
    public ServicePage findAndOpenServicePage(String name) {
        search(name);
        new Table(columnName).getRowElementByColumnValue(columnName, name).click();
        TestUtils.wait(600);
        return new ServicePage();
    }

    @Step("Проверка, что сервис '{service.serviceName}' найден при поиске по значению '{value}'")
    public ServicesListPagePC findServiceByValue(String value, Service service) {
        search(value);
        Assertions.assertTrue(new Table(columnName).isColumnValueEquals(columnName, service.getServiceName()));
        return this;
    }

    @Step("Выбор в фильтре по состоянию значения '{value}'")
    public ServicesListPagePC setStatusFilter(String value) {
        statusDropDown.selectByDivText(value);
        return this;
    }

    @Step("Применение фильтров")
    public ServicesListPagePC applyFilters() {
        applyFiltersButton.click();
        TestUtils.wait(500);
        return this;
    }

    @Step("Удаление заданного значения фильтра '{value}'")
    public ServicesListPagePC removeFilterTag(String value) {
        TestUtils.scrollToTheTop();
        StringUtils.$x("//span[text()='{}']/following-sibling::*[name()='svg']", value).click();
        TestUtils.wait(500);
        return this;
    }

    @Step("Сброс фильтров")
    public ServicesListPagePC clearFilters() {
        clearFiltersButton.click();
        TestUtils.wait(500);
        return this;
    }

    @Step("Проверка, что сервис '{service.serviceName}' отображается в списке")
    public ServicesListPagePC checkServiceIsDisplayed(Service service) {
        Assertions.assertTrue(new Table(columnName).isColumnValueEquals(columnName, service.getServiceName()));
        return this;
    }

    @Step("Проверка, что сервис '{service.serviceName}' не отображается в списке")
    public ServicesListPagePC checkServiceIsNotDisplayed(Service service) {
        Assertions.assertFalse(new Table(columnName).isColumnValueEquals(columnName, service.getServiceName()));
        return this;
    }

    @Step("Копирование сервиса '{service.serviceName}'")
    public ServicesListPagePC copyService(Service service) {
        new BaseList().copy(columnName, service.getServiceName());
        new Alert().checkText("Копирование выполнено успешно").checkColor(Alert.Color.GREEN).close();
        return this;
    }
}
