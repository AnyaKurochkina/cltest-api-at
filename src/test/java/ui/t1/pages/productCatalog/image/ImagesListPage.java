package ui.t1.pages.productCatalog.image;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.BaseListPage;
import ui.elements.Button;
import ui.elements.Table;

import static com.codeborne.selenide.Selenide.$x;
import static core.helper.StringUtils.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImagesListPage extends BaseListPage {

    private final String imageNameColumn = "Имя";
    private final SelenideElement pageTitle = $x("//div[text() = 'Образы']");
    private final Button closeInfoButton = Button.byAriaLabel("close");

    public ImagesListPage() {
        pageTitle.shouldBe(Condition.visible);
    }

    @Step("Проверка заголовков списка образов")
    public ImagesListPage checkImagesListHeaders() {
        Table imagesTable = new Table(imageNameColumn);
        assertEquals(0, imagesTable.getHeaderIndex("Имя"));
        assertEquals(1, imagesTable.getHeaderIndex("ID Marketplace"));
        assertEquals(2, imagesTable.getHeaderIndex("ID платформы"));
        assertEquals(3, imagesTable.getHeaderIndex("Дата создания"));
        assertEquals(4, imagesTable.getHeaderIndex("Дата изменения"));
        assertEquals(5, imagesTable.getHeaderIndex("Дистрибутив"));
        assertEquals(6, imagesTable.getHeaderIndex("Версия"));
        assertEquals(7, imagesTable.getHeaderIndex("Архитектура"));
        assertEquals(8, imagesTable.getHeaderIndex("Зона доступности"));
        assertEquals(9, imagesTable.getHeaderIndex("Статус"));
        assertEquals(10, imagesTable.getHeaderIndex("Теги"));
        assertEquals(11, imagesTable.getHeaderIndex("Информация"));
        return this;
    }

    @Step("Проверка номера страницы")
    public ImagesListPage checkPageNumber(int number) {
        super.checkPageNumber(number);
        return this;
    }

    @Step("Переход на следующую страницу списка")
    public ImagesListPage goToNextPage() {
        nextPageButtonV2.click();
        return this;
    }

    @Step("Изменение количества отображаемых строк")
    public ImagesListPage setRecordsPerPage(int number) {
        super.setRecordsPerPage(number);
        Table imagesTable = new Table(imageNameColumn);
        Assertions.assertTrue(imagesTable.getRows().size() <= number);
        return this;
    }

    @Step("Просмотр образа с маркетинговой информацией")
    public ImagesListPage openImageInfoWithMarketingInfo() {
        SelenideElement image = null;
        Table imagesTable = new Table(imageNameColumn);
        for (SelenideElement row : imagesTable.getRows()) {
            if (row.$x(".//a[contains(@href,'/meccano/marketing')]").exists()) {
                image = row;
                break;
            }
        }
        image.$x(".//td[last()]//*[name()='svg']").click();
        return this;
    }

    @Step("Просмотр образа без маркетинговой информацией")
    public ImagesListPage openImageInfoWithoutMarketingInfo() {
        SelenideElement image = null;
        Table imagesTable = new Table(imageNameColumn);
        for (SelenideElement row : imagesTable.getRows()) {
            if (!row.$x(".//a[contains(@href,'/meccano/marketing')]").exists()) {
                image = row;
                break;
            }
        }
        image.$x(".//td[last()]//*[name()='svg']").click();
        return this;
    }

    @Step("Проверка, что информация об образе содержит '{value}'")
    public ImagesListPage checkImageInfoContains(String value) {
        $x("//strong[contains(text(),'{}')]", value).shouldBe(Condition.visible);
        closeInfoButton.click();
        return this;
    }
}
