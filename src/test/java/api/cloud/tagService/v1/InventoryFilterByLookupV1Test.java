package api.cloud.tagService.v1;

import api.cloud.tagService.AbstractTagServiceTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.tagService.Filter;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.Tag;
import models.cloud.tagService.TagServiceSteps;
import models.cloud.tagService.v1.FilterResultV1;
import models.cloud.tagService.v2.InventoryTagsV2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static models.cloud.tagService.TagServiceSteps.inventoryTagsV2;

@Epic("Сервис тегов")
@Feature("Фильтр Inventory V1")
public class InventoryFilterByLookupV1Test extends AbstractTagServiceTest {

    @Test
    @TmsLink("1623784")
    @DisplayName("Inventory. Фильтр v1. lookup = exact")
    void findInventoriesByLookupExact() {
        List<Tag> tList = generateTags(3);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "Value")));
        inventoryTagsV2(context, iList.get(1).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "value", "exact")))
                .contextPathIsnull(false)
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(1, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertEquals(iList.get(1).getId(), filterResult.getList().get(0).getInventory(), "Нейден неверный inventory");
    }

    @Test
    @TmsLink("1623772")
    @DisplayName("Inventory. Фильтр v1. lookup = iexact")
    void findInventoriesByLookupIExact() {
        List<Tag> tList = generateTags(3);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "Value")));
        inventoryTagsV2(context, iList.get(1).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "value", "iexact")))
                .contextPathIsnull(false)
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(2, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
    }

    @Test
    @TmsLink("1623785")
    @DisplayName("Inventory. Фильтр v1. lookup = icontains")
    void findInventoriesByLookupIContains() {
        List<Tag> tList = generateTags(3);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "_Value")));
        inventoryTagsV2(context, iList.get(1).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "values")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "value", "icontains")))
                .contextPathIsnull(false)
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(2, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
    }

    @Test
    @TmsLink("1623774")
    @DisplayName("Inventory. Фильтр v1. lookup = startswith")
    void findInventoriesByLookupStartswith () {
        List<Tag> tList = generateTags(3);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "Value")));
        inventoryTagsV2(context, iList.get(1).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "val", "startswith")))
                .contextPathIsnull(false)
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(1, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertEquals(iList.get(1).getId(), filterResult.getList().get(0).getInventory(), "Нейден неверный inventory");
    }

    @Test
    @TmsLink("1623779")
    @DisplayName("Inventory. Фильтр v1. lookup = istartswith")
    void findInventoriesByLookupIStartswith () {
        List<Tag> tList = generateTags(3);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "Value")));
        inventoryTagsV2(context, iList.get(1).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "val", "istartswith")))
                .contextPathIsnull(false)
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(2, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
    }

    @Test
    @TmsLink("1623783")
    @DisplayName("Inventory. Фильтр v1. lookup = endswith")
    void findInventoriesByLookupEndswith () {
        List<Tag> tList = generateTags(3);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "valuE")));
        inventoryTagsV2(context, iList.get(1).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "lue", "endswith")))
                .contextPathIsnull(false)
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(1, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertEquals(iList.get(1).getId(), filterResult.getList().get(0).getInventory(), "Нейден неверный inventory");
    }

    @Test
    @TmsLink("1623773")
    @DisplayName("Inventory. Фильтр v1. lookup = iendswith")
    void findInventoriesByLookupIEndswith () {
        List<Tag> tList = generateTags(3);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "valuE")));
        inventoryTagsV2(context, iList.get(1).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "lue", "iendswith")))
                .contextPathIsnull(false)
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(2, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
    }

    @Test
    @TmsLink("1623769")
    @DisplayName("Inventory. Фильтр v1. lookup = regex")
    void findInventoriesByLookupRegex() {
        List<Tag> tList = generateTags(3);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "valuE")));
        inventoryTagsV2(context, iList.get(1).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "[a-z]$", "regex")))
                .contextPathIsnull(false)
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(1, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertEquals(iList.get(1).getId(), filterResult.getList().get(0).getInventory(), "Нейден неверный inventory");
    }

    @Test
    @TmsLink("1623786")
    @DisplayName("Inventory. Фильтр v1. lookup = iregex")
    void findInventoriesByLookupIRegex() {
        List<Tag> tList = generateTags(3);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "valuE")));
        inventoryTagsV2(context, iList.get(1).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "[a-z]$", "iregex")))
                .contextPathIsnull(false)
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(2, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
    }
}
