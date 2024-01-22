package ui.t1.pages.cdn;

import core.utils.Waiting;
import io.qameta.allure.Step;
import models.t1.cdn.Resource;
import ui.elements.DataTable;
import ui.elements.Dialog;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourcesTab extends AbstractCdnTab {

    @Step("Создание ресурса CDN")
    public ResourcesTab createResource(Resource resource) {
        addButton.click();
        Dialog addResourceDialog = Dialog.byTitle("Добавить ресурс");
        addResourceDialog.setInputByName("domainName", resource.getDomainName());
        addResourceDialog.setInputByName("hostnames-0", resource.getHostnames().get(0));
        addResourceDialog.clickButton("Создать");
        waitChangeStatus(resource.getHostnames().get(0));
        return this;
    }

    @Step("Удаление ресурса CDN")
    public ResourcesTab deleteResource(String resourceName) {
        chooseActionFromMenu(resourceName, "Удалить");
        Dialog.byTitle("Удаление ресурса CDN").clickButton("Удалить");
        return this;
    }

    @Step("[Проверка] существования ресурса")
    public ResourcesTab checkCdnResourceExistByName(String resourceName) {
        new DataTable("Источники").asserts().checkColumnContainsValue("Название", resourceName);
        return this;
    }

    @Step("[Проверка] отсутствия ресурса")
    public ResourcesTab checkThatCdnResourceDoesNotExist(String resourceName) {
        new DataTable("Источники").asserts().checkColumnNotContainsValue("Название", resourceName);
        return this;
    }

    @Step("Переход на страницу ресурса с именем {resourceName}")
    public ResourcePage goToResourcePage(String resourceName) {
        new DataTable("Источники").getRowByColumnValue("Название", resourceName).getElementByColumnIndex(5).click();
        return new ResourcePage(resourceName);
    }

    @Step("Ожидание смены статуса ресурсной записи на Активный")
    public ResourcesTab waitChangeStatus(String resourceName) {
        Waiting.findWithRefresh(() -> !new DataTable("Источники").getRowByColumnValue("Название", resourceName)
                .getValueByColumn("Статус")
                .equals("Разворачивается"), Duration.ofMinutes(15));
        String status = new DataTable("Источники").getRowByColumnValue("Название", resourceName)
                .getValueByColumn("Статус");
        assertEquals("Активный", status);
        return this;
    }
}
