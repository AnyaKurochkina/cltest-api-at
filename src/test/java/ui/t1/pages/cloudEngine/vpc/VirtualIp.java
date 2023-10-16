package ui.t1.pages.cloudEngine.vpc;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import ui.elements.Dialog;
import ui.elements.MuiGridItem;
import ui.elements.Table;
import ui.t1.pages.IProductT1Page;
import ui.t1.pages.cloudEngine.Column;

import static core.helper.StringUtils.$x;

@Getter
public class VirtualIp extends IProductT1Page<VirtualIp> {
    private final MuiGridItem publicIpElement = MuiGridItem.byText("Публичный IP адрес");

    public Menu getMenu() {
        return new Menu(getActionsMenuButton(BLOCK_PARAMETERS));
    }

    public class Menu {
        private final SelenideElement btn;

        public Menu(SelenideElement btn) {
            this.btn = btn;
        }

        @Step("Подключить к сетевому интерфейсу {ip}")
        public void attachComputeIp(String ip) {
                runActionWithParameters(btn, "Подключить к сетевому интерфейсу", "Подтвердить", () ->
                        Dialog.byTitle("Подключить к сетевому интерфейсу")
                                .setSelectValue("Сетевой интерфейс", ip));
        }

        @Step("Отключить от сетевого интерфейса {ip}")
        public void detachComputeIp(String ip) {
            runActionWithParameters(btn, "Отключить от сетевого интерфейса", "Подтвердить", () ->
                    Dialog.byTitle("Отключить от сетевого интерфейса")
                            .setSelectValue("Сетевой интерфейс", ip));
        }

        @Step("Отключить IP от интерфейса")
        public void disableInternet() {
            runActionWithoutParameters(BLOCK_PARAMETERS, "Отключить доступ в интернет");
        }

        @Step("Разрешить доступ в интернет")
        public void enableInternet() {
            runActionWithoutParameters(BLOCK_PARAMETERS, "Разрешить доступ в интернет");
        }
    }

    public static class InterfacesTable extends Table {

        public InterfacesTable() {
            super(Column.DIRECTION);
        }

        public static boolean isAttachIp(String ip) {
            if(!Table.isExist(Column.DIRECTION))
                return false;
            return new InterfacesTable().isColumnValueEquals(Column.IP, ip);
        }
    }
}
