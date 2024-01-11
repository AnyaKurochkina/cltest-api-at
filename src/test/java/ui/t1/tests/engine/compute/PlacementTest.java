package ui.t1.tests.engine.compute;

import core.helper.TableChecker;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.TmsLink;
import models.AbstractEntity;
import org.junit.jupiter.api.*;
import ui.cloud.pages.CompareType;
import ui.elements.Menu;
import ui.t1.pages.IndexPage;
import ui.t1.pages.cloudEngine.Column;
import ui.t1.pages.cloudEngine.compute.*;
import ui.t1.tests.engine.AbstractComputeTest;
import ui.t1.tests.engine.EntitySupplier;

import static ui.cloud.pages.orders.IProductPage.getActionsMenuButton;
import static ui.t1.pages.IProductT1Page.BLOCK_PARAMETERS;
import static ui.t1.pages.cloudEngine.compute.Placement.ACTION_DELETE;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Epic("Cloud Compute")
@Feature("Политики размещения")
public class PlacementTest extends AbstractComputeTest {

    private final EntitySupplier<PlacementCreate> placementSup = lazy(() -> {
        PlacementCreate placementCreate = new IndexPage().goToPlacementPolicy().addPlacement()
                .setName(getRandomName())
                .setAvailabilityZone(availabilityZone)
                .setType(PlacementCreate.Type.SOFT_AFFINITY)
                .clickOrder();
        new PlacementList().selectPlacement(placementCreate.getName()).markForDeletion(new PlacementEntity(), AbstractEntity.Mode.AFTER_CLASS).checkCreate();
        return placementCreate;
    });

    @Test
    @Order(1)
    @TmsLink("SOUL-8386")
    @DisplayName("Cloud Compute. Политики размещения. Заказать")
    void createPlacement() {
        openPlacementPolicy();
    }

    @Test
    @Order(2)
    @TmsLink("SOUL-8385")
    @DisplayName("Cloud Compute. Политики размещения")
    void placementList() {
        PlacementCreate placementCreate = openPlacementPolicy();
        new IndexPage().goToPlacementPolicy();
        new TableChecker()
                .add("", String::isEmpty)
                .add(Column.NAME, e -> e.equals(placementCreate.getName()))
                .add(Column.AVAILABILITY_ZONE, e -> e.equals(placementCreate.getAvailabilityZone()))
                .add("Политика", e -> e.equals(placementCreate.getType().toString()))
                .add("Кол-во", e -> e.equals("—"))
                .add("Дата создания", e -> e.length() > 4)
                .add("", String::isEmpty)
                .check(() -> new PlacementList.PlacementTable().getRowByColumnValue(Column.NAME, placementCreate.getName()));
    }

    @Test
    @Order(3)
    @TmsLink("SOUL-8387")
    @DisplayName("Cloud Compute. Политики размещения. Просмотр")
    void openPlacement() {
        PlacementCreate placementCreate = openPlacementPolicy();
        new TableChecker()
                .add(Column.NAME, e -> e.equals(placementCreate.getName()))
                .add("Тип", e -> e.equals(placementCreate.getType().toString()))
                .add("Кол-во", String::isEmpty)
                .add(Column.STATUS, String::isEmpty)
                .add("", String::isEmpty)
                .check(() -> new Placement().getTopInfo().getRow(0));
        final Placement placement = new Placement();
        Assertions.assertAll("Несоответствие полей блока 'Основные параметры'",
                () -> Assertions.assertEquals(placement.getOrderId(), placement.getId().nextItem().getText(), "Поле 'ID'"),
                () -> Assertions.assertEquals(placementCreate.getName(), placement.getName().nextItem().getText(), "Поле 'Имя'"),
                () -> Assertions.assertEquals(placementCreate.getType().toString(), placement.getType().nextItem().getText(), "Поле 'Тип'"),
                () -> Assertions.assertTrue(placement.getStatus().nextItem().$x(".//*[@data-testid='status-on']").isDisplayed(), "Поле 'Статус'")
        );
    }

    @Test
    @Order(4)
    @TmsLink("SOUL-8389")
    @DisplayName("Cloud Compute. Политики размещения. Проверить невозможность удаления при созданной ВМ с политиками")
    void deletePlacementWidthVm() {
        PlacementCreate placementCreate = openPlacementPolicy();
        VmCreate vm = new IndexPage().goToVirtualMachine().addVm()
                .setRegion(region)
                .setAvailabilityZone(availabilityZone)
                .seNetwork(defaultNetwork)
                .setSubnet(defaultSubNetwork)
                .setImage(image)
                .setDeleteOnTermination(true)
                .setName(getRandomName())
                .addSecurityGroups(securityGroup)
                .setSshKey(sshKey)
                .setPlacement(placementCreate.getName())
                .clickOrder();
        new VmList().selectCompute(vm.getName()).markForDeletion(new InstanceEntity(), AbstractEntity.Mode.AFTER_TEST).checkCreate(true);
        new Vm().getGeneralInfoTab().switchTo();
        Assertions.assertFalse(Menu.byElement(getActionsMenuButton(BLOCK_PARAMETERS)).isItemDisplayed(ACTION_DELETE),
                "Доступно действие " + ACTION_DELETE);
    }

    @Test
    @Order(100)
    @TmsLink("SOUL-8388")
    @DisplayName("Cloud Compute. Политики размещения. Удалить")
    void deletePlacement() {
        openPlacementPolicy();
        Placement placement = new Placement();
        placement.runActionWithCheckCost(CompareType.ZERO, placement::delete);
    }

    @Step("Открытие страницы политики размещения")
    private PlacementCreate openPlacementPolicy() {
        PlacementCreate placementCreate = placementSup.get();
        new IndexPage().goToPlacementPolicy().selectPlacement(placementCreate.getName());
        return placementCreate;
    }
}
