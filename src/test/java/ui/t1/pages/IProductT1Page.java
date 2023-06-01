package ui.t1.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.orders.OrderUtils;
import ui.cloud.pages.orders.IProductPage;
import ui.cloud.pages.orders.ProductStatus;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.compute.Disk;

import java.time.Duration;
import java.util.Objects;

import static core.helper.StringUtils.$x;
import static core.helper.StringUtils.getClipBoardText;

@Log4j2
public class IProductT1Page<C extends IProductPage> extends IProductPage {
    public static final String BLOCK_PARAMETERS = "Основные параметры";
    private final SelenideElement waitStatus = $x("//*[.='Обновляется информация о заказе']");

    public IProductT1Page() {}

    public void delete() {
        switchProtectOrder(false);
        runActionWithParameters(BLOCK_PARAMETERS, "Удалить", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        Waiting.find(() -> new TopInfo().getPowerStatus().equals(Disk.TopInfo.POWER_STATUS_DELETED), Duration.ofSeconds(60));
    }

    @SuppressWarnings("unchecked")
    public C checkCreate(){
        if(Objects.isNull(OrderUtils.getPreBillingPrice()))
            Waiting.sleep(30000);
        checkLastAction("Развертывание");
        btnGeneralInfo.click();
        if(Objects.nonNull(OrderUtils.getPreBillingPrice()))
            Assertions.assertEquals(OrderUtils.getPreBillingPrice(), getOrderCost(), 0.01, "Стоимость заказа отличается от стоимости предбиллинга");
        OrderUtils.setPreBillingPrice(null);
        return (C) this;
    }

    @Override
    public void waitChangeStatus() {
        waitChangeStatus(Duration.ofMinutes(1));
    }

    @Override
    public void waitChangeStatus(Duration duration) {
        if(waitStatus.exists())
            waitStatus.scrollIntoView(TypifiedElement.scrollCenter).shouldNot(Condition.visible, duration);
    }

    @Override
    @Step("Проверка выполнения действия {action}")
    public void checkLastAction(String action) {
        getBtnGeneralInfo().getButton().shouldBe(Condition.visible);
        if(historyTab.getElement().exists()) {
            TypifiedElement.refresh();
            historyTab.switchTo();
            History history = new History();
            checkErrorByStatus(history.lastActionStatus());
            Assertions.assertEquals(history.lastActionName(), action, "Название последнего действия не соответствует ожидаемому");
        }
    }

    @Override
    @Step("Проверка статуса заказа")
    public void checkErrorByStatus(ProductStatus status) {
        if (status.equals(ProductStatus.ERROR)) {
            Assertions.fail(String.format("Ошибка выполнения action продукта: \nИтоговый статус: %s . \nОшибка: %s", status,
                    StateServiceSteps.getLastErrorByProjectId(OrderUtils.getCurrentProjectId())));
        } else log.info("Статус действия {}", status);
    }

    @Step("Получить ID продукта")
    public String getOrderId() {
        btnGeneralInfo.getButton().shouldBe(Condition.visible);
        Menu.byElement(getBtnAction("Действия")).select("Скопировать ID");
        Alert.green("ID скопирован");
        return getClipBoardText();
    }

    @Override
    public void switchProtectOrder(boolean checked) {
        ActionParameters params = ActionParameters.builder().waitChangeStatus(false).checkPreBilling(false).checkLastAction(false).build();
        new TopInfo();
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
            super(Column.STATUS);
        }

        @Override
        public String getPowerStatus() {
            return getPowerStatus(Column.STATUS);
        }

    }
}
