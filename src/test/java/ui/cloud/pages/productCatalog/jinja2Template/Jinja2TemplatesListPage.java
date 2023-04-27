package ui.cloud.pages.productCatalog.jinja2Template;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import models.cloud.productCatalog.jinja2.Jinja2Template;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.BaseListPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.elements.Alert;
import ui.elements.InputFile;
import ui.elements.SearchSelect;
import ui.elements.Table;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Getter
public class Jinja2TemplatesListPage extends BaseListPage {

    private final String nameColumn = "Код шаблона";
    private final SearchSelect typeSelect = SearchSelect.byLabel("Тип");
    private final SearchSelect providerSelect = SearchSelect.byLabel("Провайдер");

    public Jinja2TemplatesListPage() {
        $x("//div[text()='Код шаблона']").shouldBe(Condition.visible);
    }

    @Step("Проверка сортировки списка разрешенных действий")
    public Jinja2TemplatesListPage checkSorting() {
        checkSortingByStringField("Наименование");
        checkSortingByStringField(nameColumn);
        checkSortingByDateField("Дата создания");
        return this;
    }

    @Step("Проверка, что шаблон Jinja2 '{jinja2Template.name}' найден при поиске по значению '{value}'")
    public Jinja2TemplatesListPage findJinja2TemplateByValue(String value, Jinja2Template jinja2Template) {
        search(value);
        Assertions.assertTrue(new Table(nameColumn).isColumnValueEquals(nameColumn, jinja2Template.getName()));
        return this;
    }

    @Step("Проверка отсутствия результатов при поиске по '{value}'")
    public Jinja2TemplatesListPage checkJinja2TemplateNotFound(String value) {
        search(value);
        assertTrue(new Table(nameColumn).isEmpty());
        return this;
    }

    @Step("Удаление шабшлона Jinja2 '{name}' из списка")
    public Jinja2TemplatesListPage delete(String name) {
        delete(nameColumn, name);
        new DeleteDialog().submitAndDelete();
        return this;
    }

    @Step("Открытие страницы шаблона Jinja2 '{name}'")
    public Jinja2TemplatePage openJinja2TemplatePage(String name) {
        new Table(nameColumn).getRowByColumnValue(nameColumn, name).get().click();
        return new Jinja2TemplatePage();
    }

    @Step("Проверка отсутствия результатов при поиске по '{value}'")
    public Jinja2TemplatesListPage checkNoResultsFound(String value) {
        assertTrue(new Table(nameColumn).isEmpty());
        return this;
    }

    @Step("Проверка отображения шаблона Jinja2 '{name}' в списке")
    public boolean isJinja2TemplateDisplayed(String name) {
        Table table = new Table(nameColumn);
        if (table.isColumnValueEquals(nameColumn, name)) return true;
        while (nextPageButtonV2.getButton().isEnabled()) {
            nextPageV2();
            if (table.isColumnValueEquals(nameColumn, name)) return true;
        }
        return false;
    }

    @Step("Поиск и открытие страницы шаблона Jinja2 '{name}'")
    public Jinja2TemplatePage findAndOpenJinja2TemplatePage(String name) {
        search(name);
        new Table(nameColumn).getRowByColumnValue(nameColumn, name).get().click();
        Waiting.sleep(1000);
        return new Jinja2TemplatePage();
    }

    @Step("Открытие страницы создания шаблона Jinja2")
    public Jinja2TemplatePage addNewJinja2Template() {
        addNewObjectButton.click();
        return new Jinja2TemplatePage();
    }

    @Step("Импорт шаблона Jinja2 из файла '{path}'")
    public Jinja2TemplatesListPage importJinja2Template(String path) {
        importButton.click();
        new InputFile(path).importFileAndSubmit();
        Alert.green("Импорт выполнен успешно");
        closeButton.click();
        return this;
    }

    @Step("Копирование шаблона Jinja2 '{jinja2Template.name}'")
    public Jinja2TemplatePage copy(Jinja2Template jinja2Template) {
        copy(nameColumn, jinja2Template.getName());
        Alert.green("Шаблон успешно скопирован");
        return new Jinja2TemplatePage();
    }
}
