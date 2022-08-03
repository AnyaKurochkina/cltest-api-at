package ui.cloud.pages.productCatalog;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.productCatalog.graph.GraphPage;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Table;

import static com.codeborne.selenide.Selenide.$x;

public class AuditPage extends GraphPage {

    private final SelenideElement contextId = $x("//span[text()='ID контекста']/following::span[1]");
    private final SelenideElement address = $x("//span[text()='Адрес']/following::span[1]");
    private final SelenideElement request = $x("//span[text()='Запрос']/ancestor::div[2]");
    private final SelenideElement response = $x("//span[text()='Ответ']/ancestor::div[2]");
    private final SelenideElement showRequest = $x("//input[@name='returnLogBody']");
    private final SelenideElement showResponse = $x("//input[@name='returnLogReplyBody']");
    private final SelenideElement paramsFullView = $x("//div[@role='dialog']//p']");
    private final SelenideElement copyContextIdButton = $x("//tbody//button[@title='Скопировать данные']");
    private final SelenideElement copyAddressButton = $x("//span[text()='Адрес']/following::button[@title='Скопировать'][1]");

    @Step("Проверка первой записи в таблице аудита")
    public AuditPage checkFirstRecord(String dateTime, String user, String operationType, String object, String statusCode, String status) {
        TestUtils.wait(1000);
        checkAuditIsLoaded();
        Table table = new Table("Учетная запись");
        if (!table.hasColumnValueContaining("Тип операции", operationType)) {
            TestUtils.wait(2000);
            Selenide.refresh();
            new GraphPage().goToAuditTab();
            table = new Table("Учетная запись");
        }
        table.getValueByColumnInFirstRow("Дата и время").shouldHave(Condition.text(dateTime));
        table.getValueByColumnInFirstRow("Учетная запись").shouldHave(Condition.text(user));
        table.getValueByColumnInFirstRow("Тип операции").shouldHave(Condition.exactText(operationType));
        table.getValueByColumnInFirstRow("Объект").shouldHave(Condition.exactText(object));
        table.getValueByColumnInFirstRow("Код статуса").shouldHave(Condition.text(statusCode));
        table.getValueByColumnInFirstRow("Статус").shouldHave(Condition.exactText(status));
        return this;
    }

    @Step("Проверка детальных сведений первой записи в таблице аудита")
    public AuditPage checkFirstRecordDetails(String contextId, String address, String request, String response) {
        Table table = new Table("Учетная запись");
        table.getRowByIndex(0).click();
        this.contextId.shouldHave(Condition.exactText(contextId));
        this.address.shouldHave(Condition.text(address));
        if (showRequest.isSelected()) {
            this.request.$x(".//descendant::span[2]").shouldHave(Condition.text(request));
        } else {
            this.request.$x(".//i").shouldHave(Condition.exactText(request));
        }
        if (showResponse.isSelected()) {
            this.response.$x(".//descendant::span[2]").shouldHave(Condition.text(request));
        } else {
            this.response.$x(".//i").shouldHave(Condition.exactText(response));
        }
        table.getRowByIndex(0).click();
        return this;
    }

    @Step("Включение отображения запроса и ответа")
    public AuditPage showRequestAndResponse() {
        showRequest.click();
        showResponse.click();
        return this;
    }

    @Step("Проверка копирования в буфер обмена")
    public AuditPage checkCopyToClipboard() {
        Table table = new Table("Учетная запись");
        table.getRowByIndex(0).click();
        copyContextIdButton.scrollTo().click();
        Assertions.assertTrue(Selenide.clipboard().getText().contains(contextId.getText()));
        copyAddressButton.click();
        Assertions.assertTrue(Selenide.clipboard().getText().equals(address.getText()));
        return this;
    }

    @Step("Перезагрузка страницы, если данные аудита не загрузились")
    private void checkAuditIsLoaded() {
        if ($x("//div[text()='Дата и время']/ancestor::table//td[text()='Нет данных для отображения']").exists()) {
            TestUtils.wait(2000);
            Selenide.refresh();
            new GraphPage().goToAuditTab();
        }
    }

}
