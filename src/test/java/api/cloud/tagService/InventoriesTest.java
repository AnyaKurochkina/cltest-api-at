package api.cloud.tagService;

import io.qameta.allure.Description;
import models.cloud.authorizer.Project;
import models.cloud.tagService.Filter;
import models.cloud.tagService.Inventory;
import models.cloud.tagService.Tag;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Collections;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventoriesTest {

    Project project = Project.builder().isForOrders(true).build().createObject();

    @Test
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
        Tag tag1 = Tag.builder().key("tag1").contextId(project.getId()).build().createObject();
        Tag tag2 = Tag.builder().key("tag2").contextId(project.getId()).build().createObject();
        Tag tag3 = Tag.builder().key("tag3").contextId(project.getId()).build().createObject();

        Inventory inventory = Inventory.builder().contextId(project.getId()).build().createObjectPrivateAccess();
        Inventory inventory2 = Inventory.builder().contextId(project.getId()).build().createObjectPrivateAccess();

        inventory.linkTags(null, Arrays.asList(new Inventory.InventoryTags.Tag(tag1.getKey(), "value_1"),
                new Inventory.InventoryTags.Tag(tag2.getKey(), "value_2")));
        inventory2.linkTags(null, Arrays.asList(new Inventory.InventoryTags.Tag(tag1.getKey(), "value_1"),
                new Inventory.InventoryTags.Tag(tag3.getKey(), "value_3")));

        Filter filter = Filter.builder()
                .allowEmptyTagFilter(true)
                .tags(new Filter.Tag("AND")
                        .addFilter(new Filter.Tag.TagFilter("tag1", Collections.singletonList("value_1")))
                        .addFilter(new Filter.Tag.TagFilter("tag2", Collections.singletonList("value_2"))))
                .contextPathIsnull(false)
                .build();
        inventory.byFilter(filter);

    }


}
