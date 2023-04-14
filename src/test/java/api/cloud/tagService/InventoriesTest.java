package api.cloud.tagService;

import com.mifmif.common.regex.Generex;
import models.cloud.authorizer.Project;
import models.cloud.tagService.Filter;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.Tag;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventoriesTest {

    Project project = Project.builder().isForOrders(true).build().createObject();

    private String randomName(String prefix) {
        return new Generex("AT-" + prefix + "-[a-z]{6}").random();
    }

    private List<Tag> generateTags(int count) {
        List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < count; i++)
            tags.add(Tag.builder().key(randomName("tag" + i)).contextId(project.getId()).build().createObject());
        return tags;
    }

    private List<Inventory> generateInventories(int count) {
        List<Inventory> inventories = new ArrayList<>();
        for (int i = 0; i < count; i++)
            inventories.add(Inventory.builder().contextId(project.getId()).build().createObjectPrivateAccess());
        return inventories;
    }


    @Test
    @Disabled
    @Order(1)
    void name() {
        Tag tag1 = Tag.builder().key("tag1").contextId("proj-pkvckn08w9").build().createObject();
        Tag tag2 = Tag.builder().key("tag2").contextId("proj-pkvckn08w9").build().createObject();
        Inventory inventory = Inventory.builder().contextId("proj-pkvckn08w9").build().createObject();
        inventory.linkTags(null, Arrays.asList(new Inventory.InventoryTags.Tag(tag1.getKey(), "value_1"),
                new Inventory.InventoryTags.Tag(tag2.getKey(), "value_2")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag().addFilter(new Filter.Tag.TagFilter("tag1", Collections.singletonList("value_1"))))
                .contextPathIsnull(false)
                .build();
        inventory.byFilter(filter);

    }

    @Test
    @DisplayName("Inventory. Фильтр. По тегу operator = AND")
    @Order(1)
    void findInventoriesByTagOperatorAnd() {
        List<Tag> tList = generateTags(3);
        List<Inventory> iList = generateInventories(2);

        iList.get(0).linkTags(null, Arrays.asList(new Inventory.InventoryTags.Tag(tList.get(0).getKey(), "value_1"),
                new Inventory.InventoryTags.Tag(tList.get(1).getKey(), "value_2")));
        iList.get(1).linkTags(null, Arrays.asList(new Inventory.InventoryTags.Tag(tList.get(0).getKey(), "value_1"),
                new Inventory.InventoryTags.Tag(tList.get(2).getKey(), "value_3")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag("AND")
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList("value_1")))
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), Collections.singletonList("value_2"))))
                .contextPathIsnull(false)
                .build();
        List<Inventory.ListItem> inventories = iList.get(0).byFilter(filter);
        Assertions.assertEquals(1, inventories.size(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertEquals(iList.get(0).getId(), inventories.get(0).getInventory(), "Нейден неверный inventory");
    }

    @Test
    @DisplayName("Inventory. Фильтр. По тегу operator = OR")
    @Order(3)
    void findInventoriesByTagOperatorOR() {
        List<Tag> tList = generateTags(3);
        List<Inventory> iList = generateInventories(4);

        iList.get(0).linkTags(null, Arrays.asList(new Inventory.InventoryTags.Tag(tList.get(0).getKey(), "value_1"),
                new Inventory.InventoryTags.Tag(tList.get(1).getKey(), "value_2")));
        iList.get(1).linkTags(null, Arrays.asList(new Inventory.InventoryTags.Tag(tList.get(0).getKey(), "value_1"),
                new Inventory.InventoryTags.Tag(tList.get(1).getKey(), "value_3")));
        iList.get(2).linkTags(null, Arrays.asList(new Inventory.InventoryTags.Tag(tList.get(0).getKey(), "value_2"),
                new Inventory.InventoryTags.Tag(tList.get(1).getKey(), "value_2")));
        iList.get(3).linkTags(null, Arrays.asList(new Inventory.InventoryTags.Tag(tList.get(2).getKey(), "value_3"),
                new Inventory.InventoryTags.Tag(tList.get(1).getKey(), "value_5")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag("OR")
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList("value_2")))
                        .addFilter(new Filter.Tag.TagFilter(tList.get(1).getKey(), Arrays.asList("value_2", "value_5"))))
                .contextPathIsnull(false)
                .build();
        List<Inventory.ListItem> inventories = iList.get(0).byFilter(filter);
        Assertions.assertEquals(3, inventories.size(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertAll("Не найден inventory",
                () -> Assertions.assertTrue(inventories.stream().anyMatch(e -> e.getInventory().equals(iList.get(0).getId())), "inventory"),
                () -> Assertions.assertTrue(inventories.stream().anyMatch(e -> e.getInventory().equals(iList.get(2).getId())), "inventory3"),
                () -> Assertions.assertTrue(inventories.stream().anyMatch(e -> e.getInventory().equals(iList.get(3).getId())), "inventory4")
        );
    }

    @Test
    @DisplayName("Inventory. excluding_tags. По тегу operator = AND")
    @Order(4)
    void findInventoriesByTagExcludingTagsOperatorAnd() {
        List<Tag> tList = generateTags(3);
        List<Inventory> iList = generateInventories(4);

        iList.get(0).linkTags(null, Arrays.asList(new Inventory.InventoryTags.Tag(tList.get(0).getKey(), "value_1"),
                new Inventory.InventoryTags.Tag(tList.get(1).getKey(), "value_4")));
        iList.get(1).linkTags(null, Arrays.asList(new Inventory.InventoryTags.Tag(tList.get(0).getKey(), "value_1"),
                new Inventory.InventoryTags.Tag(tList.get(2).getKey(), "value_5")));
        iList.get(2).linkTags(null, Arrays.asList(new Inventory.InventoryTags.Tag(tList.get(0).getKey(), "value_1"),
                new Inventory.InventoryTags.Tag(tList.get(2).getKey(), "value_2")));
        iList.get(3).linkTags(null, Arrays.asList(new Inventory.InventoryTags.Tag(tList.get(2).getKey(), "value_0"),
                new Inventory.InventoryTags.Tag(tList.get(1).getKey(), "value_5")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .excludingTags(new Filter.Tag("AND")
                        .addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList("value_1")))
                        .addFilter(new Filter.Tag.TagFilter(tList.get(2).getKey(), Arrays.asList("value_2", "value_5"))))
                .tags(new Filter.Tag().addFilter(new Filter.Tag.TagFilter(tList.get(0).getKey(), Collections.singletonList("value_1"))))
                .contextPathIsnull(false)
                .build();
        List<Inventory.ListItem> inventories = iList.get(0).byFilter(filter);
        Assertions.assertEquals(1, inventories.size(), "Неверное кол-во отфильтрованых inventories");
        Assertions.assertEquals(inventories.get(0).getInventory(), iList.get(0).getId(), "Неверный inventory");
    }
}
