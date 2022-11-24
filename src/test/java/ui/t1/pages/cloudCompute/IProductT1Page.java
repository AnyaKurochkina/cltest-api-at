package ui.t1.pages.cloudCompute;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import core.utils.Waiting;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import ui.cloud.pages.EntitiesUtils;
import ui.cloud.pages.IProductPage;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;

import java.time.Duration;

public class IProductT1Page extends IProductPage {
    private static final String COLUMN_POWER = "Статус";
    public static final String BLOCK_PARAMETERS = "Основные параметры";

    public void delete() {
        switchProtectOrder(false);
        runActionWithParameters(BLOCK_PARAMETERS, "Удалить", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        checkPowerStatus(DiskPage.TopInfo.POWER_STATUS_DELETED);
    }

    @Override
    public void waitChangeStatus() {
        EntitiesUtils.waitChangeStatus(new TopInfo(), Duration.ofMinutes(1));
    }

    @Override
    public void waitChangeStatus(Duration duration) {
        EntitiesUtils.waitChangeStatus(new TopInfo(), duration);
    }

    @Override
    @Step("Проверка выполнения действия {action}")
    public void checkLastAction(String action) {
        //Todo: пока нет промежуточного статуса
        Waiting.sleep(15000);
        TypifiedElement.refresh();

        btnHistory.shouldBe(Condition.enabled).click();
        History history = new History();
        checkErrorByStatus(history.lastActionStatus());
        Assertions.assertEquals(history.lastActionName(), action, "Название последнего действия не соответствует ожидаемому");
    }

    public String getOrderId() {
        new Menu(getBtnAction("Действия")).select("Скопировать ID");
        new Alert().checkText("ID скопирован").checkColor(Alert.Color.GREEN).close();
        return Selenide.clipboard().getText();
    }

    @Override
    public void switchProtectOrder(boolean checked) {
        //Todo: сейчас кнопка активна до загрузки меню
        Waiting.sleep(1000);
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
