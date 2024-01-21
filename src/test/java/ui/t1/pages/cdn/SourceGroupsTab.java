package ui.t1.pages.cdn;

import io.qameta.allure.Step;
import models.t1.cdn.SourceGroup;
import ui.elements.*;

public class SourceGroupsTab extends AbstractCdnTab {

    private final Button addButton = Button.byText("Добавить");
    private final Button saveButton = Button.byText("Сохранить");
    private final Dialog editDialog = new Dialog("Редактировать группу источников");
    private final DataTable sourceGroupTable = new DataTable("Источники");

    @Step("Создание группы источника CDN")
    public SourceGroupsTab createSourceGroup(SourceGroup sourceGroup) {
        addButton.click();
        Dialog addSourceGroupDialog = Dialog.byTitle("Добавить группу источников");
        addSourceGroupDialog.setInputByName("name", sourceGroup.getName());
        addSourceGroupDialog.setInputValueV2("Доменное имя источника", sourceGroup.getDomainName());
        addSourceGroupDialog.clickButtonByType("submit");
        return this;
    }

    @Step("[Проверка] группа источника с именем: {0} - существует")
    public SourceGroupsTab checkCdnSourceGroupExistByName(String sourceGroupName) {
        new DataTable("Источники").asserts().checkColumnContainsValue("Название", sourceGroupName);
        return this;
    }

    @Step("[Проверка] группа источника с именем: {0} - не существует")
    public SourceGroupsTab checkCdnSourceGroupDoesNotExistByName(String sourceGroupName) {
        sourceGroupTable.asserts().checkColumnNotContainsValue("Название", sourceGroupName);
        return this;
    }

    @Step("[Проверка] у группы источника с именем: {0}, доменное имя содержит: {1}")
    public SourceGroupsTab checkCdnSourceGroupContainsDomainName(String sourceGroupName, String domainName) {
        sourceGroupTable.getRowByColumnIndex(0, sourceGroupName)
                .asserts().checkValueInColumnWithName("Источники", domainName);
        return this;
    }

    @Step("Редактирование доменного имени группы источника")
    public SourceGroupsTab editSourceGroupDomainName(String sourceGroupName, String newDomainName) {
        Menu.byElement(sourceGroupTable.searchAllPages(t -> sourceGroupTable.isColumnValueContains("Название", sourceGroupName))
                        .getRowByColumnValueContains("Название", sourceGroupName)
                        .get()
                        .$x(".//button[@id = 'actions-menu-button']"))
                .select("Редактировать");
        editDialog.setInputValueV2("Доменное имя источника", newDomainName);
        saveButton.click();
        Alert.green("Группа источников успешно отредактирована");
        return this;
    }

    @Step("Удаление группы источника")
    public SourceGroupsTab deleteSourceGroup(String name) {
        deleteCdnEntity(name);
        Dialog.byTitle("Удаление группы источников").clickButton("Удалить");
        Alert.green("Группа источников успешно удалена");
        return this;
    }
}
