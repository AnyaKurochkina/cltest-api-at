package ui.t1.pages.cdn;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import models.t1.cdn.Resource;
import ui.elements.DataTable;
import ui.elements.Dialog;
import ui.t1.pages.cdn.resource.ResourcePage;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourcesTab extends AbstractCdnTab<ResourcesTab, Resource> {

    private final SelenideElement loader = Selenide.$x("//*[text()='Идет обработка данных']");

    @Override
    @Step("Создание ресурса CDN")
    public void create(Resource resource) {
        addButton.click();
        Dialog addResourceDialog = Dialog.byTitle("Добавить ресурс");
        addResourceDialog.setInputByName("domainName", resource.getDomainName());
        addResourceDialog.setInputByName("hostnames-0", resource.getHostnames().get(0));
        addResourceDialog.clickButton("Создать");
        waitChangeStatus(resource.getHostnames().get(0));
    }

    @Override
    @Step("Удаление ресурса CDN")
    public ResourcesTab delete(String resourceName) {
        chooseActionFromMenu(resourceName, "Удалить");
        Dialog.byTitle("Удаление ресурса CDN").clickButton("Удалить");
        waitUntilLoaderDisappear();
        return this;
    }

    @Step("Переход на страницу ресурса с именем {resourceName}")
    public ResourcePage goToResourcePage(String resourceName) {
        new DataTable("Источники").getRowByColumnValue("Название", resourceName).getElementByColumnIndex(5).click();
        return new ResourcePage();
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

    @Step("Ожидание исчезновения лоадера")
    private void waitUntilLoaderDisappear() {
        loader.shouldBe(Condition.visible.because("Лоадер должен появиться"));
        loader.shouldNotBe(Condition.visible.because("Лоадер должен исчезнуть"));
    }
}
