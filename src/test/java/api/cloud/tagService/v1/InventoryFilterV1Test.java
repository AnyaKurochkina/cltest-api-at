package api.cloud.tagService.v1;

import api.cloud.tagService.AbstractInventoryTest;
import models.cloud.tagService.Filter;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.v1.InventoryTagsV1;
import models.cloud.tagService.v2.InventoryTagsV2;
import models.cloud.tagService.Tag;
import models.cloud.tagService.v1.FilterResultV1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static models.cloud.tagService.TagServiceSteps.*;

public class InventoryFilterV1Test extends AbstractInventoryTest {

    @Test
    @DisplayName("Inventory. Фильтр по response_tags")
    void findInventoriesByResponseTags() {
        String tagValue = "response_tags";
        List<Tag> tList = generateTags(2);
        Inventory inventory = generateInventories(1).get(0);

        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(0).getId()).inventory(inventory.getId()).value(tagValue).build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(1).getId()).inventory(inventory.getId()).value(tagValue).build());

        Filter filter = Filter.builder()
                .tags(new Filter.Tag().addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), tagValue)))
                .allowEmptyTagFilter(true)
                .responseTags(Collections.singletonList(tList.get(0).getKey()))
                .build();
        FilterResultV1 filterResult = inventoryFilterV1(context, filter);
        Assertions.assertEquals(filterResult.getList().get(0).getTags().get(0).getTagKey(), tList.get(0).getKey(), "Неверные inventory");
    }
}
