package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import ui.cloud.pages.EntitiesUtils;
import ui.cloud.pages.IProductPage;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;

import java.time.Duration;
import java.util.Objects;

import static api.Tests.activeCnd;
import static api.Tests.clickableCnd;
import static core.helper.StringUtils.$x;
import static ui.elements.TypifiedElement.scrollCenter;

public class IProductT1Page extends IProductPage {
    private static final String COLUMN_POWER = "Статус";
    public static final String BLOCK_PARAMETERS = "Основные параметры";
    private final SelenideElement waitStatus = $x("//*[.='Обновляется информация о заказе']");

    public IProductT1Page() {
        new TopInfo();
    }

    public void delete() {
        switchProtectOrder(false);
        runActionWithParameters(BLOCK_PARAMETERS, "Удалить", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        //Todo: пока слип
        Waiting.sleep(15000);
        checkPowerStatus(Disk.TopInfo.POWER_STATUS_DELETED);
    }


    public <T extends IProductPage> T checkCreate(){
        checkLastAction("Развертывание");
        btnGeneralInfo.shouldBe(Condition.enabled).click();
        if(Objects.nonNull(EntitiesUtils.getPreBillingPrice()))
            Assertions.assertEquals(EntitiesUtils.getPreBillingPrice(), getCostOrder(), 0.01, "Стоимость заказа отличается от стоимости предбиллинга");
        return (T) this;
    }

    @Override
    public void waitChangeStatus() {
        waitChangeStatus(Duration.ofMinutes(1));
    }

    @Override
    public void waitChangeStatus(Duration duration) {
        if(waitStatus.exists())
            waitStatus.scrollIntoView(TypifiedElement.scrollCenter).shouldNot(Condition.visible, duration);
//        else
//            EntitiesUtils.waitStatus(new TopInfo(), Disk.TopInfo.POWER_STATUS_DELETED, duration);
    }

    @Override
    @Step("Проверка выполнения действия {action}")
    public void checkLastAction(String action) {
        if(btnHistory.exists()) {
            TypifiedElement.refresh();
            btnHistory.shouldBe(Condition.enabled).click();
            History history = new History();
            checkErrorByStatus(history.lastActionStatus());
            Assertions.assertEquals(history.lastActionName(), action, "Название последнего действия не соответствует ожидаемому");
        }
    }

    public String getOrderId() {
        Menu.byElement(getBtnAction("Действия")).select("Скопировать ID");
        Alert.green("ID скопирован");
        return Selenide.clipboard().getText();
    }

    @Override
    public void switchProtectOrder(boolean checked) {
        ActionParameters params = ActionParameters.builder().waitChangeStatus(false).checkPreBilling(false).checkLastAction(false).build();
        runActionWithParameters("Действия", "Защита от удаления", "Подтвердить",
                () -> {
                    CheckBox checkBox = CheckBox.byLabel("Включить защиту от удаления");
                    if (checkBox.getChecked() == checked)
                        params.setClickCancel(true);
                    else
                        CheckBox.byLabel("Включить защиту от удаления").setChecked(checked);
                },
                params);
    }

    @Override
    protected void checkPowerStatus(String expectedStatus) {
        new TopInfo().checkPowerStatus(expectedStatus);
    }

    public class TopInfo extends VirtualMachine {
        public TopInfo() {
            super(COLUMN_POWER);
        }

        @Override
        public String getPowerStatus() {
            return getPowerStatus(COLUMN_POWER);
        }

    }
}
