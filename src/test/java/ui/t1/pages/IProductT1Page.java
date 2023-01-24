package ui.t1.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import core.utils.Waiting;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import steps.stateService.StateServiceSteps;
import ui.cloud.pages.EntitiesUtils;
import ui.cloud.pages.IProductPage;
import ui.cloud.pages.ProductStatus;
import ui.cloud.tests.ActionParameters;
import ui.elements.*;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.compute.Disk;

import java.time.Duration;
import java.util.Objects;

import static core.helper.StringUtils.$x;

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
        Waiting.find(() -> new TopInfo().getPowerStatus().equals(Disk.TopInfo.POWER_STATUS_DELETED), Duration.ofSeconds(30));
    }

    @SuppressWarnings("unchecked")
    public C checkCreate(){
        checkLastAction("Развертывание");
        btnGeneralInfo.click();
        if(Objects.nonNull(EntitiesUtils.getPreBillingPrice()))
            Assertions.assertEquals(EntitiesUtils.getPreBillingPrice(), getCostOrder(), 0.01, "Стоимость заказа отличается от стоимости предбиллинга");
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
        if(btnHistory.exists()) {
            TypifiedElement.refresh();
            btnHistory.shouldBe(Condition.enabled).click();
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
                    StateServiceSteps.getLastErrorByProjectId(EntitiesUtils.getCurrentProjectId())));
        } else log.info("Статус действия {}", status);
    }

    @Step("Получить ID продукта")
    public String getOrderId() {
        Menu.byElement(getBtnAction("Действия")).select("Скопировать ID");
        Alert.green("ID скопирован");
        return Selenide.clipboard().getText();
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
