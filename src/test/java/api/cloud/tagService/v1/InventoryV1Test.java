package api.cloud.tagService.v1;

import api.cloud.tagService.AbstractInventoryTest;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.Tag;
import models.cloud.tagService.TagServiceSteps;
import models.cloud.tagService.v1.InventoryTagsV1;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static models.cloud.tagService.TagServiceSteps.inventoryTagsV1;

@Epic("Сервис тегов")
@Feature("Inventory тесты")
public class InventoryV1Test extends AbstractInventoryTest {

    @Test
    @TmsLink("1665584")
    @DisplayName("Inventory V1. Получение уникальных значений для тега")
    void getTagsUniqueValuesV1() {
        Tag tag = generateTags(1).get(0);
        List<Inventory> iList = generateInventories(3);
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tag.getId()).inventory(iList.get(0).getId()).value("value_1").build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tag.getId()).inventory(iList.get(1).getId()).value("value_2").build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tag.getId()).inventory(iList.get(2).getId()).value("value_1").build());
        List<String> values = TagServiceSteps.getTagsUniqueValuesV1(context, tag.getId());
        AssertUtils.assertEqualsList(Arrays.asList("value_1", "value_2"), values);
    }
}
