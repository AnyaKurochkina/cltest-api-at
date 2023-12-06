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
    public boolean deleteResource(String resourceName) {
        resourcesTab.switchTo();
        deleteEntity(resourceName);
        Dialog.byTitle("Удаление ресурса CDN").clickButton("Удалить");
        return isEntityExist(resourceName);
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
        return new ResourcesTable().isColumnValueContains("Название", name);
    }

    @Step("Проверка существования ресурса")
    public boolean isEntityExist(String name) {
        Waiting.sleep(2000);
        return new ResourcesTable().isColumnValueEquals("Название", name);
    }

    @Step("Ожидание смены статуса ресурсной записи на Активный")
    public void waitChangeStatus(String resourceName) {
        Waiting.findWithRefresh(() -> !new ResourcesTable().getRowByColumnValue("Название", resourceName)
                .getValueByColumn("Статус")
                .equals("Разворачивается"), Duration.ofMinutes(15));
        String status = new ResourcesTable().getRowByColumnValue("Название", resourceName)
                .getValueByColumn("Статус");
        assertEquals("Активный", status);
    }

    @Step("Переход на страницу ресурса с именем {resourceName}")
    public ResourcePage goToResourcePage(String resourceName) {
        new ResourcesTable().getRowByColumnValue("Название", resourceName).getElementByColumnIndex(5).click();
        return new ResourcePage(resourceName);
    }

    private void deleteEntity(String name) {
        ResourcesTable table = new ResourcesTable();
        Menu.byElement(table.searchAllPages(t -> table.isColumnValueContains("Название", name))
                        .getRowByColumnValueContains("Название", name)
                        .get()
                        .$x(".//button[@id = 'actions-menu-button']"))
                .select("Удалить");
    }

    public static class ResourcesTable extends DataTable {

        public ResourcesTable() {
            super("Источники");
        }
    }

}
