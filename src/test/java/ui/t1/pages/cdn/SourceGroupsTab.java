package ui.t1.pages.cdn;

import io.qameta.allure.Step;
import models.t1.cdn.SourceGroup;
import ui.elements.*;

import java.util.Objects;

public class SourceGroupsTab extends AbstractCdnTab<SourceGroupsTab, SourceGroup> {

    private final Button saveButton = Button.byText("Сохранить");
    private final Dialog editDialog = new Dialog("Редактировать группу источников");
    private final DataTable sourceGroupTable = new DataTable("Источники");

    @Step("Создание группы источника CDN")
    public void create(SourceGroup sourceGroup) {
        addButton.click();
        Dialog addSourceGroupDialog = Dialog.byTitle("Добавить группу источников");
        addSourceGroupDialog.setInputByName("name", sourceGroup.getName());
        addSourceGroupDialog.setInputValueV2("Доменное имя источника", sourceGroup.getDomainName());
        if (Objects.requireNonNull(sourceGroup.getIsReserved(), "Параметр не задан")) {
            addSourceGroupDialog.setRadio(Radio.byValue("Резервный"));
        }
        addSourceGroupDialog.clickButtonByType("submit");
    }

    @Step("[Проверка] у группы источника с именем: {0}, доменное имя содержит: {1}")
    public SourceGroupsTab checkCdnSourceGroupContainsDomainName(String sourceGroupName, String domainName) {
        sourceGroupTable.update().getRowByColumnIndex(0, sourceGroupName)
                .asserts().checkValueInColumnWithName("Источники", domainName);
        return this;
    }

    @Step("Редактирование доменного имени группы источника")
    public SourceGroupsTab editSourceGroupDomainName(String sourceGroupName, String newDomainName) {
        chooseActionFromMenu(sourceGroupName, "Редактировать");
        editDialog.setInputValueV2("Доменное имя источника", newDomainName);
        saveButton.click();
        Alert.green("Группа источников успешно отредактирована");
        return this;
    }

    @Override
    @Step("Удаление группы источника")
    public SourceGroupsTab delete(String name) {
        chooseActionFromMenu(name, "Удалить");
        Dialog.byTitle("Удаление группы источников").clickButton("Удалить");
        return this;
    }

    @Override
    public String getMainTableName() {
        return "Группы источников";
    }
}
