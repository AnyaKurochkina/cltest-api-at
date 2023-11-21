package api.cloud.tagService.v2;

import api.cloud.tagService.AbstractTagServiceTest;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.tagService.Filter;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.Tag;
import models.cloud.tagService.TagServiceSteps;
import models.cloud.tagService.v2.FilterResultV2;
import models.cloud.tagService.v2.InventoryTagsV2;
import models.cloud.tagService.v2.PutInventoryRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Collections;

import static models.cloud.tagService.TagServiceSteps.inventoryFilterV2;
import static models.cloud.tagService.TagServiceSteps.inventoryTagsV2;

@Epic("Сервис тегов")
@Feature("Проверка Inventory V2")
public class InventoryUpdateV2Test extends AbstractTagServiceTest {

    @Test
    @TmsLink("")
    @DisplayName("Inventory. V2. Обновление даты при PUT inventory-tags")
    void checkUpdateAfterPutRequestDateInventory() {
        String tagValue = randomName();
        Tag tag = generateTags(1).get(0);
        Inventory inventory = generateInventories(1).get(0);
        Waiting.sleep(1000);
        inventoryTagsV2(context, inventory.getId(),null, Collections.singletonList(new InventoryTagsV2.Tag(tag.getKey(), tagValue)));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .inventoryPks(Collections.singletonList(inventory.getId()))
                .build();

        FilterResultV2 filterResult = inventoryFilterV2(context, filter).getList().get(0);
        Assertions.assertTrue(filterResult.getCreatedAt().isBefore(filterResult.getUpdatedAt()));
    }

    @Test
    @TmsLink("")
    @DisplayName("Inventory. V2. Обновление даты при PUT batch")
    void checkUpdateAfterPutBatchRequestDateInventory() {
        String tagValue = randomName();
        Tag tag = generateTags(1).get(0);
        Inventory inventory = generateInventories(1).get(0);
        inventoryTagsV2(context, inventory.getId(),null, Collections.singletonList(new InventoryTagsV2.Tag(tag.getKey(), tagValue)));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .inventoryPks(Collections.singletonList(inventory.getId()))
                .build();
        ZonedDateTime updatedAt = inventoryFilterV2(context, filter).getList().get(0).getUpdatedAt();

        Waiting.sleep(1000);
        inventoryTagsV2(context, inventory.getId(),null, Collections.singletonList(new InventoryTagsV2.Tag(tag.getKey(), randomName())));
        ZonedDateTime updatedAtAfterUpdate = inventoryFilterV2(context, filter).getList().get(0).getUpdatedAt();
        Assertions.assertTrue(updatedAt.isBefore(updatedAtAfterUpdate));
    }

    @Test
    @TmsLink("")
    @DisplayName("Inventory. V2. Обновление даты при DELETE inventory-tags")
    void checkUpdateAfterDeleteRequestDateInventory() {
        String tagValue = randomName();
        Tag tag = generateTags(1).get(0);
        Inventory inventory = generateInventories(1).get(0);
        inventoryTagsV2(context, inventory.getId(),null, Collections.singletonList(new InventoryTagsV2.Tag(tag.getKey(), tagValue)));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .inventoryPks(Collections.singletonList(inventory.getId()))
                .build();
        ZonedDateTime updatedAt = inventoryFilterV2(context, filter).getList().get(0).getUpdatedAt();

        Waiting.sleep(1000);
        PutInventoryRequest.PutInventory putInventory = PutInventoryRequest.PutInventory.builder()
                .contextPath(context.getContextPath())
                .id(inventory.getId())
                .skipDefects(false)
                .build();
        PutInventoryRequest request = PutInventoryRequest.builder().inventory(putInventory).build();
        TagServiceSteps.updateInventoriesV2(request);
        ZonedDateTime updatedAtAfterUpdate = inventoryFilterV2(context, filter).getList().get(0).getUpdatedAt();
        Assertions.assertTrue(updatedAt.isBefore(updatedAtAfterUpdate));
    }
}
