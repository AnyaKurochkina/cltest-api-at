package ui.t1.pages.cloudDirector;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Step;
import lombok.Getter;
import models.cloud.authorizer.Organization;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.Alert;
import ui.elements.Button;
import ui.elements.DataTable;
import ui.elements.Dialog;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class CloudDirectorPage {
    private final Button createButton;
    public static String PREFIX;

    public CloudDirectorPage() {
        createButton = new Button($x("//button[.='создать VMware организацию']"));
        Organization org = Organization.builder().build().createObject();
        PREFIX = org.getName() + "-";
    }

    @Step("Создание VMware организации с именем {name}")
    public String create(String name) {
      //  createButton.click();
        new VmWareOrganizationList().clickAdd();
        String organizationName = PREFIX + name;
        Dialog dialog = Dialog.byTitle("Создать VMware организацию");
        dialog.setInputValue("Название", name);
        dialog.clickButton("Создать");
        dialog.getDialog().shouldNotBe(Condition.visible);
        TestUtils.wait(2000);
        Assertions.assertTrue(new VmWareOrganizationList().isColumnValueEquals(VmWareOrganizationList.ORGANIZATION_NAME, organizationName)
                , "Не найдена организация с названием " + organizationName);
        return organizationName;
    }

    public void createWithExistName(String name) {
        new VmWareOrganizationList().clickAdd();
        Dialog dialog = Dialog.byTitle("Создать VMware организацию");
        dialog.setInputValue("Название", name);
        dialog.clickButton("Создать");
        TestUtils.wait(1000);
        Alert.red("Организация с таким именем уже существует в vDC");
        dialog.getDialog().shouldBe(Condition.visible);
    }

    @Step("Удаление VMware организации с именем {name}")
    public void delete(String name) {
        new VmWareOrganizationList().getRowByColumnValue(VmWareOrganizationList.ORGANIZATION_NAME, name)
                .getElementByColumn("").$x("descendant::*[name()='svg'][1]").click();
        Dialog dialog = Dialog.byTitle("Удаление");
        dialog.setInputValue("Идентификатор", dialog.getDialog().find("b").innerText());
        dialog.clickButton("Удалить");
        Alert.green("VMware организация {} удалена успешно", name);
    }

    public VMwareOrganizationPage goToOrganization(String name) {
        new VmWareOrganizationList().getRowByColumnValue(VmWareOrganizationList.ORGANIZATION_NAME, name).get().click();
        return new VMwareOrganizationPage();
    }

    private static class VmWareOrganizationList extends DataTable {
        public static final String ORGANIZATION_NAME = "Название организации";

        public VmWareOrganizationList() {
            super(ORGANIZATION_NAME);
        }
    }
}
