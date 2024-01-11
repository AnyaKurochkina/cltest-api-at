package ui.t1.pages.cloudEngine.compute;

import core.utils.Waiting;
import lombok.Getter;
import ui.elements.Dialog;
import ui.elements.MuiGridItem;
import ui.t1.pages.IProductT1Page;
import ui.t1.pages.cloudEngine.Column;

import java.time.Duration;

@Getter
public class Placement extends IProductT1Page<Placement> {
    public static final String ACTION_DELETE = "Удалить политику размещения";
    private final MuiGridItem id = MuiGridItem.byText("ID");
    private final MuiGridItem name = MuiGridItem.byText(Column.NAME);
    private final MuiGridItem status = MuiGridItem.byText(Column.STATUS);
    private final MuiGridItem type = MuiGridItem.byText("Тип политики");

    public TopInfo getTopInfo() {
        return new TopInfo();
    }

    /* Верхний блок с данными политики */
    public class TopInfo extends VirtualMachine {
        public TopInfo() {
            super("Кол-во");
        }

        @Override
        protected String getPowerStatus() {
            return getPowerStatus(Column.STATUS);
        }
    }

    @Override
    public void delete() {
        runActionWithParameters(BLOCK_PARAMETERS, ACTION_DELETE, "Удалить", () ->
        {
            Dialog dlgActions = Dialog.byTitle("Удаление");
            dlgActions.setInputValue("Идентификатор", dlgActions.getDialog().find("b").innerText());
        });
        Waiting.find(() -> new TopInfo().getPowerStatus().equals(Disk.TopInfo.POWER_STATUS_DELETED), Duration.ofSeconds(60));
    }
}
