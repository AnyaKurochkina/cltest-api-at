package api.cloud.tagService.v2.filter;

import api.cloud.tagService.AbstractTagServiceTest;
import core.utils.Waiting;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.tagService.Filter;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.Tag;
import models.cloud.tagService.TagServiceSteps;
import models.cloud.tagService.v2.FilterResultV2Page;
import models.cloud.tagService.v2.InventoryTagsV2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static models.cloud.tagService.TagServiceSteps.inventoryTagsV2;

@Epic("Сервис тегов")
@Feature("Фильтр Inventory V2")
public class InventoryFilterByDateV2Test extends AbstractTagServiceTest {

    //GTE(">="), GT(">"), LT("<"), LTE("<=");
    @Test
    @TmsLink("")
    @DisplayName("Inventory. Фильтр V2. created_at = lt & gte")
    void findInventoriesByCreatedAtLtAndGte() {
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
        FilterResultV2Page filterResult = TagServiceSteps.inventoryFilterV2(filter);

        filter = Filter.builder()
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList(RequiredValue))))
                .inventoryFilter("created_at", new Filter.InventoryAttrs(Arrays.asList(
                        Filter.InventoryAttrs.InventoryFilter.builder().lookup("lt")
                                .value(inventoryThird.inventoryListItemV2(filterResult).getCreatedAt().toString()).build(),
                        Filter.InventoryAttrs.InventoryFilter.builder().lookup("gte")
                                .value(inventoryFirst.inventoryListItemV2(filterResult).getCreatedAt().toString()).build()
                )))
                .build();

        FilterResultV2Page findInventories = TagServiceSteps.inventoryFilterV2(filter);
        Assertions.assertEquals(2, findInventories.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertFalse(findInventories.stream().anyMatch(i -> i.getInventory().equals(inventoryThird.getId())), "Неверный список inventory");
    }

    @Test
    @TmsLink("")
    @DisplayName("Inventory. Фильтр V2. created_at = lte & gt")
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
        FilterResultV2Page filterResult = TagServiceSteps.inventoryFilterV2(filter);

        filter = Filter.builder()
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList(RequiredValue))))
                .inventoryFilter("created_at", new Filter.InventoryAttrs(Arrays.asList(
                        Filter.InventoryAttrs.InventoryFilter.builder().lookup("lte")
                                .value(inventoryThird.inventoryListItemV2(filterResult).getCreatedAt().toString()).build(),
                        Filter.InventoryAttrs.InventoryFilter.builder().lookup("gt")
                                .value(inventoryFirst.inventoryListItemV2(filterResult).getCreatedAt().toString()).build()
                )))
                .build();

        FilterResultV2Page findInventories = TagServiceSteps.inventoryFilterV2(filter);
        Assertions.assertEquals(2, findInventories.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertFalse(findInventories.stream().anyMatch(i -> i.getInventory().equals(inventoryFirst.getId())), "Неверный список inventory");
    }

    @Test
    @TmsLink("")
    @DisplayName("Inventory. Фильтр V2. updated_at = lt & gte")
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
        FilterResultV2Page filterResult = TagServiceSteps.inventoryFilterV2(filter);

        filter = Filter.builder()
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList(RequiredValue))))
                .inventoryFilter("updated_at", new Filter.InventoryAttrs(Arrays.asList(
                        Filter.InventoryAttrs.InventoryFilter.builder().lookup("lt")
                                .value(inventoryThird.inventoryListItemV2(filterResult).getUpdatedAt().toString()).build(),
                        Filter.InventoryAttrs.InventoryFilter.builder().lookup("gte")
                                .value(inventoryFirst.inventoryListItemV2(filterResult).getUpdatedAt().toString()).build()
                )))
                .build();

        FilterResultV2Page findInventories = TagServiceSteps.inventoryFilterV2(filter);
        Assertions.assertEquals(2, findInventories.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertFalse(findInventories.stream().anyMatch(i -> i.getInventory().equals(inventoryThird.getId())), "Неверный список inventory");
    }

    @Test
    @TmsLink("")
    @DisplayName("Inventory. Фильтр V2. updated_at = lte & gt")
    void findInventoriesByUpdatedAtLteAndGt() {
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
        FilterResultV2Page filterResult = TagServiceSteps.inventoryFilterV2(filter);

        filter = Filter.builder()
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList(RequiredValue))))
                .inventoryFilter("updated_at", new Filter.InventoryAttrs(Arrays.asList(
                        Filter.InventoryAttrs.InventoryFilter.builder().lookup("lte")
                                .value(inventoryThird.inventoryListItemV2(filterResult).getUpdatedAt().toString()).build(),
                        Filter.InventoryAttrs.InventoryFilter.builder().lookup("gt")
                                .value(inventoryFirst.inventoryListItemV2(filterResult).getUpdatedAt().toString()).build()
                )))
                .build();

        FilterResultV2Page findInventories = TagServiceSteps.inventoryFilterV2(filter);
        Assertions.assertEquals(2, findInventories.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertFalse(findInventories.stream().anyMatch(i -> i.getInventory().equals(inventoryFirst.getId())), "Неверный список inventory");
    }
}
