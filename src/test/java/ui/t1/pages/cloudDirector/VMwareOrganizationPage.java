package ui.t1.pages.cloudDirector;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import ui.cloud.pages.orders.OrderStatus;
import ui.cloud.pages.orders.OrderUtils;
import ui.cloud.tests.productCatalog.TestUtils;
import ui.elements.*;
import ui.models.cloudDirector.Vdc;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$x;
import static core.helper.StringUtils.format;
import static org.junit.jupiter.api.Assertions.*;
import static ui.cloud.tests.productCatalog.TestUtils.scrollToTheTop;

public class VMwareOrganizationPage {
    SelenideElement roleDropDown = $x("//*[@id ='mui-component-select-role_id']");
    SelenideElement cloudDirectorPage = $x("//a[text() = 'Список VMware организаций']");
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

    @Step("Переход на страницу Список VMware организаций")
    public CloudDirectorPage goToCloudDirectorPage() {
        scrollToTheTop();
        cloudDirectorPage.click();
        return new CloudDirectorPage();
    }

    @Step("Создание пользователя")
    public VMwareOrganizationPage addUser(String login, String role, String password) {
        if (new UsersTable().isColumnValueEquals(UsersTable.COLUMN_NAME, login)) {
            return this;
        }
        new UsersTable().clickAdd();
        Dialog.byTitle("Добавить пользователя")
                .setInputValue("Логин локального пользователя", login)
                .setSelectValue(Select.byLabel("Роль"), role)
                .setInputValue("Пароль", password)
                .setInputValue("Подтверждение пароля", password)
                .clickButton("Добавить");
        TestUtils.wait(2000);
        assertTrue(new UsersTable().isColumnValueEquals(UsersTable.COLUMN_NAME, login), "Пользователь отсутсвует.");
        return this;
    }

    @Step("Создание виртуального дата центра")
    public VMwareOrganizationPage addDataCentre(Vdc vdc) {
        new DataCentreTable().clickAdd();
        return new DataCentreCreatePage()
                .setCpu(vdc.getCpu())
                .setRam(vdc.getRam())
                .setDataCentreProfile(vdc.getStorageProfile())
                .setRouterBandwidth(vdc)
                .setDataCentreName(vdc.getName())
                .orderDataCentre();
    }

    @Step("Создание виртуального дата центра c существующим именем")
    public void addDataCentreWithExistName(String name) {
        new DataCentreTable().clickAdd();
        new DataCentreCreatePage()
                .setDataCentreName(name)
                .orderDataCentreWithSameName();
    }

    @Step("Выбор виртуального дата центра с именем {name}")
    public DataCentrePage selectDataCentre(String name) {
        new DataCentreTable().getRowByColumnValue("Название", name).get().click();
        return new DataCentrePage();
    }

    @Step("Проверка существования дата центра в таблице с именем {name}")
    public void checkDataCentreExist(String name) {
        assertTrue(new DataCentreTable().isColumnValueEquals("Название", name),
                format("Дата центр с именем {} не найден в таблице", name));
    }

    @Step("Проверка отсутствия дата центра в таблице с именем {name}")
    public void checkDataCentreNotExist(String name) {
        assertFalse(new DataCentreTable().isColumnValueEquals("Название", name),
                format("Дата центр с именем {} найден в таблице", name));
    }

    @Step("Показывать удаленные дата центры {isDisplay}")
    public VMwareOrganizationPage showDeletedDataCentres(boolean isDisplay) {
        Switch.byText("Показывать удаленные").setEnabled(isDisplay);
        return this;
    }

    @Step("Ожидание смены статуса")
    public VMwareOrganizationPage waitChangeStatus() {
        OrderUtils.waitChangeStatus(new DataCentreTable(), Duration.ofMinutes(8));
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
                .setSelectValue(Select.byLabel("Роль"), role)
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
        Alert.green("Пароль успешно изменен");
    }

    @Step("Удаление пользователя")
    public void deleteUser(String userName) {
        Menu.byElement(new UsersTable().getRowByColumnValue(UsersTable.COLUMN_NAME, userName).getElementByColumn(""))
                .select("Удалить");
        Dialog.byTitle("Подтверждение удаления").clickButton("Удалить");
        Alert.green("Пользователь {} удален", userName);
        TestUtils.wait(2000);
        assertFalse(new UsersTable().isColumnValueEquals(UsersTable.COLUMN_NAME, userName), "Пользователь найден");
    }

    public void compareUserFields(String login, String fio, String email, String role) {
        Menu.byElement(new UsersTable().getRowByColumnValue(UsersTable.COLUMN_NAME, login).getElementByColumn(""))
                .select("Редактировать");
        TestUtils.wait(1000);
        assertEquals(fio, Dialog.byTitle("Редактировать пользователя")
                .getInputValue("ФИО"));
        assertEquals(email, Dialog.byTitle("Редактировать пользователя")
                .getInputValue("Email"));
        assertEquals(role, roleDropDown.getText());
    }

    private static class UsersTable extends DataTable {
        public static final String COLUMN_NAME = "Логин";

        public UsersTable() {
            super(COLUMN_NAME);
        }
    }

    private static class DataCentreTable extends DataTable {
        public static final String COLUMN_NAME = "Название";

        public DataCentreTable() {
            super(COLUMN_NAME);
        }

        public OrderStatus getStatus() {
            return new OrderStatus(getValueByColumnInFirstRow("Статус").scrollIntoView(true).$x("descendant::*[name()='svg']"));
        }
    }
}
