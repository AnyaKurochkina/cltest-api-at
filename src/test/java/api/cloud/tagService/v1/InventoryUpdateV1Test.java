package api.cloud.tagService.v1;

import api.cloud.tagService.AbstractTagServiceTest;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.tagService.Filter;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.Tag;
import models.cloud.tagService.TagServiceSteps;
import models.cloud.tagService.v1.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;

import static models.cloud.tagService.TagServiceSteps.*;

@Epic("Сервис тегов")
@Feature("Проверка Inventory V1")
public class InventoryUpdateV1Test extends AbstractTagServiceTest {

    @Test
    @TmsLink("")
    @DisplayName("Inventory. V1. Обновление даты при POST inventory-tags")
    void checkUpdateAfterPostRequestDateInventory() {
        String tagValue = randomName();
        Tag tag = generateTags(1).get(0);
        Inventory inventory = generateInventories(1).get(0);
        Waiting.sleep(1000);
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tag.getId()).inventory(inventory.getId()).value(tagValue).build());

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .inventoryPks(Collections.singletonList(inventory.getId()))
                .build();

        FilterResultV1Item resultV1 = inventoryFilterV1(context, filter).getList().get(0);
        Assertions.assertTrue(resultV1.getCreatedAt().before(resultV1.getUpdatedAt()));
    }

    @Test
    @TmsLink("")
    @DisplayName("Inventory. V1. Обновление даты при PUT inventory-tags")
    void checkUpdateAfterPutRequestDateInventory() {
        String tagValue = randomName();
        Tag tag = generateTags(1).get(0);
        Inventory inventory = generateInventories(1).get(0);
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tag.getId()).inventory(inventory.getId()).value(tagValue).build());

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .inventoryPks(Collections.singletonList(inventory.getId()))
                .build();
        Date updatedAt = inventoryFilterV1(context, filter).getList().get(0).getUpdatedAt();

        Waiting.sleep(1000);
        CreateOrUpdateLinksWithInventoriesRequest request = CreateOrUpdateLinksWithInventoriesRequest.builder()
                .inventory(new CreateOrUpdateLinksWithInventoriesRequest.InventoryValueRequest(inventory.getId(), randomName()))
                .build();
        TagServiceSteps.tagsInventoryTagsUpdateV1(context, tag.getId(), request);
        Date updatedAtAfterUpdate = inventoryFilterV1(context, filter).getList().get(0).getUpdatedAt();
        Assertions.assertTrue(updatedAt.before(updatedAtAfterUpdate));
    }

    @Test
    @TmsLink("")
    @DisplayName("Inventory. V1. Обновление даты при DELETE inventory-tags")
    void checkUpdateAfterDeleteRequestDateInventory() {
        String tagValue = randomName();
        Tag tag = generateTags(1).get(0);
        Inventory inventory = generateInventories(1).get(0);
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tag.getId()).inventory(inventory.getId()).value(tagValue).build());

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .inventoryPks(Collections.singletonList(inventory.getId()))
                .build();
        Date updatedAt = inventoryFilterV1(context, filter).getList().get(0).getUpdatedAt();

        Waiting.sleep(1000);
        TagServiceSteps.tagsInventoryTagsDeleteV1(context, tag.getId(), Collections.singletonList(inventory));
        Date updatedAtAfterUpdate = inventoryFilterV1(context, filter).getList().get(0).getUpdatedAt();
        Assertions.assertTrue(updatedAt.before(updatedAtAfterUpdate));
    }
}
