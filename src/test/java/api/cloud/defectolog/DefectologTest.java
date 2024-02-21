package api.cloud.defectolog;

import api.cloud.defectolog.models.Defect;
import api.cloud.defectolog.models.DefectPage;
import api.cloud.defectolog.steps.DefectologSteps;
import com.mifmif.common.regex.Generex;
import core.helper.Report;
import core.utils.AssertUtils;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.cloud.authorizer.Organization;
import models.cloud.tagService.Context;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.TagServiceSteps;
import models.cloud.tagService.v2.InventoryTagsV2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Isolated;
import ui.t1.tests.engine.EntitySupplier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static models.cloud.tagService.TagServiceSteps.inventoryTagsV2;

@Isolated
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Epic("Дефектолог")
@Feature("Проверка создания дефектов")
public class DefectologTest extends AbstractDefectologTest {

    private List<Inventory> inventoriesWithoutLinks;
    private final List<Inventory> inventories = new ArrayList<>();
    Context ctx;

    private final EntitySupplier<Void> init = lazy(() -> {
        String value = new Generex("item_id_[0-9]{5}").random();
        ctx = Context.byId(((Organization) Organization.builder().type("default").build().createObject()).getName());
        inventoriesWithoutLinks = generateInventories(2);
        for (int i = 0; i < 2; i++) {
            inventories.add(Inventory.builder().context(ctx).contextPath(context.getContextPath()
                    .replaceFirst("/folder", "/ folder")).objectType("vm").build().createObjectPrivateAccess());
        }

        for (int i = 0; i < 2; i++)
            inventoryTagsV2(ctx, inventories.get(i).getId(), null, Arrays.asList(
                    new InventoryTagsV2.Tag("sys_item_context", "invalid_context"),
                    new InventoryTagsV2.Tag("sys_item_id", value),
                    new InventoryTagsV2.Tag("sys_item_name", value)));

        startTaskWidthGroups("INV-CTX-INVALID", "INV-ACL-EMPTY", "INV-REQUIRED-ATTRS",
                "INV_REQUIRED_BOOL_ATTRS", "LINK-CTX-INVALID", "LINK-DUPLICATED-ATTRS-VALUES");
        return null;
    });

    @BeforeEach
    public void beforeEach() {
        init.run();
    }

    @Test
    @Order(1)
    @DisplayName("Проверка группы INV-CTX-INVALID")
    void invCtxInvalid() {
        assertDefectPageContainsInventories("INV-CTX-INVALID", inventories);
    }

    @Test
    @Order(2)
    @DisplayName("Проверка группы INV-ACL-EMPTY")
    void invAclEmpty() {
        assertDefectPageContainsInventories("INV-ACL-EMPTY", inventories);
    }

    @Test
    @Order(3)
    @DisplayName("Проверка группы INV-REQUIRED-ATTRS")
    void invRequiredAttrs() {
        assertDefectPageContainsInventories("INV-REQUIRED-ATTRS", inventoriesWithoutLinks);
    }

    @Test
    @Order(4)
    @DisplayName("Проверка группы INV_REQUIRED_BOOL_ATTRS")
    void invRequiredBoolAttrs() {
        assertDefectPageContainsInventories("INV_REQUIRED_BOOL_ATTRS", inventories);
    }

    @Test
    @Order(5)
    @DisplayName("Проверка группы LINK-CTX-INVALID")
    void linkCtxInvalid() {
        assertDefectPageContainsInventories("LINK-CTX-INVALID", inventories);
    }

    @Test
    @Order(100)
    @DisplayName("Проверка группы LINK-DUPLICATED-ATTRS-VALUES")
    void linkDuplicatedAttrsValues() {
        assertDefectPageContainsInventories("LINK-DUPLICATED-ATTRS-VALUES", inventories);
        Report.checkStep("Отсутствие дефекта при удаленном объекте", () -> {
            TagServiceSteps.inventoriesDeleteBatchV2(ctx, Collections.singletonList(inventories.get(1).getId()));
            Waiting.sleep(60000);
            inventoryTagsV2(ctx, inventories.get(0).getId(), null,
                    Collections.singletonList(new InventoryTagsV2.Tag("test_filter", "value")));
            startTaskWidthGroups("LINK-DUPLICATED-ATTRS-VALUES");
            int defectId = findDefectIdByInternalName("LINK-DUPLICATED-ATTRS-VALUES", getDateFromFilter(inventories.get(0), ctx));
            List<Defect.DefectPages> defectPages = DefectologSteps.defectsRead(defectId).getDefectPages();
            // либо страница одна и не содержит inventories, либо страниц не создается
            if (!defectPages.isEmpty())
                AssertUtils.assertNotContainsList(DefectologSteps.defectPagesRead(defectPages.get(0).getId()).getPatients(),
                        inventories.get(0).getId(), inventories.get(1).getId());
        });
    }

    private void assertDefectPageContainsInventories(String internalName, List<Inventory> inventories) {
        int defectId = findDefectIdByInternalName(internalName, getDateFromFilter(inventories.get(0), ctx));
        DefectPage defectPage = readDefectPage(defectId);
        inventories.forEach(inventory ->
                Assertions.assertTrue(defectPage.getPatients().contains(inventory.getId()),
                        String.format("Inventory %s not found in defectPage %d ", inventory.getId(), defectPage.getId())));
    }
}
