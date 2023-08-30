package ui.t1.pages.cloudEngine.vpc;

import ui.elements.Table;
import ui.t1.pages.IProductT1Page;
import ui.t1.pages.cloudEngine.Column;

public class VirtualIp extends IProductT1Page<VirtualIp> {

    public VirtualIpList.Menu getMenu() {
        return new VirtualIpList().getMenuVirtualIp(getActionsMenuButton(BLOCK_PARAMETERS));
    }

    public static class InterfacesTable extends Table {

        public InterfacesTable() {
            super(Column.DIRECTION);
        }

        public static boolean isAttachIp(String ip) {
            return new InterfacesTable().isColumnValueEquals(Column.IP, ip);
        }
    }
}
