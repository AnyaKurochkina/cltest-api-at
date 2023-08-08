package ui.t1.pages.IAM.organization;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import ui.elements.Alert;
import ui.elements.Button;
import ui.elements.DataTable;
import ui.elements.Input;
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
        Alert.green("Организация успешно создана");
        return this;
    }

    @Step("Проверка существования организации")
    public boolean isOrgExist(String name) {
        return new OrganizationTable().isColumnValueEquals(OrganizationTable.COLUMN_NAME, name);
    }

    private static class OrganizationTable extends DataTable {
        public static final String COLUMN_NAME = "Название";

        public OrganizationTable() {
            super(COLUMN_NAME);
        }
    }
}
