package ui.t1.pages.cloudEngine.vpc;

import ui.elements.Table;
import ui.t1.pages.IProductT1Page;
import ui.t1.pages.cloudEngine.Column;

public class Router extends IProductT1Page<Router> {

    public RouterList.Menu getMenu() {
        return new RouterList().getMenuRouter(getActionsMenuButton(BLOCK_PARAMETERS));
    }

    public static class RouterTable extends Table {
        public RouterTable() {
            super("Тип");
        }
    }

    public static class NetworkInterfacesTable extends Table {
        public NetworkInterfacesTable() {
            super(Column.MAC);
        }
    }
}
