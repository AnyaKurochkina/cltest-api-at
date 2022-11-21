package ui.t1.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.Table;

import static com.codeborne.selenide.Selenide.$x;

@Getter
public class CloudDirectorPage {
    final SelenideElement btnCreateVmOrganization = $x("//button[.='создать VMware организацию']");
    public static final String ORGANIZATION_NAME = "Название организации";
    public static final String PREFIX = "t1-cloud-";

    public String create(String name){
        btnCreateVmOrganization.click();
        String organizationName = PREFIX + name;
        Dialog dialog = Dialog.byTitle("Создать VMware организацию");
        dialog.setInputValue("Название", name);
        dialog.clickButton("Создать");
        dialog.getDialog().shouldNotBe(Condition.visible);
        Assertions.assertTrue(new VmWareOrganizationList().isColumnValueEquals(ORGANIZATION_NAME, organizationName)
                , "Не найдена организация с названием " + organizationName);
        return organizationName;
    }

    public void delete(String name){
        new VmWareOrganizationList().getRowByColumnValue(ORGANIZATION_NAME, name).getElementByColumn("").$x("descendant::*[name()='svg'][1]").click();
        Dialog dialog = Dialog.byTitle("Удаление");
        dialog.setInputValue("Идентификатор", dialog.getDialog().find("b").innerText());
        dialog.clickButton("Удалить");
        new Alert().checkText("VMware организация {} удалена успешно", name).checkColor(Alert.Color.GREEN).close();
    }

    private static class VmWareOrganizationList extends Table{
        public VmWareOrganizationList() {
            super(ORGANIZATION_NAME);
        }
    }
}
