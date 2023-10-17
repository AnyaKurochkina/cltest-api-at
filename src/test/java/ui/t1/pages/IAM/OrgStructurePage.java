package ui.t1.pages.IAM;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.helper.StringUtils;
import core.utils.Waiting;
import io.qameta.allure.Step;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.elements.Alert;
import ui.elements.Button;
import ui.elements.Dialog;
import ui.elements.Table;
import ui.t1.pages.ContextDialog;

import java.util.List;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;

public class OrgStructurePage {
    SelenideElement createAction = StringUtils.$x("//div[text() = 'Создать проект']");
    SelenideElement createFolderAction = StringUtils.$x("//div[text() = 'Создать папку']");
    SelenideElement selectContextAction = StringUtils.$x("//div[text() = 'Выбрать контекст']");
    SelenideElement deleteAction = StringUtils.$x("//div[text() = 'Удалить']");
    SelenideElement editAction = StringUtils.$x("//div[text() = 'Редактировать']");
    Button settings = Button.byXpath("//*[@data-dimension = 's']");
    SelenideElement changeContext = StringUtils.$x("//*[name() = 'path' and @d = 'M5.226 8.56c0-.18.07-.35.21-.48.27-.24.68-.22.92.04l5.74 6.37 5.55-6.41a.65.65 0 01.92-.04c.26.24.28.65.04.92l-5.99 6.9c-.28.31-.76.31-1.04 0L5.396 9a.627.627 0 01-.17-.44z']/parent::*/parent::*");

    public OrgStructurePage() {
        StringUtils.$x("//span[text() = 'Организационная структура']")
                .shouldBe(Condition.visible);
    }

    @Step("Открыть настройки таблицы")
    public TableSettingsDialog openTableSettings() {
        settings.click();
        return new TableSettingsDialog("Настройки таблицы");
    }

    @Step("Переход в модальное окно изменения контекста")
    public ContextDialog goToContextDialog() {
        changeContext.shouldBe(activeCnd).shouldBe(clickableCnd).click();
        return new ContextDialog();
    }

    @Step("Раскрытие списка действий у организации")
    public OrgStructurePage expandOrgActions() {
        new OrgTable().getRow(0).get().$x(".//button[@id='actions-menu-button']").click();
        createAction.shouldBe(Condition.visible).click();
        return this;
    }

    @Step("Проверка отсутсвия действия {actionName} у организации")
    public Boolean isActionExist(String actionName) {
        return StringUtils.$x("//div[text() = '{}']", actionName).exists();
    }

    @Step("Создание проекта с именем {name}")
    public OrgStructurePage createProject(String name) {
        new OrgTable().getRow(0).get().$x(".//button[@id='actions-menu-button']").click();
        createAction.shouldBe(Condition.visible).click();
        Dialog.byTitle("Создание проекта")
                .setInputValue("Наименование", name)
                .clickButton("Создать");
        Alert.green(String.format("Проект \"%s\" создан", name));
        return this;
    }

    @Step("Создание папки с именем {name}")
    public OrgStructurePage createFolder(String name) {
        new OrgTable().getRow(0).get().$x(".//button[@id='actions-menu-button']").click();
        createFolderAction.shouldBe(Condition.visible).click();
        Dialog.byTitle("Создание папки")
                .setInputValue("Наименование", name)
                .clickButton("Создать");
        Alert.green(String.format("Папка \"%s\" создана", name));
        return this;
    }

    @Step("Открыть модальное окно у папки/проекта/организации")
    public ModalWindow openModalWindow(String name) {
        Waiting.sleep(2000);
        Table.Row row = new OrgTable().getRowByColumnValue("Название", name);
        String type = row.getValueByColumn("Тип");
        row.get().click();
        return new ModalWindow(String.format("%s  \"%s\"", type, name));
    }

    @Step("Выбрать контекст")
    public OrgStructurePage selectContext(String name) {
        new OrgTable().getRowByColumnValue("Название", name)
                .get().$x(".//button[@id='actions-menu-button']")
                .click();
        selectContextAction.shouldBe(Condition.visible).click();
        Alert.green(String.format("Выбран контекст: Папка \"%s\"", name));
        Waiting.sleep(5000);
        return this;
    }

