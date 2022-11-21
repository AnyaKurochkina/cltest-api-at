package ui.cloud.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import models.cloud.orderService.products.ClickHouseCluster;
import models.cloud.portalBack.AccessGroup;
import org.junit.jupiter.api.Assertions;
import ui.cloud.tests.ActionParameters;
import ui.elements.Alert;
import ui.elements.Dialog;
import ui.elements.Table;

import static core.helper.StringUtils.$x;
import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static ui.elements.TypifiedElement.scrollCenter;

public class ClickHouseClusterPage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String HEADER_USER_LOCAL = "ТУЗ Локальные";
    private static final String HEADER_USER_AD = "ТУЗ AD";
    private static final String HEADER_GROUP_AD = "Группы пользователей AD";
    private static final String HEADER_GROUP_ADMIN = "Группы прикладных администраторов AD";
    private static final String HEADER_DB_USERS = "ch_customer";
    private static final String HEADER_LIMIT_CONNECT = "Предел подключений";
    private static final String HEADER_DISK_SIZE = "Размер, ГБ";

    SelenideElement btnDb = $x("//button[.='БД и Владельцы']");
    SelenideElement btnUsers = $x("//button[.='Пользователи']");
    SelenideElement btnGroups = $x("//button[.='Группы']");
    AccessGroup accessGroup = AccessGroup.builder().projectName(product.getProjectId()).build().createObject();
    public ClickHouseClusterPage(ClickHouseCluster product) {
        super(product);
    }

    @Override
    void checkPowerStatus(String expectedStatus) {
        new ClickHouseClusterPage.VirtualMachineTable("Роли узла").checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию", ActionParameters.builder().node(new Table("Роли узла").getRowByIndex(0)).build());
    }

    public void delete() {
        runActionWithParameters(BLOCK_APP, "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new ClickHouseClusterPage.VirtualMachineTable("Статус").checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new ClickHouseClusterPage.VirtualMachineTable("Роли узла").checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Перезагрузить");
        new ClickHouseClusterPage.VirtualMachineTable("Роли узла").checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void removeDb(String name) {
        new ClickHouseClusterPage.VirtualMachineTable("Статус").checkPowerStatus(ClickHouseClusterPage.VirtualMachineTable.POWER_STATUS_ON);
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        runActionWithoutParameters(name, "Удалить БД");
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        btnDb.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertFalse(new Table(HEADER_LIMIT_CONNECT).isColumnValueEquals("", name), "БД существует");
    }

    public void enlargeDisk(String name, String size, SelenideElement node) {
        node.scrollIntoView(scrollCenter).click();
        String firstSizeDisk = getTableByHeader("Дополнительные точки монтирования")
                .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE);
        expandDisk(name, size, node);
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        node.scrollIntoView(scrollCenter).click();
        String value = String.valueOf(Integer.parseInt(firstSizeDisk) +
                Integer.parseInt(size));
        Assertions.assertEquals(value, getTableByHeader("Дополнительные точки монтирования")
                        .getRowByColumnValue("", name).getValueByColumn(HEADER_DISK_SIZE),
                "Неверный размер диска");
        Assertions.assertTrue(getTableByHeader("Дополнительные диски").isColumnValueContains(HEADER_DISK_SIZE,
                value));
    }

    public void createLocalAccount(String name) {
        btnUsers.shouldBe(Condition.enabled).click();
        runActionWithParameters(HEADER_USER_LOCAL, "Создать локальную УЗ", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Создать локальную УЗ");
            dlg.setInputValue("Имя пользователя", name);
            generatePassButton.shouldBe(Condition.enabled).click();
            new Alert().checkText("Значение скопировано").checkColor(Alert.Color.GREEN).close();
        });
        btnUsers.shouldBe(Condition.enabled).click();
        Assertions.assertTrue(getBtnAction("at_local_user").exists(), "Ошибка создания УЗ");
    }

    public void resetPasswordLA(String name) {
        btnUsers.shouldBe(Condition.enabled).click();
        runActionWithParameters(name, "Сбросить пароль", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            new Alert().checkText("Значение скопировано").checkColor(Alert.Color.GREEN).close();
        });
    }


    public void deleteLocalAccount(String name) {
        btnUsers.shouldBe(Condition.enabled).click();
        runActionWithParameters(name, "Удалить локальную УЗ", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Удалить локальную УЗ");
            dlg.setInputValue("Пользователь БД", name);
        });
        btnUsers.shouldBe(Condition.enabled).click();
        Assertions.assertFalse(getBtnAction(name).exists(), "Ошибка удаления УЗ");
    }

    public void addAccountAD(String name) {
        btnUsers.shouldBe(Condition.enabled).click();
        runActionWithParameters(HEADER_USER_AD, "Добавить ТУЗ AD", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Добавить ТУЗ AD");
            dlg.setInputValue("Имя пользователя", name);
        });
        btnUsers.shouldBe(Condition.enabled).click();
        Assertions.assertTrue(getBtnAction("at_ad_user").exists(), "Ошибка создания УЗ АД");
    }

    public void resetPasswordAD(String name) {
        btnUsers.shouldBe(Condition.enabled).click();
        runActionWithParameters(name, "Сбросить пароль", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Сбросить пароль");
            generatePassButton.shouldBe(Condition.enabled).click();
            new Alert().checkText("Значение скопировано").checkColor(Alert.Color.GREEN).close();
        });
    }

    public void deleteAccountAD(String name) {
        btnUsers.shouldBe(Condition.enabled).click();
        runActionWithParameters(name, "Удалить ТУЗ AD", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Удалить ТУЗ AD");
            dlg.setInputValue("Пользователь БД", name);
        });
        btnUsers.shouldBe(Condition.enabled).click();
        Assertions.assertFalse(getBtnAction(name).exists(), "Ошибка удаления УЗ АД");
    }

    public void addGroupAD(String nameGroup) {
        btnGroups.shouldBe(Condition.enabled).click();
        runActionWithParameters(HEADER_GROUP_AD, "Добавить пользовательскую группу", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Добавить пользовательскую группу");
            dlg.setDropDownValue("Группы", nameGroup);
        });
        btnGroups.shouldBe(Condition.enabled).click();
        Assertions.assertTrue(getBtnAction(accessGroup.getPrefixName()).exists(), "Ошибка создания AD");
    }
    public void addGroupAdmin(String nameGroup) {
        btnGroups.shouldBe(Condition.enabled).click();
        runActionWithParameters(HEADER_GROUP_ADMIN, "Добавить группу администраторов", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Добавить группу администраторов");
            dlg.setDropDownValue("Группы", nameGroup);
        });
        btnGroups.shouldBe(Condition.enabled).click();
        Assertions.assertTrue(getBtnAction(accessGroup.getPrefixName()).exists(), "Ошибка создания AD");
    }

    public void deleteGroupAD(String nameGroup) {
        btnGroups.shouldBe(Condition.enabled).click();
        runActionWithParameters(nameGroup, "Удалить пользовательскую группу", "Подтвердить", () -> {
            Dialog dlg = Dialog.byTitle("Удалить пользовательскую группу");
            dlg.setInputValue("Название группы", nameGroup);
        });
        btnGroups.shouldBe(Condition.enabled).click();
        Assertions.assertTrue(getBtnAction(accessGroup.getPrefixName()).exists(), "Ошибка удаления AD");
    }

    public void deleteGroupAdmin(String nameGroup) {
        btnGroups.shouldBe(Condition.enabled).click();
        runActionWithoutParameters(nameGroup, "Удалить админ группу");
        btnGroups.shouldBe(Condition.enabled).click();
        Assertions.assertTrue(getBtnAction(accessGroup.getPrefixName()).exists(), "Ошибка удаления админ группы");
    }
//    public SelenideElement getRowCh (){
//        new Table("Имя").getRowByColumnValue("Роли узла","clickhouse");
//        return ;
//    }

    public class VirtualMachineTable extends VirtualMachine {
        public VirtualMachineTable(String columnName) {
            super(columnName);
        }
        @Override
        public String getPowerStatus() {
            return getPowerStatus("Статус");
        }

    }
}
