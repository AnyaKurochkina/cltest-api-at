package ui.t1.pages.cloudCompute;

import ui.cloud.pages.EntitiesUtils;
import ui.cloud.pages.IProductPage;
import ui.cloud.tests.ActionParameters;
import ui.elements.CheckBox;
import ui.elements.Dialog;

import java.time.Duration;

public class DiskPage extends IProductPage {
    private static final String COLUMN_POWER = "Статус";
    private static final String BLOCK_PARAMETERS = "Основные параметры";

    public void delete() {
        switchProtectOrder("");
        runActionWithParameters(BLOCK_PARAMETERS, "Удалить", "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        checkPowerStatus(TopInfo.POWER_STATUS_DELETED);
    }

    @Override
    public void waitChangeStatus() {
        EntitiesUtils.waitChangeStatus(new TopInfo(), Duration.ofMinutes(8));
    }

    @Override
    public void switchProtectOrder(String ignore) {
        runActionWithParameters("", "Защита от удаления", "Подтвердить",
                () -> CheckBox.byLabel("Включить защиту от удаления").setChecked(false),
                ActionParameters.builder().waitChangeStatus(false).checkPreBilling(false).checkLastAction(false).build());
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
