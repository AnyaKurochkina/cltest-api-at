package ui.t1.pages.productCatalog.image;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.BaseListPage;
import ui.elements.Table;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImageGroupsListPage extends BaseListPage {

    private final String titleColumn = "Название";
    private final String osVersionColumn = "Версия ОС";
    private final SelenideElement pageTitle = $x("//div[text() = 'Группы образов']");

    public ImageGroupsListPage() {
        pageTitle.shouldBe(Condition.visible);
    }

    @Step("Проверка заголовков списка групп образов")
    public ImageGroupsListPage checkImageGroupsListHeaders() {
        Table imagesTable = new Table(titleColumn);
        assertEquals(0, imagesTable.getHeaderIndex("Имя"));
        assertEquals(1, imagesTable.getHeaderIndex("Название"));
        assertEquals(2, imagesTable.getHeaderIndex("Теги"));
        assertEquals(3, imagesTable.getHeaderIndex("Дистрибутив"));
        assertEquals(4, imagesTable.getHeaderIndex("Дата и время синхронизации"));
        return this;
    }

    @Step("Изменение количества отображаемых строк")
    public ImageGroupsListPage setRecordsPerPage(int number) {
        super.setRecordsPerPage(number);
        Table imagesTable = new Table(titleColumn);
        Assertions.assertTrue(imagesTable.getRows().size() <= number);
        return this;
    }

    @Step("Раскрытие информации по группе образов")
    public ImageGroupsListPage showImageGroupInfo(int rowNumber) {
        Table imagesTable = new Table(titleColumn);
        SelenideElement row = imagesTable.getRowByIndex(rowNumber);
        row.$x(".//td[last()]//button").click();
        Table imageGroupInfoTable = new Table(osVersionColumn);
        assertEquals(0, imageGroupInfoTable.getHeaderIndex("Имя"));
        assertEquals(1, imageGroupInfoTable.getHeaderIndex("Дистрибутив"));
        assertEquals(2, imageGroupInfoTable.getHeaderIndex("Версия ОС"));
        assertEquals(3, imageGroupInfoTable.getHeaderIndex("Теги"));
        assertEquals(4, imageGroupInfoTable.getHeaderIndex("ID"));
        return this;
    }

    @Step("Скрытие информации по группе образов")
    public ImageGroupsListPage hideImageGroupInfo(int rowNumber) {
        Table imagesTable = new Table(titleColumn);
        SelenideElement row = imagesTable.getRowByIndex(rowNumber);
        row.$x(".//td[last()]//button").click();
        row.$x("following::th[text()='" + osVersionColumn + "']").shouldNotBe(Condition.visible);
        return this;
    }
}
