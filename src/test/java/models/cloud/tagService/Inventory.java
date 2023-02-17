package models.cloud.tagService;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import core.enums.Role;
import core.helper.Configure;
import core.helper.StringUtils;
import core.helper.http.Http;
import lombok.*;
import lombok.experimental.SuperBuilder;
import models.Entity;
import steps.resourceManager.ResourceManagerSteps;

import java.util.*;

@SuperBuilder @Getter @ToString @AllArgsConstructor @NoArgsConstructor
public class Inventory extends ContextEntity{
    String id;
    String objectType;
    String contextPath;

    @Override
    public Entity init() {
        super.init();
        contextPath = Optional.ofNullable(contextPath).orElse(ResourceManagerSteps.getProjectPath(contextType, contextId) + "/");
        id = Optional.ofNullable(id).orElse(UUID.randomUUID().toString());
        objectType = Optional.ofNullable(objectType).orElse("dictionary");
        return this;
    }

    @Override
    protected void create() {
        Inventory inventory = new Http(Configure.TagService)
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(toJson())
                .post("/v2/{}/{}/inventories/", contextType, contextId)
                .assertStatus(201)
                .extractAs(Inventory.class);
        StringUtils.copyAvailableFields(inventory, this);
    }

    @Override
    protected void delete() {
        new Http(Configure.TagService)
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(toJson())
                .delete("/v2/{}/{}/inventories/{}/", contextType, contextId, id)
                .assertStatus(204);
    }

    public void linkTags(String dataSource, List<InventoryTags.Tag> tags){
        InventoryTags inventoryTags = new InventoryTags(dataSource, tags);
        new Http(Configure.TagService)
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(serialize(inventoryTags))
                .put("/v2/{}/{}/inventories/{}/inventory-tags/", contextType, contextId, id)
                .assertStatus(200);
    }

    public List<ListItem> byFilter(String dataSource, List<InventoryTags.Tag> tags){
        InventoryTags inventoryTags = new InventoryTags(dataSource, tags);
        new Http(Configure.TagService)
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(serialize(inventoryTags))
                .post("/v2/{}/{}/inventories/filter/", contextType, contextId)
                .assertStatus(200);
        return null;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class InventoryTags {
        String dataSource;
        List<Tag> tags = new ArrayList<>();

        public InventoryTags(String dataSource, List<Tag> tagList){
            this.dataSource = dataSource;
            tagList.forEach(e -> tags.add(new Tag(e.getTag(), e.getValue())));
        }

        @Data
        @AllArgsConstructor
        public static class Tag {
            String tag;
            String value;
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ListItem {
        String inventory;
        String contextPath;
        Map<String, String> tags;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Filter {
        List<String> inventoryPks;
        Tag tags;

        @Data
        @AllArgsConstructor
        public static class Tag {
        }
    }

}
