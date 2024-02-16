package ui.t1.pages.cloudEngine.backup;

import core.utils.Waiting;
import io.qameta.allure.Step;
import ui.cloud.tests.ActionParameters;
import ui.elements.Breadcrumb;
import ui.elements.Dialog;
import ui.t1.pages.IProductT1Page;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.compute.VmCreate;

import java.time.Duration;

public class IncrementalCopy extends IProductT1Page<IncrementalCopy> {

    private class TopInfo extends VirtualMachine {
        public TopInfo() {
            super(Column.CREATED_DATE);
        }

        @Override
        protected String getPowerStatus() {
            return getPowerStatus(Column.STATUS);
        }
    }

    @Step("Возврат в контейнер резервных копий")
    protected void goToBackupContainer() {
        Breadcrumb.click("Резервная копия");
    }

    @Step("Создание вм {name} на базе резервной копии")
    public void restore(String name, String sshKey, String securityGroup) {
        final String action = "Восстановить";
        runActionWithParameters(BLOCK_PARAMETERS, action, "Подтвердить", () ->
        {
            Dialog.byTitle(action);
            new VmCreate().setName(name).addSecurityGroups(securityGroup).setSshKey(sshKey);
        }, ActionParameters.builder().checkLastAction(false).build());
        goToBackupContainer();
        checkLastAction(action);
    }

    @Override
    public void delete() {
        runActionWithoutParameters(BLOCK_PARAMETERS, "Удалить резервную копию",
                ActionParameters.builder().checkLastAction(false).build());
        Waiting.find(() -> new TopInfo().getPowerStatus().equals(TopInfo.POWER_STATUS_DELETED), Duration.ofSeconds(60));
        goToBackupContainer();
        checkLastAction("Удалить резервную копию");
    }
}
