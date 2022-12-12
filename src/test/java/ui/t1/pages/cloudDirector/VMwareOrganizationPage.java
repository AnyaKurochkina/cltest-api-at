package ui.t1.pages.cloudDirector;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.*;

import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VMwareOrganizationPage {
    SelenideElement roleDropDown = $x("//*[@id ='mui-component-select-role_id']");
    Button usersTab;
    Button dataCentreTab;

    public VMwareOrganizationPage() {
        SelenideElement selenideElement = $x("//*[text() = 'VMware организация']");
        selenideElement.shouldBe(Condition.visible);
        dataCentreTab = new Button($x("//button[. = 'Виртуальный дата-центр']"));
        usersTab = new Button($x("//button[. = 'Пользователи']"));
    }

    @Step("Переход во вкладку Пользователи")
    public VMwareOrganizationPage goToUsers() {
        usersTab.click();
        return this;
    }

    @Step("Создание пользователя")
    public VMwareOrganizationPage addUser(String login, String role, String password) {
        if (new UsersTable().isColumnValueEquals(UsersTable.COLUMN_NAME, login)) {
            return this;
        }
        new UsersTable().clickAdd();
        Dialog.byTitle("Добавить пользователя")
                .setInputValue("Логин локального пользователя", login)
                .setDropDownValue(new DropDown(roleDropDown), role)
                .setInputValue("Пароль", password)
                .setInputValue("Подтверждение пароля", password)
                .clickButton("Добавить");
        TestUtils.wait(2000);
        assertTrue(new UsersTable().isColumnValueEquals(UsersTable.COLUMN_NAME, login), "Пользователь отсутсвует.");
        return this;
    }

    @Step("Редактирование пользователя")
    public VMwareOrganizationPage editUser(String login, String fio, String email, String role) {
        Menu.byElement(new UsersTable().getRowByColumnValue(UsersTable.COLUMN_NAME, login).getElementByColumn(""))
                .select("Редактировать");
        TestUtils.wait(1000);
        Dialog.byTitle("Редактировать пользователя")
                .setInputValue("ФИО", fio)
                .setInputValue("Email", email)
                .setDropDownValue(new DropDown(roleDropDown), role)
                .clickButton("Сохранить");
        TestUtils.wait(2000);
        assertTrue(new UsersTable().isColumnValueEquals(UsersTable.COLUMN_NAME, login), "Пользователь отсутсвует.");
        return this;
    }

    @Step("Изменение пароля пользователя")
    public void changeUserPassword(String userName, String password) {
        Menu.byElement(new UsersTable().getRowByColumnValue(UsersTable.COLUMN_NAME, userName).getElementByColumn(""))
                .select("Изменить пароль");
        Dialog.byTitle("Редактирование пароля")
                .setInputValue("Новый пароль", password)
                .setInputValue("Подтверждение пароля", password)
                .clickButton("Сохранить");
        new Alert().checkText("Пароль успешно изменен").checkColor(Alert.Color.GREEN);
    }

    @Step("Удаление пользователя")
    public void deleteUser(String userName) {
        Menu.byElement(new UsersTable().getRowByColumnValue(UsersTable.COLUMN_NAME, userName).getElementByColumn(""))
                .select("Удалить");
        Dialog.byTitle("Подтверждение удаления").clickButton("Удалить");
        new Alert().checkText("Пользователь {} удален", userName).checkColor(Alert.Color.GREEN);
        TestUtils.wait(2000);
        Assertions.assertFalse(new UsersTable().isColumnValueEquals(UsersTable.COLUMN_NAME, userName), "Пользователь найден");
    }

    public void compareUserFields(String login, String fio, String email, String role) {
        Menu.byElement(new UsersTable().getRowByColumnValue(UsersTable.COLUMN_NAME, login).getElementByColumn(""))
                .select("Редактировать");
        TestUtils.wait(1000);
        assertEquals(fio, Dialog.byTitle("Редактировать пользователя")
                .getInputValue("ФИО"));
        assertEquals(email, Dialog.byTitle("Редактировать пользователя")
                .getInputValue("Email"));
        assertEquals(role, new DropDown(roleDropDown).getElement().getText());
    }

    private static class UsersTable extends DataTable {
        public static final String COLUMN_NAME = "Логин";

        public UsersTable() {
            super(COLUMN_NAME);
        }
    }
}
