package ui.cloud.pages.productCatalog.template;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.AssertUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.Getter;
import models.cloud.productCatalog.template.Template;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.BaseListPage;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.elements.Alert;
import ui.elements.Button;
import ui.elements.InputFile;
import ui.elements.Table;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Getter
public class TemplatesListPage extends BaseListPage {

    public static final Button goToUsageButton = Button.byText("Перейти в использование");
    public static final String nameColumn = "Код шаблона";
    private final SelenideElement pageTitle = $x("//div[text() = 'Шаблоны узлов']");
    private final SelenideElement createTemplateButton = $x("//div[@data-testid = 'add-button']//button");

    public TemplatesListPage() {
        pageTitle.shouldBe(Condition.visible);
    }

    @Step("Создание шаблона узлов '{template.name}'")
    public TemplatePage createTemplate(Template template) {
        createTemplateButton.click();
        return new TemplatePage().createTemplate(template);
    }

    @Step("Проверка, что шаблон '{template.name}' найден при поиске по значению '{value}'")
    public TemplatesListPage findTemplateByValue(String value, Template template) {
        search(value);
        Assertions.assertTrue(new Table(nameColumn).isColumnValueEquals(nameColumn, template.getName()));
        return this;
    }

    @Step("Проверка, что шаблоны не найдены при поиске по '{value}'")
    public TemplatesListPage checkTemplateNotFound(String value) {
        search(value);
        assertTrue(new Table(nameColumn).isEmpty());
        return this;
    }

    @Step("Удаление шаблона '{name}'")
    public TemplatesListPage deleteTemplate(String name) {
        search(name);
        BaseListPage.delete(nameColumn, name);
        new DeleteDialog().inputValidIdAndDelete();
        return this;
    }

    @Step("Проверка заголовков списка шаблонов")
    public TemplatesListPage checkHeaders() {
        AssertUtils.assertHeaders(new Table(nameColumn),
                "Наименование", nameColumn, "Дата создания", "Описание", "", "");
        return this;
    }

    @Step("Проверка валидации некорректных параметров при создании шаблона")
    public TemplatesListPage checkCreateTemplateDisabled(Template template) {
        createTemplateButton.scrollIntoView(false).click();
        return new TemplatePage().checkCreateTemplateDisabled(template);
    }

    @Step("Проверка валидации неуникального имени шаблона узла '{template.name}'")
    public TemplatesListPage checkNonUniqueNameValidation(Template template) {
        createTemplateButton.scrollIntoView(false).click();
        return new TemplatePage().checkNonUniqueNameValidation(template);
    }

    @Step("Открытие страницы шаблона '{name}'")
    public TemplatePage openTemplatePage(String name) {
        new Table(nameColumn).getRowByColumnValue(nameColumn, name).get().click();
        return new TemplatePage();
    }

    @Step("Проверка валидации недопустимых значений в коде шаблона")
    public TemplatesListPage checkTemplateNameValidation(String[] names) {
        createTemplateButton.shouldBe(Condition.visible).click();
        return new TemplatePage().checkTemplateNameValidation(names);
    }

    @Step("Проверка сортировки по наименованию")
    public TemplatesListPage checkSortingByTitle() {
        BaseListPage.checkSortingByStringField("Наименование");
        return this;
    }

    @Step("Проверка сортировки по коду шаблона")
    public TemplatesListPage checkSortingByName() {
        BaseListPage.checkSortingByStringField(nameColumn);
        return this;
    }

    @Step("Проверка сортировки по дате создания")
    public TemplatesListPage checkSortingByCreateDate() {
        BaseListPage.checkSortingByDateField("Дата создания");
        return this;
    }

    @Step("Сортировка по дате создания")
    public TemplatesListPage sortByCreateDate() {
        sortByCreateDate.click();
        return this;
    }

    @Step("Переход на последнюю страницу списка")
    public TemplatesListPage lastPage() {
        super.lastPage();
        return this;
    }

    @Step("Проверка, что подсвечен шаблон 'name'")
    public void checkTemplateIsHighlighted(String name) {
        checkRowIsHighlighted(nameColumn, name);
    }

    @Step("Поиск и открытие страницы шаблона '{name}'")
    public TemplatePage findAndOpenTemplatePage(String name) {
        search(name);
        new Table(nameColumn).getRowByColumnValue(nameColumn, name).get().click();
        Waiting.sleep(1000);
        return new TemplatePage();
    }

    @Step("Копирование шаблона '{name}'")
    public TemplatesListPage copy(String name) {
        new BaseListPage().copy(nameColumn, name);
        Alert.green("Копирование выполнено успешно");
        return this;
    }

    @Step("Импорт шаблона из файла '{path}'")
    public TemplatesListPage importTemplate(String path) {
        importButton.click();
        new InputFile(path).importFileAndSubmit();
        Alert.green("Импорт выполнен успешно");
        closeButton.click();
        return this;
    }
}