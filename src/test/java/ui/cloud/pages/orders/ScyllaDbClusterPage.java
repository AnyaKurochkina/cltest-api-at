package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import models.cloud.orderService.products.ScyllaDbCluster;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NotFoundException;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.Select;
import ui.elements.Table;

import java.util.List;

import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class ScyllaDbClusterPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String BLOCK_DB = "Базы данных";
    private static final String BLOCK_AT_DB_ADMIN = "at_db_admin";
    private static final String BLOCK_DB_AT_USER = "at_db_at_user";
    private static final String BLOCK_DB_USERS = "Пользователи";
    private static final String BLOCK_ACCESS = "Доступ";
    private static final String HEADER_NAME_USER_DB = "Имя";
    private static final String HEADER_NAME_DB = "Имя базы данных";
    private static final String HEADER_DB_USERS_ROLE = "Роль";
    private static final String POWER = "Статус";
    private static final String HEADER_SORT = "Сортировка";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";
    private static final String HEADER_COMMENTS = "Комментарий";

    private final SelenideElement cpu = $x("(//h5)[1]");
    private final SelenideElement ram = $x("(//h5)[2]");
    AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();

    public ScyllaDbClusterPage(ScyllaDbCluster product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new ScyllaDbClusterPage.VirtualMachineTable().checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration(SelenideElement node) {
        node.scrollIntoView(scrollCenter).click();
        checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию", ActionParameters.builder().node(node).build());
    }

    public void delete() {
        runActionWithParameters(BLOCK_APP, "Удалить кластер", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new ScyllaDbClusterPage.VirtualMachineTable().checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new ScyllaDbClusterPage.VirtualMachineTable().checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Перезагрузить");
        new ScyllaDbClusterPage.VirtualMachineTable().checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void updateOs() {
        runActionWithoutParameters(BLOCK_APP, "Обновить ОС");
        new ScyllaDbClusterPage.VirtualMachineTable().checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
    }


    public void createDb(String name) {
        new ScyllaDbClusterPage.VirtualMachineTable().checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        if (new Table(HEADER_NAME_DB).isEmpty()) {
            runActionWithParameters(BLOCK_DB, "Добавить БД", "Подтвердить", () -> {
                Dialog dlg = Dialog.byTitle("Добавить БД");
                dlg.setInputValue("Имя хранилища ключей", name);
            });
            generalInfoTab.switchTo();
            Assertions.assertTrue(new Table(HEADER_NAME_DB).isColumnValueContains(HEADER_NAME_DB, name), "БД не существует");
        }
    }

    public void addUserDb(String nameUserDb) {
        new ScyllaDbClusterPage.VirtualMachineTable().checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        if (new Table(HEADER_DB_USERS_ROLE).isEmpty()) {
            runActionWithParameters(BLOCK_DB_USERS, "Добавить пользователя", "Подтвердить", () -> {
                Dialog dlg = Dialog.byTitle("Добавить пользователя");
                dlg.setInputValue("Имя пользователя", nameUserDb);
                generatePassButton.shouldBe(Condition.enabled).click();
                Alert.green("Значение скопировано");
            });
            generalInfoTab.switchTo();
            Assertions.assertTrue(new Table(HEADER_DB_USERS_ROLE).isColumnValueContains(HEADER_NAME_USER_DB, nameUserDb), "Пользователь не существует");
        }
    }

    public void addRightsUser(String nameDb, String nameUserDb) {
        new ScyllaDbClusterPage.VirtualMachineTable().checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        if (!(new Table(HEADER_NAME_USER_DB, 2).isColumnValueEquals(HEADER_NAME_USER_DB, nameUserDb))) {
            runActionWithParameters(BLOCK_ACCESS, "Добавить права доступа пользователю БД", "Подтвердить", () -> {
                Dialog.byTitle("Добавить права доступа пользователю БД");
                Select.byLabel("Имя базы данных").set(nameDb);
                Select.byLabel("Имя пользователя").set(nameUserDb);
            });
            generalInfoTab.switchTo();
            Assertions.assertTrue(
                    new Table(HEADER_NAME_USER_DB, 2).isColumnValueContains(HEADER_NAME_USER_DB, nameUserDb), "Ошибка добавления прав доступа");
        }
    }

    public void deleteRightsUser(String nameUserDb) {
        new ScyllaDbClusterPage.VirtualMachineTable()
                .checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        if (new Table(HEADER_NAME_USER_DB, 2).isColumnValueEquals(HEADER_NAME_USER_DB, nameUserDb)) {
            runActionWithoutParameters(getActionsMenuButton(nameUserDb, 2), "Удалить права доступа пользователю БД");
            generalInfoTab.switchTo();
            Assertions.assertFalse(new Table(HEADER_NAME_USER_DB, 2)
                    .isColumnValueEquals(HEADER_NAME_USER_DB, nameUserDb), "Ошибка удаления прав доступа");
        }
    }

    public void removeDb(String name) {
        new ScyllaDbClusterPage.VirtualMachineTable().checkPowerStatus(ScyllaDbClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        if (new Table(HEADER_NAME_DB).isColumnValueEquals(HEADER_NAME_DB, name)) {
            runActionWithoutParameters(name, "Удалить БД");
            generalInfoTab.switchTo();
            Assertions.assertFalse(new Table("").isColumnValueEquals("", name), "БД существует");
        }
    }

    public void enlargeDisk(String name, String size, SelenideElement node) {
        node.scrollIntoView(scrollCenter).click();
        String firstSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE);
        String secondSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("", "/app/scylla/logs").getValueByColumn(HEADER_DISK_SIZE);
        expandDisk(name, size, node);
        generalInfoTab.switchTo();
        node.scrollIntoView(scrollCenter).click();
        String value = String.valueOf(Integer.parseInt(firstSizeDisk) +
                Integer.parseInt(size));
        String value2 = (String.valueOf(Integer.parseInt(value) + Integer.parseInt(secondSizeDisk)));
        Assertions.assertEquals(value, getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
        Assertions.assertTrue(getTableByHeader("Дополнительные диски").isColumnValueContains(HEADER_DISK_SIZE,
                value2));
    }

    public void resetPasswordUserDb(String nameUserDB) {
        runActionWithParameters(nameUserDB, "Сбросить пароль", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            Alert.green("Значение скопировано");
        });
    }

    public void deleteUserDb(String nameUser) {
        if (new Table(HEADER_DB_USERS_ROLE).isColumnValueContains(HEADER_NAME_USER_DB, nameUser)) {
            runActionWithoutParameters(nameUser, "Удалить пользователя");
            generalInfoTab.switchTo();
            Assertions.assertTrue(new Table(HEADER_DB_USERS_ROLE).isEmpty(), "Ошибка удаления пользователя БД");
        }
    }

    public SelenideElement getVMElement() {
        return new Table("Роли узла").getRow(0).get();
    }

    @Step("Добавить новые группы {group} с ролью {role}")
    public void addGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        getVMElement().scrollIntoView(scrollCenter).click();
        runActionWithParameters("Роли", "Добавить группу доступа", "Подтвердить", () -> {
            Select.byLabel("Роль").set(role);
            groups.forEach(group -> Select.byLabel("Группы").set(group));
        }, ActionParameters.builder().waitChangeStatus(false).node(getVMElement()).build());
        generalInfoTab.switchTo();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getVMElement().scrollIntoView(scrollCenter).click();
        groups.forEach(group -> Assertions.assertTrue(new RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
    }

    @Step("Изменить состав групп у роли {role} на {groups}")
    public void updateGroup(String role, List<String> groups) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        getVMElement().scrollIntoView(scrollCenter).click();
        runActionWithParameters(new RoleTable().getRoleMenuElement(role), "Изменить состав группы", "Подтвердить", () -> {
            Select groupsElement = Select.byLabel("Группы").clear();
            groups.forEach(groupsElement::set);
        }, ActionParameters.builder().node(getVMElement()).build());
        generalInfoTab.switchTo();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getVMElement().scrollIntoView(scrollCenter).click();
        groups.forEach(group -> Assertions.assertTrue(new RoleTable().getGroupsRole(role).contains(group), "Не найдена группа " + group));
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
    }

    @Step("Удалить группу доступа с ролью {role}")
    public void deleteGroup(String role) {
        checkPowerStatus(VirtualMachine.POWER_STATUS_ON);
        getVMElement().scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(new RoleTable().getRoleMenuElement(role), "Удалить группу доступа", ActionParameters.builder().waitChangeStatus(false).node(getVMElement()).build());
        generalInfoTab.switchTo();
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        getVMElement().scrollIntoView(scrollCenter).click();
        Assertions.assertThrows(NotFoundException.class, () -> new RoleTable().getRoleRow(role));
    }

    //Таблица ролей
    public class RoleTable extends Table {
        public RoleTable() {
            super("Группы");
        }

        @Override
        protected void open() {
            btnGeneralInfo.click();
            getVMElement().scrollIntoView(scrollCenter).click();
        }

        private SelenideElement getRoleMenuElement(String name) {
            return getRoleRow(name).$("button");
        }

        private SelenideElement getRoleRow(String name) {
            return getRowElementByColumnValue("", name);
        }

        private String getGroupsRole(String name) {
            open();
            return getRowByColumnValue("", name).getValueByColumn("Группы");
        }
    }


    public class VirtualMachineTable extends VirtualMachine {
        public VirtualMachineTable() {
            super("Роли узла");
        }

        @Override
        public String getPowerStatus() {
            return getPowerStatus(POWER);
        }

    }
}