    @Step("Удаление папки с именем {name}")
    public OrgStructurePage deleteFolder(String name) {
        new OrgTable().getRowByColumnValue("Название", name)
                .get().$x(".//button[@id='actions-menu-button']")
                .click();
        deleteAction.shouldBe(Condition.visible).click();
        new DeleteDialog()
                .inputValidIdAndDelete(String.format("Папка \"%s\" удалена", name));
        Selenide.refresh();
        Alert.green("Выбран контекст:");
        return this;
    }

    @Step("Создание проекта в структуре папки")
    public OrgStructurePage createProjectInFolder(String folderName, String name) {
        Waiting.sleep(2000);
        new OrgTable().getRowByColumnValue("Название", folderName)
                .get()
                .$x(".//button[@id='actions-menu-button']")
                .click();
        createAction.shouldBe(Condition.visible).click();
        Dialog.byTitle("Создание проекта")
                .setInputValue("Наименование", name)
                .clickButton("Создать");
        Alert.green(String.format("Проект \"%s\" создан", name));
        return this;
    }

    @Step("Изменение имени у проекта {name}")
    public OrgStructurePage changeNameProject(String name, String newName) {
        new OrgTable().getRowByColumnValue("Название", name)
                .get()
                .$x(".//button[@id='actions-menu-button']")
                .click();
        editAction.shouldBe(Condition.visible).click();
        Dialog.byTitle("Редактирование проекта")
                .setInputValue("Наименование", newName)
                .clickButton("Применить");
        Alert.green(String.format("Проект \"%s\" изменен", newName));
        return this;
    }

    @Step("Изменение имени у папки {name}")
    public OrgStructurePage changeNameFolder(String name, String newName) {
        new OrgTable().getRowByColumnValue("Название", name)
                .get()
                .$x(".//button[@id='actions-menu-button']")
                .click();
        editAction.shouldBe(Condition.visible).click();
        Dialog.byTitle("Редактирование папки")
                .setInputValue("Наименование", newName)
                .clickButton("Применить");
        Alert.green(String.format("Папка \"%s\" изменена", newName));
        return this;
    }

    @Step("Удаление проекта с именем {name}")
    public OrgStructurePage deleteProject(String name) {
        new OrgTable().getRowByColumnValue("Название", name)
                .get().$x(".//button[@id='actions-menu-button']")
                .click();
        deleteAction.shouldBe(Condition.visible).click();
        new DeleteDialog()
                .inputInvalidId("dsfsdf")
                .inputValidIdAndDelete(String.format("Проект \"%s\" удален", name));
        return this;
    }

    @Step("Получение идентификатора у ресурса {name}")
    public String getResourceId(String name) {
        return new OrgTable().getRowByColumnValue("Название", name)
                .getValueByColumn("Идентификатор");
    }

    @Step("Получение заголовков таблицы")
    public List<String> getTableHeaders() {
        return new OrgTable().getNotEmptyHeaders();
    }

    @Step("Проверка существования проекта с именем {name} в папке {folder}")
    public boolean isProjectExist(String name) {
        return new OrgTable().isColumnValueEquals("Название", name);
    }

    @Step("Проверка существования проекта с именем {projectName} в папке {folder}")
    public boolean isProjectExistInFolder(String projectName, String folder) {
        OrgTable table = new OrgTable();
        SelenideElement element = table.getRowByColumnValue("Название", folder).get().$x(".//button[@aria-label = 'expand row']");
        if (element.getAttribute("aria-expanded") == null) {
            element.click();
        }
        return table.isColumnValueEquals("Название", projectName);
    }

    @Step("Проверка существования папки с именем {name}")
    public boolean isFolderExist(String name) {
        return new OrgTable().isColumnValueEquals("Название", name);
    }

    private static class OrgTable extends Table {

        public OrgTable() {
            super("Название");
        }
    }
}
