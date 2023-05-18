package api.cloud.tagService.v1;

import api.cloud.tagService.AbstractInventoryTest;
import core.enums.Role;
import core.helper.http.AssertResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import models.cloud.authorizer.GlobalUser;
import models.cloud.tagService.Filter;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.TagServiceSteps;
import models.cloud.tagService.v1.FilterResultV1Item;
import models.cloud.tagService.v1.InventoryTagsV1;
import models.cloud.tagService.Tag;
import models.cloud.tagService.v1.FilterResultV1;
import models.cloud.tagService.v2.InventoryTagsV2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.keyCloak.KeyCloakSteps;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static models.cloud.tagService.Inventory.DEFAULT_TYPE;
import static models.cloud.tagService.TagServiceSteps.*;

@Epic("Сервис тегов")
@Feature("Фильтр Inventory V1")
public class InventoryFilterV1Test extends AbstractInventoryTest {

    @Test
    @TmsLink("1623836")
    @DisplayName("Inventory. Фильтр V1 по response_tags")
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

    @Test
    @TmsLink("1623780")
    @DisplayName("Inventory. Фильтр V1. По тегу operator = AND")
    void findInventoriesByTagOperatorAnd() {
        List<Tag> tList = generateTags(3);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(0).getId()).inventory(iList.get(0).getId()).value("value_1").build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(1).getId()).inventory(iList.get(0).getId()).value("value_2").build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(0).getId()).inventory(iList.get(1).getId()).value("value_1").build());
        inventoryTagsV1(context, InventoryTagsV1.builder().tag(tList.get(2).getId()).inventory(iList.get(1).getId()).value("value_3").build());

        Filter filter = Filter.builder()
                .tags(new Filter.Tag("AND")
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList("value_1")))
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), Collections.singletonList("value_2"))))
                .build();
        FilterResultV1 filterResult = inventoryFilterV1(context, filter);
        Assertions.assertEquals(1, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertEquals(iList.get(0).getId(), filterResult.getList().get(0).getInventory(), "Нейден неверный inventory");
    }

    @Test
    @TmsLink("1623781")
    @DisplayName("Inventory. Фильтр v1. По тегу operator = OR")
    void findInventoriesByTagOperatorOR() {
        List<Tag> tList = generateTags(3);
        List<Inventory> iList = generateInventories(4);

        inventoryTagsV2(context, iList.get(0).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value_1"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value_2")));
        inventoryTagsV2(context, iList.get(1).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value_1"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value_3")));
        inventoryTagsV2(context, iList.get(2).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value_2"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value_2")));
        inventoryTagsV2(context, iList.get(3).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(2).getKey(), "value_3"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value_5")));

        Filter filter = Filter.builder()
                .tags(new Filter.Tag("OR")
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList("value_2")))
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), Arrays.asList("value_2", "value_5"))))
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(3, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertAll("Не найден inventory",
                () -> Assertions.assertTrue(filterResult.stream().anyMatch(e -> e.getInventory().equals(iList.get(0).getId())), "inventory"),
                () -> Assertions.assertTrue(filterResult.stream().anyMatch(e -> e.getInventory().equals(iList.get(2).getId())), "inventory3"),
                () -> Assertions.assertTrue(filterResult.stream().anyMatch(e -> e.getInventory().equals(iList.get(3).getId())), "inventory4")
        );
    }

    @Test
    @TmsLink("1623766")
    @DisplayName("Inventory. Фильтр v1. excluding_tags. operator = AND")
    void findInventoriesByTagExcludingTagsOperatorAnd() {
        List<Tag> tList = generateTags(3);
        List<Inventory> iList = generateInventories(4);

        inventoryTagsV2(context, iList.get(0).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value_1"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value_4")));
        inventoryTagsV2(context, iList.get(1).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value_1"),
                new InventoryTagsV2.Tag(tList.get(2).getKey(), "value_5")));
        inventoryTagsV2(context, iList.get(2).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value_1"),
                new InventoryTagsV2.Tag(tList.get(2).getKey(), "value_2")));
        inventoryTagsV2(context, iList.get(3).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(2).getKey(), "value_0"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value_5")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .excludingTags(new Filter.Tag("AND")
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList("value_1")))
                        .addFilter(new Filter.Tag.TagFilter(tList.get(2).getKey(), Arrays.asList("value_2", "value_5"))))
                .tags(new Filter.Tag().addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList("value_1"))))
                .contextPathIsnull(false)
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(1, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertEquals(filterResult.getList().get(0).getInventory(), iList.get(0).getId(), "Неверный inventory");
    }

    @Test
    @TmsLink("1623771")
    @DisplayName("Inventory. Фильтр v1. excluding_tags. operator = OR")
    void findInventoriesByTagExcludingTagsOperatorOR() {
        //Генерируем списки тегов и инвентори
        List<Tag> tList = generateTags(3);
        List<Inventory> iList = generateInventories(4);
        //Линкуем список тег:значение к инвенори
        inventoryTagsV2(context, iList.get(0).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value_1"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value_0")));
        inventoryTagsV2(context, iList.get(1).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "value_2"),
                new InventoryTagsV2.Tag(tList.get(2).getKey(), "value_3")));
        inventoryTagsV2(context, iList.get(2).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(2).getKey(), "value_3"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value_1")));
        inventoryTagsV2(context, iList.get(3).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(2).getKey(), "value_0"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "value_1")));
        //Создаем фильтр по тегам указывая excludingTags
        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .excludingTags(new Filter.Tag("OR")
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList("value_1")))
                        .addFilter(new Filter.Tag.TagFilter(tList.get(2).getKey(), Arrays.asList("value_2", "value_3"))))
                .tags(new Filter.Tag("OR")
                        .addFilter(new Filter.Tag.TagFilter(tList.get(2).getKey(), Arrays.asList("value_3", "value_0")))
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList("value_1"))))
                .contextPathIsnull(false)
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(1, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertEquals(filterResult.getList().get(0).getInventory(), iList.get(3).getId(), "Неверный inventory");
    }

    @Test
    @TmsLink("1623778")
    @DisplayName("Inventory. Фильтр v1. Сортировка по ordering")
    void findInventoriesByTagOrdering() {
        List<Tag> tList = generateTags(3);
        List<Inventory> iList = generateInventories(5);

        inventoryTagsV2(context, iList.get(0).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "0"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "3")));
        inventoryTagsV2(context, iList.get(1).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "0"),
                new InventoryTagsV2.Tag(tList.get(2).getKey(), "5")));
        inventoryTagsV2(context, iList.get(2).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "0"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "1")));
        inventoryTagsV2(context, iList.get(3).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "0"),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "2")));
        inventoryTagsV2(context, iList.get(4).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), "0"),
                new InventoryTagsV2.Tag(tList.get(2).getKey(), "4")));

        Filter filter = Filter.builder()
                .tags(new Filter.Tag().addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), "0")))
                .build();
        Filter.Query query = Filter.Query.builder()
                .addOrder(tList.get(1).getKey())
                .addOrderDesc(tList.get(2).getKey())
                .build();

        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter, query);

        Assertions.assertEquals(filterResult.stream().map(FilterResultV1Item::getInventory).collect(Collectors.toList()),
                Arrays.asList(iList.get(2).getId(), iList.get(3).getId(), iList.get(0).getId(), iList.get(1).getId(), iList.get(4).getId()), "Неверный список inventory");
    }

    @Test
    @TmsLink("1623791")
    @DisplayName("Inventory. Фильтр v1 по impersonate")
    void findInventoriesByTagImpersonate() {
        String tagValue = "impersonate";
        List<Tag> tList = generateTags(2);
        List<Inventory> iList = generateInventories(4);

        inventoryTagsV2(context, iList.get(0).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), tagValue),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "all")));
        iList.get(0).updateAcl(Collections.singletonList("all"), null);

        GlobalUser user = GlobalUser.builder().role(Role.CLOUD_ADMIN).build().createObject();
        inventoryTagsV2(context, iList.get(1).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), tagValue),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "user")));
        iList.get(1).updateAcl(null, Collections.singletonList(user.getUsername()));

        inventoryTagsV2(context, iList.get(2).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), tagValue),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "client role")));
        iList.get(2).updateAcl(Collections.singletonList("cloud_day2_roles:test-admin1"), null);

        inventoryTagsV2(context, iList.get(3).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), tagValue),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "realm role")));
        iList.get(3).updateAcl(null, Collections.singletonList("qa-admin1"));

        Filter filter = Filter.builder()
                .impersonate(KeyCloakSteps.getUserInfo(Role.CLOUD_ADMIN))
                .tags(new Filter.Tag().addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), tagValue)))
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(4, filterResult.getList().size(), "Неверное кол-во inventories");
    }

    @Test
    @TmsLink("1623789")
    @DisplayName("Inventory. Фильтр v1 по context_path_isnull")
    void findInventoriesByContextPathIsnull() {
        Filter filterWidthNullPath = Filter.builder()
                .contextPathIsnull(true)
                .allowEmptyTagFilter(true)
                .build();
        Filter filterWithoutNullPath = Filter.builder()
                .contextPathIsnull(false)
                .allowEmptyTagFilter(true)
                .build();
        Assertions.assertTrue(TagServiceSteps.inventoryFilterV1(context, filterWidthNullPath).getMeta().getTotalCount() >
                TagServiceSteps.inventoryFilterV1(context, filterWithoutNullPath).getMeta().getTotalCount(), "(contextPathIsnull = true) <= (contextPathIsnull = false)");
    }

    @Test
    @TmsLink("1623768")
    @DisplayName("Inventory. Фильтр. allow_empty_tag_filter")
    void findInventoriesByAllowEmptyTagFilter() {
        Filter filter = Filter.builder()
                .allowEmptyTagFilter(false)
                .build();
        AssertResponse.run(() -> TagServiceSteps.inventoryFilterV1(context, filter))
                .responseContains("If the \\\"allow_empty_tag_filter\\\" parameter")
                .status(400);
    }

    @Test
    @TmsLink("1623787")
    @DisplayName("Inventory. Фильтр v1. data_sources")
    void findInventoriesByDataSources() {
        String tagValue = "data_sources";
        String dataSourceFirst = "base_cloud_attrs";
        String dataSourceSecond = "empty";

        Tag tag = generateTags(1).get(0);
        List<Inventory> iList = generateInventories(2);

        inventoryTagsV2(context, iList.get(0).getId(),dataSourceFirst, Collections.singletonList(new InventoryTagsV2.Tag(tag.getKey(), tagValue)));
        inventoryTagsV2(context, iList.get(1).getId(),dataSourceSecond, Collections.singletonList(new InventoryTagsV2.Tag(tag.getKey(), tagValue)));

        Filter filterFirst = Filter.builder()
                .tags(new Filter.Tag().addFilter(new Filter.Tag.TagFilter(tag.getKey(), tagValue)))
                .dataSources(Collections.singletonList(dataSourceFirst))
                .build();
        Filter filterSecond = Filter.builder()
                .tags(new Filter.Tag().addFilter(new Filter.Tag.TagFilter(tag.getKey(), tagValue)))
                .dataSources(Arrays.asList(dataSourceSecond, dataSourceFirst))
                .build();

        FilterResultV1 firstResult = TagServiceSteps.inventoryFilterV1(context, filterFirst);
        FilterResultV1 secondResult = TagServiceSteps.inventoryFilterV1(context, filterSecond);
        Assertions.assertAll("Неверное количество иневентори",
                () -> Assertions.assertEquals(1, firstResult.getMeta().getTotalCount()),
                () -> Assertions.assertEquals(2, secondResult.getMeta().getTotalCount()));
        Assertions.assertEquals(iList.get(0).getId(), firstResult.getList().get(0).getInventory(), "Неверные иневентори в фильтре");
    }

    @Test
    @TmsLink("1623775")
    @DisplayName("Inventory. Фильтр v1. inventory_types")
    void findInventoriesByInventoryTypes() {
        String tagValue = "inventory_types";
        String otherType = "cloud_base_item";

        Tag tag = generateTags(1).get(0);
        Inventory firstInventory = Inventory.builder().objectType(DEFAULT_TYPE).context(context).build().createObjectPrivateAccess();
        Inventory secondInventory = Inventory.builder().objectType(otherType).context(context).build().createObjectPrivateAccess();

        inventoryTagsV2(context, firstInventory.getId(),null, Collections.singletonList(new InventoryTagsV2.Tag(tag.getKey(), tagValue)));
        inventoryTagsV2(context, secondInventory.getId(),null, Collections.singletonList(new InventoryTagsV2.Tag(tag.getKey(), tagValue)));

        Filter filterFirst = Filter.builder()
                .tags(new Filter.Tag().addFilter(new Filter.Tag.TagFilter(tag.getKey(), tagValue)))
                .inventoryTypes(Collections.singletonList(DEFAULT_TYPE))
                .build();
        Filter filterSecond = Filter.builder()
                .tags(new Filter.Tag().addFilter(new Filter.Tag.TagFilter(tag.getKey(), tagValue)))
                .inventoryTypes(Arrays.asList(DEFAULT_TYPE, otherType))
                .build();

        FilterResultV1 firstResult = TagServiceSteps.inventoryFilterV1(context, filterFirst);
        FilterResultV1 secondResult = TagServiceSteps.inventoryFilterV1(context, filterSecond);
        Assertions.assertAll("Неверное количество иневентори",
                () -> Assertions.assertEquals(1, firstResult.getMeta().getTotalCount()),
                () -> Assertions.assertEquals(2, secondResult.getMeta().getTotalCount()));
        Assertions.assertEquals(firstInventory.getId(), firstResult.getList().get(0).getInventory(), "Неверные иневентори в фильтре");
    }

    @Test
    @TmsLink("1623767")
    @DisplayName("Inventory. Фильтр v1. inventory_pks")
    void findInventoriesByInventoryPks() {
        List<Inventory> iList = generateInventories(2);

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .inventoryPks(Collections.singletonList(iList.get(0).getId()))
                .build();

        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(1, filterResult.getMeta().getTotalCount(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertEquals(filterResult.getList().get(0).getInventory(), iList.get(0).getId(), "Неверный inventory");
    }

    @Test
    @TmsLink("1623776")
    @DisplayName("Inventory. Фильтр v1 по roles")
    void findInventoriesByRoles() {
        String tagValue = "roles";
        List<Tag> tList = generateTags(2);
        List<Inventory> iList = generateInventories(3);

        inventoryTagsV2(context, iList.get(0).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), tagValue),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "all")));
        iList.get(0).updateAcl(Collections.singletonList("all"), null);

        GlobalUser user = GlobalUser.builder().role(Role.CLOUD_ADMIN).build().createObject();
        inventoryTagsV2(context, iList.get(1).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), tagValue),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "user")));
        iList.get(1).updateAcl(null, Collections.singletonList(user.getUsername()));

        inventoryTagsV2(context, iList.get(2).getId(),null, Arrays.asList(new InventoryTagsV2.Tag(tList.get(0).getKey(), tagValue),
                new InventoryTagsV2.Tag(tList.get(1).getKey(), "realm role")));
        iList.get(2).updateAcl(null, Collections.singletonList("qa-admin1"));

        Filter filter = Filter.builder()
                .roles(Collections.singletonList("qa-admin1"))
                .tags(new Filter.Tag().addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), tagValue)))
                .build();
        FilterResultV1 filterResult = TagServiceSteps.inventoryFilterV1(context, filter);
        Assertions.assertEquals(2, filterResult.getList().size(), "Неверное кол-во inventories");
        Assertions.assertAll("Нет inventory в списке", () -> iList.get(0).inventoryListItemV1(filterResult),
                () -> iList.get(2).inventoryListItemV1(filterResult));
    }
}
