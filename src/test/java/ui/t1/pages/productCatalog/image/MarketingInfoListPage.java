package ui.t1.pages.productCatalog.image;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.BaseListPage;
import ui.elements.Table;

import static com.codeborne.selenide.Selenide.$x;
import static core.helper.StringUtils.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MarketingInfoListPage extends BaseListPage {

    private final String nameColumn = "Имя";
    private final SelenideElement pageTitle = $x("//div[text() = 'Маркетинговая информация']");

    public MarketingInfoListPage() {
        pageTitle.shouldBe(Condition.visible);
    }

    @Step("Проверка заголовков списка маркетинговой информации")
    public MarketingInfoListPage checkHeaders() {
        Table table = new Table(nameColumn);
        assertEquals(0, table.getHeaderIndex("Имя"));
        assertEquals(1, table.getHeaderIndex("Идентификатор"));
        assertEquals(2, table.getHeaderIndex("На поддержке"));
        return this;
    }

    @Step("Изменение количества отображаемых строк")
    public MarketingInfoListPage setRecordsPerPage(int number) {
        super.setRecordsPerPage(number);
        Table table = new Table(nameColumn);
        Assertions.assertTrue(table.getRows().size() <= number);
        return this;
    }

    @Step("Просмотр маркетинговой информации, содержащей в имени '{name}'")
    public MarketingInfoListPage view(String name) {
        Table table = new Table(nameColumn);
        SelenideElement row = table.getRowByColumnValueContains(nameColumn, name).get();
        row.$x(".//a[contains(@href,'/marketing/')]").click();
        return this;
    }

    @Step("Проверка содержания '{value}' в заголовке при просмотре маркетинговой информации")
    public MarketingInfoListPage checkInfoTitleContains(String value) {
        $x("//div[contains(text(),'{}')]", value).shouldBe(Condition.visible);
        return this;
    }
}
