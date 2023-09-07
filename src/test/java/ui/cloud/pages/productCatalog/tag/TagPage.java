package ui.cloud.pages.productCatalog.tag;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.cloud.pages.productCatalog.EntityPage;
import ui.elements.Table;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Getter
public class TagPage extends EntityPage {

    private final SelenideElement tagsListLink = $x("//a[text() = 'Список тегов']");

    public TagPage() {
        tagsListLink.shouldBe(Condition.visible);
    }

    public TagPage checkObject(String type, String title, String name) {
        Table table = new Table("Тип объекта");
        assertTrue(table.isColumnValueEquals("Тип объекта", type));
        assertTrue(table.isColumnValueEquals("Наименование", title));
        assertTrue(table.isColumnValueEquals("Код", name));
        return this;
    }
}