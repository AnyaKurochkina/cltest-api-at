package api.cloud.tagService.v2;

import api.cloud.tagService.AbstractInventoryTest;
import core.helper.http.AssertResponse;
import core.helper.http.QueryBuilder;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.Project;
import models.cloud.tagService.*;
import models.cloud.tagService.v1.InventoryTagsV1;
import models.cloud.tagService.v2.InventoryTagListV2Page;
import models.cloud.tagService.v2.InventoryV2Page;
import models.cloud.tagService.v2.InventoryTagListV2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static models.cloud.tagService.TagServiceSteps.inventoryFilterV2;
import static models.cloud.tagService.TagServiceSteps.inventoryTagsV1;

@Epic("Сервис тегов")
@Feature("Inventory тесты")
public class InventoryV2Test extends AbstractInventoryTest {

    @Test
    @TmsLink("")
    @DisplayName("Inventory V2. Delete batch.")
    void inventoriesDeleteBatch() {
        List<Inventory> inventories = new ArrayList<>();
        Project project = Project.builder().projectName("API. inventoriesDeleteBatch").build().createObject();
        Context context = Context.byId(project.getId());
        for (int i = 0; i < 3; i++)
            inventories.add(Inventory.builder().context(context).build().createObjectPrivateAccess());
        TagServiceSteps.inventoriesDeleteBatchV2(context, inventories.stream().map(Inventory::getId).collect(Collectors.toList()));
        InventoryV2Page inventoryList = TagServiceSteps.inventoriesListV2(context, new QueryBuilder().add("with_deleted", false));
        Assertions.assertEquals(0, inventoryList.stream()
                .filter(e -> Objects.nonNull(e.getContextPath()))
                .filter(e -> !e.getContextPath().equals(""))
                .count(), "Неверное кол-во inventories");
    }

    @Test
    @TmsLink("")
    @DisplayName("Inventory V2. Inventories list. with_deleted")
    void inventoriesList() {
        InventoryV2Page inventoriesWithoutDeleted = TagServiceSteps.inventoriesListV2(context, new QueryBuilder().add("with_deleted", false));
        InventoryV2Page inventoriesWithDeleted = TagServiceSteps.inventoriesListV2(context, new QueryBuilder().add("with_deleted", true));
        Assertions.assertTrue(inventoriesWithoutDeleted.getMeta().getTotalCount() <
                inventoriesWithDeleted.getMeta().getTotalCount(), "Неверное кол-во inventories");
    }

    @Test
    @TmsLink("")
    @DisplayName("Inventory V2. GET inventory-tags")
    void inventoryTagsList() {
        String tagValue = "inventoryTagsList";
        List<Tag> tags = generateTags(2);
        Inventory inventory = generateInventories(1).get(0);
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tags.get(0).getId()).inventory(inventory.getId()).value(tagValue).build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tags.get(1).getId()).inventory(inventory.getId()).value(tagValue).build());
        InventoryTagListV2Page inventoryTagList =  TagServiceSteps.inventoryTagListV2(context, inventory.getId());
        Assertions.assertAll("Проверка inventoryTagList",
                () -> Assertions.assertEquals(2, inventoryTagList.getMeta().getTotalCount()),
                () -> Assertions.assertEquals(2, inventoryTagList.stream().map(e -> e.getInventory().getId().equals(inventory.getId())).count()),
                () -> Assertions.assertTrue(inventoryTagList.stream().anyMatch(e -> e.getTag().equals(tags.get(0)))),
                () -> Assertions.assertTrue(inventoryTagList.stream().anyMatch(e -> e.getTag().equals(tags.get(1)))));
    }

    @Test
    @TmsLink("")
    @DisplayName("Inventory V2. Delete")
    void inventoriesDelete() {
        Inventory inventory = generateInventories(1).get(0);
        inventory.delete();
        AssertResponse.run(() -> TagServiceSteps.inventoryTagListV2(context, inventory.getId())).status(404);
    }

    @Test
    @TmsLink("")
    @DisplayName("Inventory V2. Replace ContextPatch")
    void inventoryReplaceContextPatch() {
        Inventory inventory = generateInventories(1).get(0);
        Project project = Project.builder().projectName("API. inventoryReplaceContextPatch").build().createObject();
        Context context = Context.byId(project.getId());
        AssertResponse.run(() -> TagServiceSteps.inventoryTagListV2(context, inventory.getId())).status(404);
    }
}
