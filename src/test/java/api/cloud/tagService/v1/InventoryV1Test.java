package api.cloud.tagService.v1;

import api.cloud.tagService.AbstractInventoryTest;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.Tag;
import models.cloud.tagService.TagServiceSteps;
import models.cloud.tagService.v1.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static models.cloud.tagService.TagServiceSteps.inventoryTagsV1;

@Epic("Сервис тегов")
@Feature("Inventory тесты")
public class InventoryV1Test extends AbstractInventoryTest {

    @Test
    @TmsLink("1665584")
    @DisplayName("Tag V1. Получение уникальных значений для тега")
    void getTagsUniqueValuesV1() {
        Tag tag = generateTags(1).get(0);
        List<Inventory> iList = generateInventories(3);
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tag.getId()).inventory(iList.get(0).getId()).value("value_1").build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tag.getId()).inventory(iList.get(1).getId()).value("value_2").build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tag.getId()).inventory(iList.get(2).getId()).value("value_1").build());
        List<String> values = TagServiceSteps.getTagsUniqueValuesV1(context, tag.getId());
        AssertUtils.assertEqualsList(Arrays.asList("value_1", "value_2"), values);
    }

    @Test
    @TmsLink("1676840")
    @DisplayName("Tag V1. Удаление связей тег-объект инфраструктуры")
    void tagsInventoryTagsDeleteV1() {
        Tag tag = generateTags(1).get(0);
        List<Inventory> iList = generateInventories(2);
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tag.getId()).inventory(iList.get(0).getId()).value("value_1").build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tag.getId()).inventory(iList.get(1).getId()).value("value_2").build());
        TagServiceSteps.tagsInventoryTagsDeleteV1(context, tag.getId(), iList);
        TagsInventoriesV1 tagsInventories = TagServiceSteps.tagsInventoriesV1(context, tag.getId());
        Assertions.assertEquals(0, tagsInventories.getMeta().getTotalCount());
    }

    @Test
    @TmsLink("1676843")
    @DisplayName("Tag V1. Получение объектов инфраструктуры для тега")
    void tagsInventoriesV1() {
        Tag tag = generateTags(1).get(0);
        List<Inventory> iList = generateInventories(2);
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tag.getId()).inventory(iList.get(0).getId()).value("value_1").build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tag.getId()).inventory(iList.get(1).getId()).value("value_2").build());
        TagsInventoriesV1 tagsInventories = TagServiceSteps.tagsInventoriesV1(context, tag.getId());
        Assertions.assertEquals(2, tagsInventories.getMeta().getTotalCount());
        AssertUtils.assertEqualsList(iList.stream().map(Inventory::getId).collect(Collectors.toList()),
                tagsInventories.stream().map(TagsInventoriesV1Item::getInventory).collect(Collectors.toList()));
    }

    @Test
    @org.junit.jupiter.api.Tag("health_check")
    @TmsLink("1676844")
    @DisplayName("Tag V1. Работа со связями тег-объект инфраструктуры")
    void tagsInventoryTagsUpdateV1() {
        Tag tag = generateTags(1).get(0);
        List<Inventory> iList = generateInventories(2);
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tag.getId()).inventory(iList.get(0).getId()).value("value_1").build());

        CreateOrUpdateLinksWithInventoriesRequest request = CreateOrUpdateLinksWithInventoriesRequest.builder()
                .inventory(new CreateOrUpdateLinksWithInventoriesRequest.InventoryValueRequest(iList.get(0).getId(), "value_2"))
                .inventory(new CreateOrUpdateLinksWithInventoriesRequest.InventoryValueRequest(iList.get(1).getId(), "value_1"))
                .build();
        CreateOrUpdateInventoryTags createOrUpdateInventoryTags = TagServiceSteps.tagsInventoryTagsUpdateV1(context, tag.getId(), request);

        CreateOrUpdateInventoryTags createOrUpdateInventoryTagsExpected = CreateOrUpdateInventoryTags.builder()
                .addCreatedDataSet(new CreateOrUpdateInventoryTags.InventoryTagWithoutTagInfo(iList.get(1).getId(), tag.getId(), "value_1"))
                .addUpdatedDataSet(new CreateOrUpdateInventoryTags.InventoryTagWithoutTagInfo(iList.get(0).getId(), tag.getId(), "value_2"))
                .build();
        Assertions.assertEquals(createOrUpdateInventoryTagsExpected, createOrUpdateInventoryTags);
    }

    @Test
    @TmsLink("1676845")
    @DisplayName("Tag V1. Редактирование тега PATH")
    void v1TagsPartialUpdate() {
        List<Tag> tags = generateTags(2);
        Tag request = Tag.builder().context(context).value("value_1").parent(tags.get(1).getId()).build();
        tags.get(0).setValue("value_1");
        tags.get(0).setParent(tags.get(1).getId());
        Assertions.assertAll("Проверка полученного tag",
                () -> Assertions.assertEquals(tags.get(0), TagServiceSteps.v1TagsUpdate(request, tags.get(0).getId())),
                () -> Assertions.assertEquals(tags.get(0), TagServiceSteps.v1TagsRead(context, tags.get(0).getId())));
    }

    @Test
    @TmsLink("1676846")
    @DisplayName("Tag V1. Редактирование тега PUT")
    void v1TagsUpdate() {
        List<Tag> tags = generateTags(2);
        Tag request = Tag.builder().context(context).value("value_1").parent(tags.get(1).getId()).build();
        tags.get(0).setValue("value_1");
        tags.get(0).setParent(tags.get(1).getId());
        Assertions.assertAll("Проверка полученного tag",
                () -> Assertions.assertEquals(tags.get(0), TagServiceSteps.v1TagsUpdate(request, tags.get(0).getId())),
                () -> Assertions.assertEquals(tags.get(0), TagServiceSteps.v1TagsRead(context, tags.get(0).getId())));
    }
}
