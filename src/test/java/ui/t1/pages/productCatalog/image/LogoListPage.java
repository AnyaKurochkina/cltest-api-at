package ui.t1.pages.productCatalog.image;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.BaseListPage;
import ui.elements.Table;

import java.util.Arrays;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogoListPage extends BaseListPage {

    private final String nameColumn = "Имя";
    private final SelenideElement pageTitle = $x("//div[text() = 'Логотипы']");

    public LogoListPage() {
        pageTitle.shouldBe(Condition.visible);
    }

    @Step("Проверка заголовков списка логотипов")
    public LogoListPage checkHeaders() {
        Table table = new Table(nameColumn);
        assertEquals(Arrays.asList("Имя", "Дистрибутив"),
                table.getNotEmptyHeaders());
        return this;
    }

    @Step("Изменение количества отображаемых строк")
    public LogoListPage setRecordsPerPage(int number) {
        super.setRecordsPerPage(number);
        Table table = new Table(nameColumn);
        Assertions.assertTrue(table.getRows().size() <= number);
        return this;
    }
}
