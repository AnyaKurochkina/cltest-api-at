package ui.cloud.pages.orders;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.utils.Env;
import core.utils.EnviromentChecker;
import models.cloud.orderService.products.TarantoolDataGrid;
import ui.elements.Button;
import ui.elements.Dialog;
import ui.elements.Select;
import ui.elements.Table;

import static core.helper.StringUtils.$x;

public class TarantoolDataGridAstraPage extends AbstractAstraPage {
    private static final String BLOCK_VM = "Виртуальные машины";
    private static final String BLOCK_APP = "Приложение";
    private static final String HEADER_CERTIFICATE = "Сертификаты";
    private static final String HEADER_COPY = "Резервные копии";
    private static final String HEADER_CONF_CLUSTER = "Конфигурация кластера";
    private static final String STATUS = "Статус";
    protected Button btnCluster = Button.byElement(Selenide.$x("//button[.='Кластер']"));
    private final String instance = "zorg-core-01";

    private final SelenideElement cpu = $x("(//h5)[1]");
    private final SelenideElement ram = $x("(//h5)[2]");
    private final SelenideElement getMenuActionIndex2 = $x("(//button[@id='actions-menu-button'])[2]");

    public TarantoolDataGridAstraPage(TarantoolDataGrid product) {
        super(product);
    }

    @Override
    public String getVirtualTableName() {
        return BLOCK_VM;
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new TarantoolDataGridAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(expectedStatus);
    }

    public void delete() {
        runActionWithParameters(getActionsMenuButton("", 2), "Удалить рекурсивно", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new TarantoolDataGridAstraPage.VirtualMachineTable(STATUS).checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void updateVersionApp() {
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters("Приложение", "Обновить версию приложения Tarantool Data Grid");
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void updateCertificate() {
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(HEADER_CERTIFICATE, "Обновить сертификаты Tarantool Data Grid");
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void createReserveCopy() {
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(HEADER_COPY, "Создать резервную копию");
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public String getInstance() {
        if (EnviromentChecker.standEnvIs(Env.IFT)) {
            return "plux-core-01";
        } else {
            return "zorg-core-01";
        }
    }

    public void stopTdg() {
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnCluster.click();
        runActionWithParameters(HEADER_CONF_CLUSTER, "Остановка сервисов TDG", "Подтвердить", () -> {
            Select.byLabel("Тип").set("Instance");
            Select.byLabel("Инстансы").set(getInstance());
        });
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void startTdg() {
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnCluster.click();
        runActionWithParameters(HEADER_CONF_CLUSTER, "Запуск сервисов TDG", "Подтвердить", () -> {
            Select.byLabel("Тип").set("Instance");
            Select.byLabel("Инстансы").set(getInstance());
        });
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void resetTdg() {
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        btnCluster.click();
        runActionWithParameters(HEADER_CONF_CLUSTER, "Перезапуск сервисов TDG", "Подтвердить", () -> {
            Select.byLabel("Тип").set("Instance");
            Select.byLabel("Инстансы").set(getInstance());
        });
        new TarantoolDataGridAstraPage.VirtualMachineTable().checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(TarantoolDataGridAstraPage.VirtualMachineTable.POWER_STATUS_OFF);
    }

    public class VirtualMachineTable extends VirtualMachine {
        public VirtualMachineTable() {
            super("Роли узла");
        }

        public VirtualMachineTable(String columnName) {
            super(columnName);
        }

        @Override
        public String getPowerStatus() {
            return getPowerStatus(STATUS);
        }
    }

    //Таблица ролей
    public class RoleTable extends Table {
        public RoleTable() {
            super("Группы");
        }

        @Override
        protected void open() {
            btnGeneralInfo.click();
        }

        private SelenideElement getRoleRow(String name) {
            return getRowElementByColumnValue("", name);
        }


    }
}
