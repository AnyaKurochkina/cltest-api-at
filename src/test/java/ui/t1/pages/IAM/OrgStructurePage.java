package ui.t1.pages.IAM;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import ui.cloud.pages.productCatalog.DeleteDialog;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.Table;

import static com.codeborne.selenide.Selenide.$x;

public class OrgStructurePage {
    SelenideElement createAction = $x("//div[text() = 'Создать проект']");
    SelenideElement createFolderAction = $x("//div[text() = 'Создать папку']");
    SelenideElement selectContextAction = $x("//div[text() = 'Выбрать контекст']");
    SelenideElement deleteAction = $x("//div[text() = 'Удалить']");
    SelenideElement editAction = $x("//div[text() = 'Редактировать']");

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
        String type = new OrgTable().getRowByColumnValue("Название", name).getValueByColumn("Тип");
        new OrgTable().getRowByColumnValue("Название", name)
                .get()
                .click();
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

    @Step("Проверка существования проекта с именем {name}")
    public boolean isProjectExist(String name) {
        return new OrgTable().isColumnValueEquals("Название", name);
    }

    @Step("Проверка существования папки с именем {name}")
    public boolean isFolderExist(String name) {
        return new OrgTable().isColumnValueEquals("Название", name);
    }

    private static class OrgTable extends Table {

        public OrgTable() {
            super($x("//table[thead/tr/td[.='Название']]"));
        }
    }
}
