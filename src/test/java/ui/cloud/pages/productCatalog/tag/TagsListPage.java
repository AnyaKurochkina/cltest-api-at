package ui.cloud.pages.productCatalog.tag;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import models.cloud.productCatalog.tag.Tag;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.EntityListPage;
import ui.elements.Table;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Getter
public class TagsListPage extends EntityListPage {

    private final String nameColumn = "Наименование";
    private final SelenideElement emptyNameHint = $x("//*[text()='Тег не должен быть пустым']");
    private final SelenideElement nonUniqueNameHint = $x("//*[text()='Тег с таким именем уже существует']");

    @Step("Проверка, что тег '{tag.name}' найден при поиске по значению '{value}'")
    public TagsListPage findTagByValue(String value, Tag tag) {
        search(value);
        Assertions.assertTrue(new Table(nameColumn).isColumnValueEquals(nameColumn, tag.getName()));
        return this;
    }

    @Step("Удаление тега из списка")
    public TagsListPage openDeleteDialog(String name) {
        new Table(nameColumn).getRowByColumnValue(nameColumn, name).get().$x(".//button").click();
        return this;
    }

    @Step("Открытие страницы тега")
    public TagPage openTagPage(String name) {
        new Table(nameColumn).getRowByColumnValue(nameColumn, name).get().click();
        return new TagPage();
    }

    @Step("Проверка отсутствия результатов при поиске по '{value}'")
    public TagsListPage checkTagNotFound(String value) {
        search(value);
        assertTrue(new Table(nameColumn).isEmpty());
        return this;
    }
}