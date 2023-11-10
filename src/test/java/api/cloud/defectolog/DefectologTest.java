package api.cloud.defectolog;

import api.cloud.defectolog.models.DefectPage;
import api.cloud.defectolog.models.StartTask;
import api.cloud.defectolog.steps.DefectologSteps;
import api.cloud.tagService.AbstractTagServiceTest;
import core.exception.NotFoundElementException;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.cloud.tagService.Context;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.v2.InventoryTagsV2;
import org.junit.jupiter.api.*;
import ui.t1.tests.engine.EntitySupplier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static models.cloud.tagService.TagServiceSteps.inventoryTagsV2;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("Дефектолог")
@Feature("Проверка создания дефектов")
public class DefectologTest extends AbstractTagServiceTest {

    private List<Inventory> inventoriesWithoutLinks;
    private final List<Inventory> inventories = new ArrayList<>();

    private final EntitySupplier<Void> init = lazy(() -> {
        final Context ctx = new Context("organizations", "vtb");
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


        StartTask task = StartTask.builder().kwargsParam(StartTask.KwargsParam.builder()
                .taskValidators(Arrays.asList("INV-CTX-INVALID", "INV-ACL-EMPTY", "INV-REQUIRED-ATTRS", "INV_REQUIRED_BOOL_ATTRS",
                        "LINK-CTX-INVALID", "LINK-DUPLICATED-ATTRS-VALUES")).build()).build();
        Assertions.assertTrue(DefectologSteps.tasksCreate(task));
        Waiting.sleep(5000);
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
        int defectId = findDefectIdByInternalName(internalName);
        DefectPage defectPage = readDefectPage(defectId);
        inventories.forEach(inventory ->
                Assertions.assertTrue(defectPage.getPatients().contains(inventory.getId()),
                        String.format("Inventory %s not found in defectPage %d ", inventory.getId(), defectPage.getId())));
    }

    private int findDefectIdByInternalName(String internalName) {
        return DefectologSteps.defectsList().stream()
                .filter(e -> Duration.between(e.getCreatedAt().toInstant(), new Date().toInstant()).getSeconds() < 120)
                .filter(e -> e.getGroup().getInternalName().equals(internalName))
                .findFirst()
                .orElseThrow(NotFoundElementException::new).getId();
    }

    private DefectPage readDefectPage(int defectId) {
        int pageId = DefectologSteps.defectsRead(defectId).getDefectPages().get(0).getId();
        return DefectologSteps.defectPagesRead(pageId);
    }
}
