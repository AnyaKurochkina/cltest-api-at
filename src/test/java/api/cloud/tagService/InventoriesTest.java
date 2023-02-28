package api.cloud.tagService;

import models.cloud.tagService.Inventory;
import models.cloud.tagService.Tag;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Arrays;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventoriesTest {

    @Test
    @Order(1)
    void name() {
        Tag tag1 = Tag.builder().key("tag1").contextId("proj-pkvckn08w9").build().createObject();
        Tag tag2 = Tag.builder().key("tag2").contextId("proj-pkvckn08w9").build().createObject();
        Inventory inventory = Inventory.builder().contextId("proj-pkvckn08w9").build().createObject();
        inventory.linkTags(null, Arrays.asList(new Inventory.InventoryTags.Tag(tag1.getKey(), "value_1"),
                new Inventory.InventoryTags.Tag(tag2.getKey(), "value_2")));
    }


}
