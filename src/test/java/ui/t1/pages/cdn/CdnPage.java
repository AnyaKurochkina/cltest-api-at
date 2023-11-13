package ui.t1.pages.cdn;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import io.qameta.allure.Step;
import models.t1.cdn.Resource;
import ui.elements.Button;
import ui.elements.DataTable;
import ui.elements.Dialog;

import java.time.Duration;

import static core.helper.StringUtils.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CdnPage {

    Button addResources = Button.byText("Добавить");

    public CdnPage() {
        $x("//div[text() = 'CDN']").shouldBe(Condition.visible);
    }

    @Step("Создание ресурса CDN")
    public CdnPage createResource(Resource resource) {
        addResources.click();
        Dialog addResourceDialog = Dialog.byTitle("Добавить ресурс");
        addResourceDialog.setInputByName("domainName", resource.getDomainName());
        addResourceDialog.setInputByName("hostnames-0", resource.getHostName());
        addResourceDialog.clickButton("Создать");
        Waiting.findWithRefresh(() -> !new ResourcesTable().getRowByColumnValue("Название", resource.getHostName())
                .getValueByColumn("Статус")
                .equals("Разворачивается"), Duration.ofMinutes(15));
        String status = new ResourcesTable().getRowByColumnValue("Название", resource.getHostName())
                .getValueByColumn("Статус");
        assertEquals("Активный", status);
        return this;
    }

    @Step("Проверка существования ресурса")
    public boolean isResourceExist(String name) {
        return new ResourcesTable().isColumnValueEquals("Название", name);
    }

    public static class ResourcesTable extends DataTable {

        public ResourcesTable() {
            super("Источники");
        }
    }

}
