package api.cloud.defectolog;

import api.cloud.defectolog.models.DefectPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.cloud.authorizer.Organization;
import models.cloud.tagService.Context;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.v2.InventoryTagsV2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Isolated;
import ui.t1.tests.engine.EntitySupplier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static models.cloud.tagService.TagServiceSteps.inventoryTagsV2;

@Isolated
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("Дефектолог")
@Feature("Проверка создания дефектов")
public class DefectologTest extends AbstractDefectologTest {

    private List<Inventory> inventoriesWithoutLinks;
    private final List<Inventory> inventories = new ArrayList<>();
    Context ctx;

    private final EntitySupplier<Void> init = lazy(() -> {
        ctx = Context.byId(((Organization) Organization.builder().type("default").build().createObject()).getName());
        inventoriesWithoutLinks = generateInventories(2);
        for (int i = 0; i < 2; i++) {
            inventories.add(Inventory.builder().context(ctx).contextPath(context.getContextPath()
                    .replaceFirst("/folder", "/ folder")).objectType("vm").build().createObjectPrivateAccess());
        }

        for (int i = 0; i < 2; i++)
            inventoryTagsV2(ctx, inventories.get(i).getId(), null, Arrays.asList(
                    new InventoryTagsV2.Tag("sys_item_context", "invalid_context"),
                    new InventoryTagsV2.Tag("sys_item_id", "item_id"),
                    new InventoryTagsV2.Tag("sys_item_name", "item_id")));

        startTaskWidthGroups("INV-CTX-INVALID", "INV-ACL-EMPTY", "INV-REQUIRED-ATTRS",
                "INV_REQUIRED_BOOL_ATTRS", "LINK-CTX-INVALID", "LINK-DUPLICATED-ATTRS-VALUES");
        return null;
    });

    @BeforeEach
    void beforeEach() {
        init.run();
    }

    @Test
    @DisplayName("Проверка группы INV-CTX-INVALID")
    void invCtxInvalid() {
        assertDefectPageContainsInventories("INV-CTX-INVALID", inventories);
    }

    @Test
    @DisplayName("Проверка группы INV-ACL-EMPTY")
    void invAclEmpty() {
        assertDefectPageContainsInventories("INV-ACL-EMPTY", inventories);
    }

    @Test
    @DisplayName("Проверка группы INV-REQUIRED-ATTRS")
    void invRequiredAttrs() {
        assertDefectPageContainsInventories("INV-REQUIRED-ATTRS", inventoriesWithoutLinks);
    }

    @Test
    @DisplayName("Проверка группы INV_REQUIRED_BOOL_ATTRS")
    void invRequiredBoolAttrs() {
        assertDefectPageContainsInventories("INV_REQUIRED_BOOL_ATTRS", inventories);
    }

    @Test
    @DisplayName("Проверка группы LINK-CTX-INVALID")
    void linkCtxInvalid() {
        assertDefectPageContainsInventories("LINK-CTX-INVALID", inventories);
    }

    @Test
    @DisplayName("Проверка группы LINK-DUPLICATED-ATTRS-VALUES")
    void linkDuplicatedAttrsValues() {
        assertDefectPageContainsInventories("LINK-DUPLICATED-ATTRS-VALUES", inventories);
    }

    private void assertDefectPageContainsInventories(String internalName, List<Inventory> inventories) {
        int defectId = findDefectIdByInternalName(internalName, getDateFromFilter(inventories.get(0), ctx));
        DefectPage defectPage = readDefectPage(defectId);
        inventories.forEach(inventory ->
                Assertions.assertTrue(defectPage.getPatients().contains(inventory.getId()),
                        String.format("Inventory %s not found in defectPage %d ", inventory.getId(), defectPage.getId())));
    }
}
