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
import org.junit.jupiter.api.*;
import ui.t1.tests.engine.EntitySupplier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("Дефектолог")
@Feature("Проверка создания дефектов 'Объект инфраструктуры'")
public class DefectologTest extends AbstractTagServiceTest {

    private final List<Inventory> inventories = new ArrayList<>();
    private final EntitySupplier<Void> init = lazy(() -> {
        for (int i = 0; i < 2; i++)
            inventories.add(Inventory.builder().context(new Context("organizations", "vtb")).contextPath(context.getContextPath()
                    .replaceFirst("/folder", "/ folder")).objectType("vm").build().createObjectPrivateAccess());
        StartTask task = StartTask.builder().kwargsParam(StartTask.KwargsParam.builder()
                .taskValidators(Arrays.asList("INV-CTX-INVALID", "INV-ACL-EMPTY", "INV-REQUIRED-ATTRS", "INV_REQUIRED_BOOL_ATTRS")).build()).build();
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
        assertDefectPageContainsInventories("INV-CTX-INVALID");
    }

    @Test
    @DisplayName("Проверка группы INV-ACL-EMPTY")
    void invAclEmpty() {
        assertDefectPageContainsInventories("INV-ACL-EMPTY");
    }

    @Test
    @DisplayName("Проверка группы INV-REQUIRED-ATTRS")
    void invRequiredAttrs() {
        assertDefectPageContainsInventories("INV-REQUIRED-ATTRS");
    }

    @Test
    @DisplayName("Проверка группы INV_REQUIRED_BOOL_ATTRS")
    void invRequiredBoolAttrs() {
        assertDefectPageContainsInventories("INV_REQUIRED_BOOL_ATTRS");
    }

    private void assertDefectPageContainsInventories(String internalName) {
        int defectId = findDefectIdByInternalName(internalName);
        DefectPage defectPage = readDefectPage(defectId);
        inventories.forEach(inventory ->
                Assertions.assertTrue(defectPage.getPatients().contains(inventory.getId()),
                        String.format("Inventory %s not found in defectPage %d ", inventory.getId(), defectPage.getId())));
    }

    private int findDefectIdByInternalName(String internalName) {
        return DefectologSteps.defectsList().stream()
                .filter(e -> Duration.between(e.getCreatedAt().toInstant(), new Date().toInstant()).getSeconds() < 80)
                .filter(e -> e.getGroup().getInternalName().equals(internalName))
                .findFirst()
                .orElseThrow(NotFoundElementException::new).getId();
    }

    private DefectPage readDefectPage(int defectId) {
        int pageId = DefectologSteps.defectsRead(defectId).getDefectPages().get(0).getId();
        return DefectologSteps.defectPagesRead(pageId);
    }
}
