package ui.t1.tests.engine.vpc;

import core.helper.StringUtils;
import core.helper.TableChecker;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.AbstractEntity;
import org.junit.BlockTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.cloud.pages.CompareType;
import ui.elements.Menu;
import ui.extesions.InterceptTestExtension;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.vpc.Router;
import ui.t1.pages.cloudEngine.vpc.RouterCreate;
import ui.t1.pages.cloudEngine.vpc.RouterList;
import ui.t1.pages.cloudEngine.vpc.VirtualIpList;
import ui.t1.tests.engine.AbstractComputeTest;

import java.util.Arrays;

import static ui.cloud.pages.orders.IProductPage.getActionsMenuButton;
import static ui.t1.pages.IProductT1Page.BLOCK_PARAMETERS;

@BlockTests
@ExtendWith(InterceptTestExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Feature("Маршрутизаторы")
@Epic("Cloud Compute")
public class RouterTest extends AbstractComputeTest {
    RouterCreate router;

    @Test
    @Order(1)
    @TmsLink("")
    @Tag("smoke")
    @DisplayName("Cloud VPC. Маршрутизаторы. Создать маршрутизатор")
    void addRouter() {
        router = new IndexPage().goToRouters().addRouter().setName(getRandomName()).setRegion(region).setDesc("desc").addNetwork(defaultNetwork).clickOrder();
        new RouterList().selectRouter(router.getName()).markForDeletion(new VmEntity().setMode(AbstractEntity.Mode.AFTER_CLASS)).checkCreate(true);
    }

    @Test
    @Order(2)
    @TmsLink("")
    @DisplayName("Cloud VPC. Маршрутизаторы")
    void checkRouterList() {
        new IndexPage().goToRouters();
        new TableChecker()
                .add("", String::isEmpty)
                .add(Column.NAME, e -> e.equals(router.getName()))
                .add("Регион", e -> e.equals(router.getRegion()))
                .add("Публичный IP",  e -> StringUtils.isMatch("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$", e))
                .add("Дата создания",  e -> e.length() > 4)
                .add("", String::isEmpty)
                .check(() -> new RouterList.RouterTable().getRowByColumnValue(Column.NAME, router.getName()));
    }

    @Test
    @Order(3)
    @TmsLink("")
    @DisplayName("Cloud VPC. Маршрутизатор. Действия")
    void checkRouter() {
        new IndexPage().goToRouters().selectRouter(router.getName());
        new TableChecker()
                .add(Column.NAME, e -> e.equals(router.getName()))
                .add("Тип", e -> e.equals("Маршрутизатор"))
                .add("Статус",  String::isEmpty)
                .add("", String::isEmpty)
                .check(() -> new Router.RouterTable().getRow(0));
        new TableChecker()
                .add(Column.IP, e -> StringUtils.isMatch("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$", e))
                .add(Column.NAME, e -> e.length() > 5)
                .add("Тип", e -> e.equals("Сетевой интерфейс"))
                .add("Статус",  String::isEmpty)
                .add("Подсеть",  e -> e.equals(router.getNetworks().get(0)))
                .add("Сеть",  e -> e.equals(router.getRegion()))
                .add(Column.MAC,  e -> StringUtils.isMatch( "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$", e))
                .add("", String::isEmpty)
                .check(() -> new Router.RouterTable().getRow(0));
        AssertUtils.assertEqualsList(Arrays.asList("Подключить сеть", "Скопировать ID", "Удалить"), new Menu(getActionsMenuButton(BLOCK_PARAMETERS)).getOptions());
        AssertUtils.assertEqualsList(Arrays.asList("Удалить сетевой интерфейс", "Скопировать ID"), new Menu(getActionsMenuButton("Сетевые интерфейсы")).getOptions());
    }

    @Test
    @Order(4)
    @TmsLink("")
    @DisplayName("Cloud VPC. Подключить сеть")
    void connectNetwork() {
        final String networkName = getRandomName();
        new IndexPage().goToNetworks().addNetwork(networkName, "desc");
        try {
            new IndexPage().goToRouters()
                    .getMenuRouter(new RouterList.RouterTable().getRowByColumnValue(Column.NAME, router.getName()).getElementLastColumn())
                    .attachNetwork(networkName);
        } finally {
            new IndexPage().goToNetworks().deleteNetwork(networkName);
        }
    }

    @Test
    @TmsLink("")
    @Order(100)
    @DisplayName("Cloud VPC. Маршрутизаторы. Удалить")
    void deleteRouter() {
        Router routerPage = new IndexPage().goToRouters().selectRouter(router.getName());
        routerPage.runActionWithCheckCost(CompareType.ZERO, routerPage::delete);
    }
}
