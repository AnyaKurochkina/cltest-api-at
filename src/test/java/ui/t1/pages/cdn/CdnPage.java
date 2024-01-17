package ui.t1.pages.cdn;

import com.codeborne.selenide.Condition;
import core.utils.Waiting;
import io.qameta.allure.Step;
import models.t1.cdn.Resource;
import ui.elements.*;

import java.time.Duration;

import static core.helper.StringUtils.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CdnPage {

    private final Button addButton = Button.byText("Добавить");
    private final Tab sourceGroupsTab = Tab.byText("Группы источников");
    private final Tab resourcesTab = Tab.byText("Ресурсы");

    public CdnPage() {
        $x("//div[text() = 'CDN']").shouldBe(Condition.visible);
    }

    @Step("Создание ресурса CDN")
    public CdnPage createResource(Resource resource) {
        addButton.click();
        Dialog addResourceDialog = Dialog.byTitle("Добавить ресурс");
        addResourceDialog.setInputByName("domainName", resource.getDomainName());
        addResourceDialog.setInputByName("hostnames-0", resource.getHostnames().get(0));
        addResourceDialog.clickButton("Создать");
        waitChangeStatus(resource.getHostnames().get(0));
        return this;
    }

    @Step("Удаление ресурса CDN")
    public CdnPage deleteResource(String resourceName) {
        resourcesTab.switchTo();
        deleteEntity(resourceName);
        Dialog.byTitle("Удаление ресурса CDN").clickButton("Удалить");
        return this;
    }

    @Step("Создание группы источника CDN")
    public CdnPage createSourceGroup(String name, String sourceDomainName) {
        sourceGroupsTab.switchTo();
        addButton.click();
        Dialog addSourceGroupDialog = Dialog.byTitle("Добавить группу источников");
        addSourceGroupDialog.setInputByName("name", name);
        addSourceGroupDialog.setInputValueV2("Доменное имя источника", sourceDomainName);
        addSourceGroupDialog.clickButtonByType("submit");
        Alert.green("Группа источников успешно добавлена");
        return this;
    }

    @Step("Удаление группы источника")
    public boolean deleteSourceGroup(String name) {
        sourceGroupsTab.switchTo();
        deleteEntity(name);
        Dialog.byTitle("Удаление группы источников").clickButton("Удалить");
        Alert.green("Группа источников успешно удалена");
        Waiting.sleep(1000);
        return new DataTable("Источники").isColumnValueContains("Название", name);
    }

    @Step("Проверка существования ресурса")
    public CdnPage checkCdnResourceExistByName(String resourceName) {
        new DataTable("Источники").asserts().checkColumnContainsValue("Название", resourceName);
        return this;
    }

    @Step("Проверка отсутствия ресурса")
    public CdnPage checkThatCdnResourceDoesNotExist(String resourceName) {
        new DataTable("Источники").asserts().checkColumnNotContainsValue("Название", resourceName);
        return this;
    }

    @Step("Ожидание смены статуса ресурсной записи на Активный")
    public CdnPage waitChangeStatus(String resourceName) {
        Waiting.findWithRefresh(() -> !new DataTable("Источники").getRowByColumnValue("Название", resourceName)
                .getValueByColumn("Статус")
                .equals("Разворачивается"), Duration.ofMinutes(15));
        String status = new DataTable("Источники").getRowByColumnValue("Название", resourceName)
                .getValueByColumn("Статус");
        assertEquals("Активный", status);
        return this;
    }

    @Step("Переход на страницу ресурса с именем {resourceName}")
    public ResourcePage goToResourcePage(String resourceName) {
        new DataTable("Источники").getRowByColumnValue("Название", resourceName).getElementByColumnIndex(5).click();
        return new ResourcePage(resourceName);
    }

    private void deleteEntity(String name) {
        DataTable table = new DataTable("Источники");
        Menu.byElement(table.searchAllPages(t -> table.isColumnValueContains("Название", name))
                        .getRowByColumnValueContains("Название", name)
                        .get()
                        .$x(".//button[@id = 'actions-menu-button']"))
                .select("Удалить");
    }
}
