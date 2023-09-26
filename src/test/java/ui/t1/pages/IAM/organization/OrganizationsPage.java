package ui.t1.pages.IAM.organization;

import com.codeborne.selenide.Condition;
import core.exception.NotFoundElementException;
import io.qameta.allure.Step;
import ui.elements.*;
import ui.models.Organization;

import static com.codeborne.selenide.Selenide.$x;

public class OrganizationsPage {

    private final Button createOrg = Button.byText("Создать организацию");
    private final Button create = Button.byText("Создать");
    private final Input orgName = Input.byName("title");
    private final Input ownerEmail = Input.byName("owner");
    private final Input INN = Input.byName("inn");

    public OrganizationsPage() {
        $x("//p[text()='Организации']").shouldBe(Condition.visible);
    }

    @Step("Создание организации")
    public OrganizationsPage createOrganization(Organization org) {
        createOrg.click();
        orgName.setValue(org.getName());
        ownerEmail.setValue(org.getEmail());
        INN.setValue(org.getINN());
        create.click();
        Alert.green("Новая организация создана");
        return this;
    }

    @Step("Удаление организации")
    public OrganizationsPage deleteOrganization(Organization org) {
        String orgName = org.getName();
        OrganizationTable table = new OrganizationTable();
        Menu.byElement(table.searchAllPages(t -> table.isColumnValueEquals(OrganizationTable.COLUMN_NAME, orgName))
                        .getRowByColumnValue(OrganizationTable.COLUMN_NAME, orgName)
                        .getElementByColumnIndex(3)
                        .$("button"))
                .select("Удалить");
        Dialog.byTitle("Удаление организации")
                .clickButton("Удалить");
        Alert.green("Организация удалена");
        return this;
    }

    @Step("Проверка существования организации")
    public boolean isOrgExist(String name) throws NotFoundElementException {
        OrganizationTable table = new OrganizationTable();
        boolean result = false;
        try {
            result = table.searchAllPages(t -> table.isColumnValueEquals(OrganizationTable.COLUMN_NAME, name))
                    .isColumnValueEquals(OrganizationTable.COLUMN_NAME, name);
        } catch (NotFoundElementException ignore) {

        }
        return result;
    }

    private static class OrganizationTable extends DataTable {
        public static final String COLUMN_NAME = "Название";

        public OrganizationTable() {
            super(COLUMN_NAME);
        }
    }
}
