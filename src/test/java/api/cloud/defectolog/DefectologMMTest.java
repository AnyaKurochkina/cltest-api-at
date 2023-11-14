package api.cloud.defectolog;

import api.cloud.defectolog.models.DefectPage;
import api.cloud.defectolog.models.StartTask;
import api.cloud.defectolog.steps.DefectologSteps;
import api.cloud.tagService.AbstractTagServiceTest;
import api.cloud.tagService.v2.InventoryFilterV2Test;
import core.exception.NotFoundElementException;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import models.cloud.tagService.Context;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.TagServiceSteps;
import models.cloud.tagService.v2.InventoryTagsV2;
import org.junit.jupiter.api.*;
import ui.t1.tests.engine.EntitySupplier;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static models.cloud.tagService.TagServiceSteps.inventoryTagListV2;
import static models.cloud.tagService.TagServiceSteps.inventoryTagsV2;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Epic("Дефектолог")
@Feature("Проверка создания дефектов")
public class DefectologMMTest extends AbstractTagServiceTest {

    @Test
    @DisplayName("Тег sys_item_maintenance_mode. not_exists_attrs")
    void notExistsAttrs() {
        Inventory inventory = generateInventories(1).get(0);
        Date dateFrom = Date.from(Instant.ofEpochMilli(inventoryTagListV2(context, inventory.getId()).getList().get(0)
                .getUpdatedAt().getTime()).plusSeconds(1));

        StartTask task = StartTask.builder().kwargsParam(StartTask.KwargsParam.builder()
                .taskValidators(Collections.singletonList("INV-MM-ATTRS")).build()).build();
        Assertions.assertTrue(DefectologSteps.tasksCreate(task));

        int defectId = findDefectIdByInternalName("INV-MM-ATTRS");
        DefectPage defectPage = readDefectPage(defectId);

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
                .filter(e -> Duration.between(e.getCreatedAt().toInstant(), new Date().toInstant()).getSeconds() < 10)
                .filter(e -> e.getGroup().getInternalName().equals(internalName))
                .findFirst()
                .orElseThrow(NotFoundElementException::new).getId();
    }

    private DefectPage readDefectPage(int defectId) {
        int pageId = DefectologSteps.defectsRead(defectId).getDefectPages().get(0).getId();
        return DefectologSteps.defectPagesRead(pageId);
    }
}
