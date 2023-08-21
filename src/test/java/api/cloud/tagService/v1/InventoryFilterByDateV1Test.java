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
import models.cloud.tagService.v1.FilterResultV1;
import models.cloud.tagService.v1.InventoryTagsV1;
import models.cloud.tagService.v2.InventoryTagsV2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static models.cloud.tagService.TagServiceSteps.inventoryTagsV1;
import static models.cloud.tagService.TagServiceSteps.inventoryTagsV2;

@Epic("Сервис тегов")
@Feature("Фильтр Inventory V1")
public class InventoryFilterByDateV1Test extends AbstractTagServiceTest {

    //GTE(">="), GT(">"), LT("<"), LTE("<=");
    @Test
    @TmsLink("1623788")
    @DisplayName("Inventory. Фильтр V1. created_at = lt & gte")
    void findInventoriesByCreatedAtLtAndGte() {
        List<Tag> tList = generateTags(2);
        Inventory inventoryFirst = generateInventories(1).get(0);
        Waiting.sleep(5000);
        Inventory inventorySecond = generateInventories(1).get(0);
        Waiting.sleep(5000);
        Inventory inventoryThird = generateInventories(1).get(0);
        String RequiredValue = randomName("unique_value");
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(0).getId()).inventory(inventoryFirst.getId()).value(RequiredValue).build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(1).getId()).inventory(inventoryFirst.getId()).value("value_first").build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(0).getId()).inventory(inventorySecond.getId()).value(RequiredValue).build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(1).getId()).inventory(inventorySecond.getId()).value("value_second").build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(0).getId()).inventory(inventoryThird.getId()).value(RequiredValue).build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(1).getId()).inventory(inventoryThird.getId()).value("value_third").build());

        Filter filter = Filter.builder()
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList(RequiredValue))))
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);

        filter = Filter.builder()
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList(RequiredValue))))
                .inventoryFilter("created_at", new Filter.InventoryAttrs(Arrays.asList(
                        Filter.InventoryAttrs.InventoryFilter.builder().lookup("lt")
                                .value(inventoryThird.inventoryListItemV1(filterResult).getCreatedAt().toString()).build(),
                        Filter.InventoryAttrs.InventoryFilter.builder().lookup("gte")
                                .value(inventoryFirst.inventoryListItemV1(filterResult).getCreatedAt().toString()).build()
                )))
                .build();

        FilterResultV1 findInventories = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(2, findInventories.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertFalse(findInventories.stream().anyMatch(i -> i.getInventory().equals(inventoryThird.getId())), "Неверный список inventory");
    }

    @Test
    @TmsLink("1623777")
    @DisplayName("Inventory. Фильтр V1. created_at = lte & gt")
    void findInventoriesByCreatedAtLteAndGt() {
        List<Tag> tList = generateTags(2);
        Inventory inventoryFirst = generateInventories(1).get(0);
        Waiting.sleep(5000);
        Inventory inventorySecond = generateInventories(1).get(0);
        Waiting.sleep(5000);
        Inventory inventoryThird = generateInventories(1).get(0);
        String RequiredValue = randomName("unique_value");
        inventoryTagsV2(context, inventoryFirst.getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), RequiredValue),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value_first")));
        inventoryTagsV2(context, inventorySecond.getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), RequiredValue),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value_second")));
        inventoryTagsV2(context, inventoryThird.getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), RequiredValue),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value_third")));

        Filter filter = Filter.builder()
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList(RequiredValue))))
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);

        filter = Filter.builder()
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList(RequiredValue))))
                .inventoryFilter("created_at", new Filter.InventoryAttrs(Arrays.asList(
                        Filter.InventoryAttrs.InventoryFilter.builder().lookup("lte")
                                .value(inventoryThird.inventoryListItemV1(filterResult).getCreatedAt().toString()).build(),
                        Filter.InventoryAttrs.InventoryFilter.builder().lookup("gt")
                                .value(inventoryFirst.inventoryListItemV1(filterResult).getCreatedAt().toString()).build()
                )))
                .build();

        FilterResultV1 findInventories = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(2, findInventories.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertFalse(findInventories.stream().anyMatch(i -> i.getInventory().equals(inventoryFirst.getId())), "Неверный список inventory");
    }

    @Test
    @TmsLink("1623790")
    @DisplayName("Inventory. Фильтр V1. updated_at = lt & gte")
    void findInventoriesByUpdatedAtLtAndGte() {
        List<Tag> tList = generateTags(2);
        Inventory inventoryFirst = generateInventories(1).get(0);
        Inventory inventorySecond = generateInventories(1).get(0);
        Inventory inventoryThird = generateInventories(1).get(0);
        String RequiredValue = randomName("unique_value");
        inventoryTagsV2(context, inventoryFirst.getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), RequiredValue),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value_first")));
        Waiting.sleep(5000);
        inventoryTagsV2(context, inventorySecond.getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), RequiredValue),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value_second")));
        Waiting.sleep(5000);
        inventoryTagsV2(context, inventoryThird.getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), RequiredValue),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value_third")));

        Filter filter = Filter.builder()
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList(RequiredValue))))
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);

        filter = Filter.builder()
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList(RequiredValue))))
                .inventoryFilter("updated_at", new Filter.InventoryAttrs(Arrays.asList(
                        Filter.InventoryAttrs.InventoryFilter.builder().lookup("lt")
                                .value(inventoryThird.inventoryListItemV1(filterResult).getUpdatedAt().toString()).build(),
                        Filter.InventoryAttrs.InventoryFilter.builder().lookup("gte")
                                .value(inventoryFirst.inventoryListItemV1(filterResult).getUpdatedAt().toString()).build()
                )))
                .build();

        FilterResultV1 findInventories = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(2, findInventories.getMeta().getTotalCount(), "Неверное кол-во отфильтрованных inventories");
        Assertions.assertFalse(findInventories.stream().anyMatch(i -> i.getInventory().equals(inventoryThird.getId())), "Неверный список inventory");
    }

    @Test
    @TmsLink("1623782")
    @DisplayName("Inventory. Фильтр V1. updated_at = lte & gt")
    void findInventoriesByUpdatedAtLteAndGt() {
        List<Tag> tList = generateTags(2);
        Inventory inventoryFirst = generateInventories(1).get(0);
        Inventory inventorySecond = generateInventories(1).get(0);
        Inventory inventoryThird = generateInventories(1).get(0);
        String RequiredValue = randomName("unique_value");
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(0).getId()).inventory(inventoryFirst.getId()).value(RequiredValue).build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(1).getId()).inventory(inventoryFirst.getId()).value("value_first").build());
        Waiting.sleep(5000);
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(0).getId()).inventory(inventorySecond.getId()).value(RequiredValue).build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(1).getId()).inventory(inventorySecond.getId()).value("value_second").build());
        Waiting.sleep(5000);
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(0).getId()).inventory(inventoryThird.getId()).value(RequiredValue).build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(1).getId()).inventory(inventoryThird.getId()).value("value_third").build());

        Filter filter = Filter.builder()
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList(RequiredValue))))
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);

        filter = Filter.builder()
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList(RequiredValue))))
                .inventoryFilter("updated_at", new Filter.InventoryAttrs(Arrays.asList(
                        Filter.InventoryAttrs.InventoryFilter.builder().lookup("lte")
                                .value(inventoryThird.inventoryListItemV1(filterResult).getUpdatedAt().toString()).build(),
                        Filter.InventoryAttrs.InventoryFilter.builder().lookup("gt")
                                .value(inventoryFirst.inventoryListItemV1(filterResult).getUpdatedAt().toString()).build()
                )))
                .build();

        FilterResultV1 findInventories = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(2, findInventories.getMeta().getTotalCount(), "Неверное кол-во отфильтрованных inventories");
        Assertions.assertFalse(findInventories.stream().anyMatch(i -> i.getInventory().equals(inventoryFirst.getId())), "Неверный список inventory");
    }
}
