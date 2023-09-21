package ui.cloud.pages.orders;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import models.cloud.orderService.products.ApacheKafkaCluster;
import models.cloud.orderService.products.KafkaService;
import models.cloud.subModels.Flavor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.IndexPage;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class KafkaServicePage extends IProductPage {
    private static final String BLOCK_APP = "Приложение";
    private static final String BLOCK_VM = "Виртуальная машина";
    private static final String BLOCK_CLUSTER = "Кластер";
    private static final String STATUS = "Роли узла";
    SelenideElement btnAclAccess = $x("//button[.='ACL на доступ']");
    SelenideElement btnAclGroup = $x("//button[.='ACL на группы']");
    SelenideElement btnAdd = $x("//button[contains(@class, 'array-item-add')]");

    public KafkaServicePage(KafkaService product) {
        super(product);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new VirtualMachineTable(STATUS).checkPowerStatus(expectedStatus);
    }

    public void start() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
        runActionWithoutParameters(BLOCK_APP, "Включить");
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopSoft() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить");
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void checkConfiguration() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        mainItemPage.scrollIntoView(scrollCenter).shouldBe(clickableCnd).click();
        new Table("Роли узла").getRowByIndex(0).scrollIntoView(scrollCenter).click();
        runActionWithoutParameters(BLOCK_VM, "Проверить конфигурацию", ActionParameters.builder().node(new Table("Роли узла").getRowByIndex(0)).checkAlert(false).build());
    }

    public void delete() {
        runActionWithParameters(BLOCK_CLUSTER, "Удалить топик", "Удалить", () ->
        {
            Dialog dlgActions = new Dialog("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        new VirtualMachineTable("Статус").checkPowerStatus(VirtualMachineTable.POWER_STATUS_DELETED);
    }

    public void restart() {
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_CLUSTER, "Перезагрузить кластер", ActionParameters.builder().timeOut(Duration.ofMinutes(20)).build());
        new VirtualMachineTable(STATUS).checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
    }

    public void stopHard() {
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_ON);
        runActionWithoutParameters(BLOCK_APP, "Выключить принудительно");
        checkPowerStatus(VirtualMachineTable.POWER_STATUS_OFF);
    }

    public void createBatchAcl(String nameT1) {
        btnAclAccess.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        runActionWithParameters("ACL на топики", "Пакетное создание ACL", "Подтвердить", () -> {
            Input.byLabel("Common Name сертификата клиента").setValue(nameT1);
        });
        btnAclAccess.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertFalse(getTableByHeader(nameT1).isEmpty(), "Ошибка создания acl");
    }

    public void createGroupAcl(String name) {
        btnAclGroup.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        runActionWithParameters("ACL на группы", "Пакетное создание групповой ACL", "Подтвердить", () -> {
            for (int i = 0; i < 2; i++) {
                if (i != 0)
                    btnAdd.shouldBe(Condition.enabled).click();
                Input.byLabel("Введите идентификатор транзакции", i + 1).setValue(name + i);
            }
        });
        btnAclGroup.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertFalse(getTableByHeader("CN сертификата клиента").isEmpty(), "Ошибка создания групповой acl");
    }

    public void deleteGroupAcl() {
        btnAclGroup.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        CheckBox.byXpath("//input[@type='checkbox']").setChecked(true);
        runActionWithoutParameters(getButton("Пакетное удаление ACL на группу"), "Пакетное удаление ACL на группу", ActionParameters.builder().isSimpleAction(true).build());
        btnAclGroup.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertTrue(getTableByHeader("CN сертификата клиента").isEmpty(), "Ошибка удаления acl");
    }

    public void deleteBatchAcl() {
        btnAclAccess.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        CheckBox.byXpath("//input[@type='checkbox']").setChecked(true);
        runActionWithoutParameters(getButton("Пакетное удаление ACL"), "Пакетное удаление ACL", ActionParameters.builder().isSimpleAction(true).build());
        btnAclAccess.shouldBe(activeCnd).hover().shouldBe(clickableCnd).click();
        Assertions.assertTrue(getTableByHeader("Роль").isEmpty(), "Ошибка удаления acl");
    }

    public SelenideElement getButton(String name) {
        return $x("(//button[.='{}'])", name);
    }


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
