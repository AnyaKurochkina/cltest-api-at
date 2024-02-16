package api.cloud.tagService.v2.filter.context;

import api.cloud.tagService.AbstractTagServiceTest;
import core.utils.AssertUtils;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.tagService.Filter;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.Tag;
import models.cloud.tagService.TagServiceSteps;
import models.cloud.tagService.v2.FilterResultV2;
import models.cloud.tagService.v2.FilterResultV2Page;
import models.cloud.tagService.v2.InventoryTagsV2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static models.cloud.tagService.TagServiceSteps.inventoryTagsV2;

@Epic("Сервис тегов")
@Feature("Контекстный фильтр Inventory V2")
public class InventoryContextFilterByLookupV2Test extends AbstractTagServiceTest {

    @Test
    @TmsLink("1623699")
    @DisplayName("Inventory. Контекстный фильтр V2. lookup = exact")
    void findInventoriesByLookupExact() {
        List<Tag> tList = generateTags(2);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "Value")));
        inventoryTagsV2(context, iList.get(1).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "value", "exact")))
                .contextPathIsnull(false)
                .build();
        FilterResultV2Page filterResult = TagServiceSteps.inventoryFilterV2(context, filter);
        Assertions.assertEquals(1, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованных inventories");
        Assertions.assertEquals(iList.get(1).getId(), filterResult.getList().get(0).getInventory(), "Найден неверный inventory");
    }

    @Test
    @TmsLink("1623700")
    @DisplayName("Inventory. Контекстный фильтр V2. lookup = iexact")
    void findInventoriesByLookupIExact() {
        List<Tag> tList = generateTags(2);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "Value")));
        inventoryTagsV2(context, iList.get(1).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "value", "iexact")))
                .contextPathIsnull(false)
                .build();
        FilterResultV2Page filterResult = TagServiceSteps.inventoryFilterV2(context, filter);
        Assertions.assertEquals(2, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованных inventories");
    }

    @Test
    @TmsLink("1623702")
    @DisplayName("Inventory. Контекстный фильтр V2. lookup = icontains")
    void findInventoriesByLookupIContains() {
        List<Tag> tList = generateTags(2);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "_Value")));
        inventoryTagsV2(context, iList.get(1).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "values")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "value", "icontains")))
                .contextPathIsnull(false)
                .build();
        FilterResultV2Page filterResult = TagServiceSteps.inventoryFilterV2(context, filter);
        Assertions.assertEquals(2, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованных inventories");
    }

    @Test
    @TmsLink("1623704")
    @DisplayName("Inventory. Контекстный фильтр V2. lookup = startswith")
    void findInventoriesByLookupStartswith() {
        List<Tag> tList = generateTags(2);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "Value")));
        inventoryTagsV2(context, iList.get(1).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "val", "startswith")))
                .contextPathIsnull(false)
                .build();
        FilterResultV2Page filterResult = TagServiceSteps.inventoryFilterV2(context, filter);
        Assertions.assertEquals(1, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованных inventories");
        Assertions.assertEquals(iList.get(1).getId(), filterResult.getList().get(0).getInventory(), "Найден неверный inventory");
    }

    @Test
    @TmsLink("1623705")
    @DisplayName("Inventory. Контекстный фильтр V2. lookup = istartswith")
    void findInventoriesByLookupIStartswith() {
        List<Tag> tList = generateTags(2);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "Value")));
        inventoryTagsV2(context, iList.get(1).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "val", "istartswith")))
                .contextPathIsnull(false)
                .build();
        FilterResultV2Page filterResult = TagServiceSteps.inventoryFilterV2(context, filter);
        Assertions.assertEquals(2, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованных inventories");
    }

    @Test
    @TmsLink("1623710")
    @DisplayName("Inventory. Контекстный фильтр V2. lookup = endswith")
    void findInventoriesByLookupEndswith() {
        List<Tag> tList = generateTags(2);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "valuE")));
        inventoryTagsV2(context, iList.get(1).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "lue", "endswith")))
                .contextPathIsnull(false)
                .build();
        FilterResultV2Page filterResult = TagServiceSteps.inventoryFilterV2(context, filter);
        Assertions.assertEquals(1, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованных inventories");
        Assertions.assertEquals(iList.get(1).getId(), filterResult.getList().get(0).getInventory(), "Найден неверный inventory");
    }

    @Test
    @TmsLink("1623715")
    @DisplayName("Inventory. Контекстный фильтр V2. lookup = iendswith")
    void findInventoriesByLookupIEndswith() {
        List<Tag> tList = generateTags(2);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "valuE")));
        inventoryTagsV2(context, iList.get(1).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "lue", "iendswith")))
                .contextPathIsnull(false)
                .build();
        FilterResultV2Page filterResult = TagServiceSteps.inventoryFilterV2(context, filter);
        Assertions.assertEquals(2, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованных inventories");
    }

    @Test
    @TmsLink("1623719")
    @DisplayName("Inventory. Контекстный фильтр V2. lookup = regex")
    void findInventoriesByLookupRegex() {
        List<Tag> tList = generateTags(2);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "valuE")));
        inventoryTagsV2(context, iList.get(1).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "[a-z]$", "regex")))
                .contextPathIsnull(false)
                .build();
        FilterResultV2Page filterResult = TagServiceSteps.inventoryFilterV2(context, filter);
        Assertions.assertEquals(1, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованных inventories");
        Assertions.assertEquals(iList.get(1).getId(), filterResult.getList().get(0).getInventory(), "Найден неверный inventory");
    }

    @Test
    @TmsLink("1623720")
    @DisplayName("Inventory. Контекстный фильтр V2. lookup = iregex")
    void findInventoriesByLookupIRegex() {
        List<Tag> tList = generateTags(2);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "valuE")));
        inventoryTagsV2(context, iList.get(1).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag()
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "[a-z]$", "iregex")))
                .contextPathIsnull(false)
                .build();
        FilterResultV2Page filterResult = TagServiceSteps.inventoryFilterV2(context, filter);
        Assertions.assertEquals(2, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованных inventories");
    }

    @Test
    @TmsLink("")
    @DisplayName("Inventory. Контекстный фильтр V2. lookup = inner_filter")
    void findInventoriesByLookupInnerFilter() {
        List<Tag> tList = generateTags(2);
        List<Inventory> iList = generateInventories(3);

        inventoryTagsV2(context, iList.get(0).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "test")));
        inventoryTagsV2(context, iList.get(1).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "tesT")));
        inventoryTagsV2(context, iList.get(2).getId(), null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "test2")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag().setTagOperator("OR")
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), "test2", "iexact"))
                        .addInnerFilter("AND", Arrays.asList(new Filter.Tag.TagFilter(tList.get(1).getKey(), "t", "endswith"),
                                new Filter.Tag.TagFilter(tList.get(1).getKey(), "t", "startswith"))))
                .contextPathIsnull(false)
                .build();
        FilterResultV2Page filterResult = TagServiceSteps.inventoryFilterV2(context, filter);
        Assertions.assertEquals(2, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованных inventories");
        AssertUtils.assertEqualsList(Arrays.asList(iList.get(0).getId(), iList.get(2).getId()),
                filterResult.stream().map(FilterResultV2::getInventory).collect(Collectors.toList()));
    }
}
