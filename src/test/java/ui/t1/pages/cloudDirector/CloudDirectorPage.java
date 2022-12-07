package ui.t1.pages.cloudDirector;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import ui.elements.*;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class CloudDirectorPage {
    private final Button createButton;
    public static final String PREFIX = "t1-cloud-";

    public CloudDirectorPage() {
        createButton = new Button($x("//button[.='создать VMware организацию']"));
    }

    public String create(String name) {
        createButton.click();
        String organizationName = PREFIX + name;
        Dialog dialog = Dialog.byTitle("Создать VMware организацию");
        dialog.setInputValue("Название", name);
        dialog.clickButton("Создать");
        dialog.getDialog().shouldNotBe(Condition.visible);
        Assertions.assertTrue(new VmWareOrganizationList().isColumnValueEquals(VmWareOrganizationList.ORGANIZATION_NAME, organizationName)
                , "Не найдена организация с названием " + organizationName);
        return organizationName;
    }

    public void createWithExistName(String name) {
        new VmWareOrganizationList().clickAdd();
        Dialog dialog = Dialog.byTitle("Создать VMware организацию");
        dialog.setInputValue("Название", name);
        dialog.clickButton("Создать");
        new Alert().checkText("Организация с таким именем уже существует в vDC").checkColor(Alert.Color.RED);
        dialog.getDialog().shouldBe(Condition.visible);
    }

    public void delete(String name) {
        new VmWareOrganizationList().getRowByColumnValue(VmWareOrganizationList.ORGANIZATION_NAME, name)
                .getElementByColumn("").$x("descendant::*[name()='svg'][1]").click();
        Dialog dialog = Dialog.byTitle("Удаление");
        dialog.setInputValue("Идентификатор", dialog.getDialog().find("b").innerText());
        dialog.clickButton("Удалить");
        new Alert().checkText("VMware организация {} удалена успешно", name).checkColor(Alert.Color.GREEN).close();
    }

    public VMwareOrganizationPage goToOrganization(String name) {
        new VmWareOrganizationList().getRowElementByColumnValue(VmWareOrganizationList.ORGANIZATION_NAME, name).click();
        return new VMwareOrganizationPage();
    }

    private static class VmWareOrganizationList extends DataTable {
        public static final String ORGANIZATION_NAME = "Название организации";

        public VmWareOrganizationList() {
            super(ORGANIZATION_NAME);
        }
    }
}
