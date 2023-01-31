package ui.cloud.pages.productCatalog.service;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import io.qameta.allure.Step;
import models.cloud.productCatalog.graph.Graph;
import models.cloud.productCatalog.service.Service;
import org.junit.jupiter.api.Assertions;
import steps.productCatalog.GraphSteps;
import ui.cloud.pages.productCatalog.BaseListPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.*;

import java.util.Arrays;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServicesListPagePC extends BaseListPage {

    private static final String columnName = "Код сервиса";
    private final Input searchInput = Input.byPlaceholder("Поиск");
    private final Select directionDropDown = Select.byLabel("Направление");
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
    private final SelenideElement cancelButton = $x("//div[text()='Отменить']/parent::button");
    private final SelenideElement noDataFound = $x("//td[text()='Нет данных для отображения']");
    private final Select statusDropDown = Select.byLabel("Статус");

    @Step("Проверка заголовков списка сервисов")
    public ServicesListPagePC checkHeaders() {
        Table servicesList = new Table(columnName);
        assertEquals(Arrays.asList("Наименование", columnName, "Дата создания", "Описание", "Статус", "", ""),
                servicesList.getHeaders());
        return this;
    }

    @Step("Проверка сортировки по наименованию")
    public ServicesListPagePC checkSortingByTitle() {
        BaseListPage.checkSortingByStringField("Наименование");
        return this;
    }

    @Step("Проверка сортировки по коду сервиса")
    public ServicesListPagePC checkSortingByName() {
        BaseListPage.checkSortingByStringField(columnName);
        return this;
    }

    @Step("Проверка сортировки по дате создания")
    public ServicesListPagePC checkSortingByCreateDate() {
        BaseListPage.checkSortingByDateField("Дата создания");
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

    @Step("Создание сервиса '{service.name}'")
    public ServicePage createService(Service service) {
        addNewObjectButton.click();
        directionDropDown.set(service.getDirectionName());
        titleInput.setValue(service.getTitle());
        nameInput.setValue(service.getName());
        if (service.getGraphId() != null) {
            Graph graph = GraphSteps.getGraphById(service.getGraphId());
            graphSelect.setContains(graph.getName());
        }
        descriptionInput.setValue(service.getDescription());
        createButton.click();
        Alert.green("Сервис успешно создан");
        return new ServicePage();
    }

    @Step("Проверка валидации обязательных параметров при создании сервиса")
    public ServicesListPagePC checkCreateServiceDisabled(Service service) {
        TestUtils.scrollToTheTop();
        addNewObjectButton.click();
        nameInput.setValue(service.getName());
        titleInput.setValue(service.getTitle());
        descriptionInput.setValue(service.getDescription());
        if (service.getTitle().isEmpty()) {
            titleRequiredFieldHint.shouldBe(Condition.visible);
        }
        if (service.getName().isEmpty()) {
            nameRequiredFieldHint.shouldBe(Condition.visible);
        }
        createButton.getButton().shouldBe(Condition.disabled);
        cancelButton.click();
        return this;
    }

    @Step("Проверка валидации неуникального имени сервиса '{service.name}'")
    public ServicesListPagePC checkNonUniqueNameValidation(Service service) {
        TestUtils.scrollToTheTop();
        addNewObjectButton.click();
        nameInput.setValue(service.getName());
        titleInput.setValue(service.getTitle());
        nonUniqueNameValidationHint.shouldBe(Condition.visible);
        createButton.getButton().shouldBe(Condition.disabled);
        cancelButton.click();
        return this;
    }

    @Step("Проверка валидации недопустимых значений в коде сервиса")
    public ServicesListPagePC checkNameValidation(String[] names) {
        addNewObjectButton.click();
        for (String name : names) {
            nameInput.setValue(name);
            TestUtils.wait(500);
            if (!nameValidationHint.exists()) {
                TestUtils.wait(1000);
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
        BaseListPage.delete(columnName, name);
        new DeleteDialog().inputValidIdAndDelete("Удаление выполнено успешно");
        Assertions.assertTrue(new Table(columnName).isEmpty());
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
        new Table(columnName).getRowByColumnValue(columnName, name).get().click();
        TestUtils.wait(600);
        return new ServicePage();
    }

    @Step("Проверка, что сервис '{service.name}' найден при поиске по значению '{value}'")
    public ServicesListPagePC findServiceByValue(String value, Service service) {
        search(value);
        Assertions.assertTrue(new Table(columnName).isColumnValueEquals(columnName, service.getName()));
        return this;
    }

    @Step("Выбор в фильтре по состоянию значения '{value}'")
    public ServicesListPagePC setStatusFilter(String value) {
        statusDropDown.set(value);
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

    @Step("Проверка, что сервис '{service.name}' отображается в списке")
    public ServicesListPagePC checkServiceIsDisplayed(Service service) {
        Assertions.assertTrue(new Table(columnName).isColumnValueEquals(columnName, service.getName()));
        return this;
    }

    @Step("Проверка, что сервис '{service.name}' не отображается в списке")
    public ServicesListPagePC checkServiceIsNotDisplayed(Service service) {
        Assertions.assertFalse(new Table(columnName).isColumnValueEquals(columnName, service.getName()));
        return this;
    }

    @Step("Копирование сервиса '{service.name}'")
    public ServicesListPagePC copyService(Service service) {
        new BaseListPage().copy(columnName, service.getName());
        Alert.green("Копирование выполнено успешно");
        return this;
    }

    @Step("Сортировка по дате создания")
    public ServicesListPagePC sortByCreateDate() {
        sortByCreateDate.click();
        return this;
    }

    @Step("Переход на последнюю страницу списка")
    public ServicesListPagePC lastPage() {
        super.lastPage();
        return this;
    }

    @Step("Открытие страницы сервиса {name}")
    public ServicePage openServicePage(String name) {
        new Table(columnName).getRowByColumnValue(columnName, name).get().click();
        return new ServicePage();
    }

    @Step("Проверка, что подсвечен сервис 'name'")
    public void checkServiceIsHighlighted(String name) {
        checkRowIsHighlighted(columnName, name);
    }
}
