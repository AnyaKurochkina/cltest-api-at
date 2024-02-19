package models.cloud.tagService;

import core.enums.Role;
import core.helper.http.Http;
import core.helper.http.QueryBuilder;
import models.cloud.tagService.v1.*;
import models.cloud.tagService.v2.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

import static models.Entity.serialize;
import static tests.routes.TagServiceAPI.*;


public class TagServiceSteps {

    public static FilterResultV1 inventoryFilterV1(Context context, Filter filter, QueryBuilder query) {
        query.add("page", 1);
        return Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(serialize(filter))
                .api(v1InventoriesCreate, context.getType(), context.getId(), query)
                .extractAllPages(FilterResultV1.class);
    }

    public static FilterResultV1 inventoryFilterV1(Context context, Filter filter) {
        return inventoryFilterV1(context, filter, new QueryBuilder());
    }

    public static FilterResultV2Page inventoryFilterV2(Context context, Filter filter, QueryBuilder query) {
        query.add("page", 1);
        return Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(serialize(filter))
                .api(v2InventoriesContextFilterCreate, context.getType(), context.getId(), query)
                .extractAllPages(FilterResultV2Page.class);
    }

    public static FilterResultV2Page inventoryFilterV2(Context context, Filter filter) {
        return inventoryFilterV2(context, filter, new QueryBuilder());
    }

    public static FilterResultV2Page inventoryFilterV2(Filter filter, QueryBuilder query) {
        query.add("page", 1);
        return Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(serialize(filter))
                .api(v2InventoriesFilterCreate, query)
                .extractAllPages(FilterResultV2Page.class);
    }

    public static FilterResultV2Page inventoryFilterV2(Filter filter) {
        return inventoryFilterV2(filter, new QueryBuilder());
    }

    public static void inventoryTagsV2(Context context, String inventoryId, String dataSource, List<InventoryTagsV2.Tag> tags) {
        InventoryTagsV2 inventoryTags = new InventoryTagsV2(dataSource, tags);
        Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(serialize(inventoryTags))
                .api(v2InventoriesInventoryTagsUpdate, context.getType(), context.getId(), inventoryId);
    }

    public static void inventoriesDeleteBatchV2(Context context, List<String> inventories) {
        Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(new JSONObject().put("inventories", inventories))
                .api(v2InventoriesBatch, context.getType(), context.getId());
    }

    public static InventoryV2Page inventoriesListV2(Context context, QueryBuilder query) {
        return Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .api(v2InventoriesList, context.getType(), context.getId(), query)
                .extractAs(InventoryV2Page.class);
    }

    public static Inventory updateV2Put(Inventory inventory) {
        return Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(inventory.toJson())
                .api(v2InventoriesUpdate, inventory.getContext().getType(), inventory.getContext().getId(), inventory.getId())
                .extractAs(Inventory.class);
    }

    public static List<String> getTagsUniqueValuesV1(Context context, String id) {
        return Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .api(v1TagsUniqueValues, context.getType(), context.getId(), id)
                .jsonPath()
                .getList("list", String.class);
    }

    public static void tagsInventoryTagsDeleteV1(Context context, String tagId, List<Inventory> inventories) {
        Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(new JSONObject().put("inventories",new JSONArray(inventories
                        .stream().map(Inventory::getId).collect(Collectors.toList()))))
                .api(v1TagsInventoryTagsDelete, context.getType(), context.getId(), tagId);
    }

    public static TagsInventoriesV1 tagsInventoriesV1(Context context, String tagId) {
        return Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .api(v1TagsInventories, context.getType(), context.getId(), tagId)
                .extractAllPages(TagsInventoriesV1.class);
    }

    public static Tag v1TagsPartialUpdate(Tag tag, String tagId) {
        return Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(serialize(tag))
                .api(v1TagsPartialUpdate, tag.getContext().getType(), tag.getContext().getId(), tagId)
                .extractAs(Tag.class);
    }

    public static Tag v1TagsUpdate(Tag tag, String tagId) {
        return Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(serialize(tag))
                .api(v1TagsUpdate, tag.getContext().getType(), tag.getContext().getId(), tagId)
                .extractAs(Tag.class);
    }

    public static Tag v1TagsRead(Context context, String id) {
        return Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .api(v1TagsUpdate, context.getType(), context.getId(), id)
                .extractAs(Tag.class);
    }

    public static CreateOrUpdateInventoryTags tagsInventoryTagsUpdateV1(Context context, String tagId, CreateOrUpdateLinksWithInventoriesRequest request) {
        return Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(serialize(request))
                .api(v1TagsInventoryTagsUpdate, context.getType(), context.getId(), tagId)
                .extractAs(CreateOrUpdateInventoryTags.class);
    }

    public static List<PutInventoryRequest.PutInventory> updateInventoriesV2(PutInventoryRequest request) {
        return Http.builder()
                .setRole(Role.SUPERADMIN)
                .body(serialize(request))
                .api(v2AdminInventoriesBatch)
                .jsonPath()
                .getList("list", PutInventoryRequest.PutInventory.class);
    }

    public static void updateV2Path(Inventory inventory) {
        Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(inventory.toJson())
                .api(v2InventoriesPartialUpdate, inventory.getContext().getType(), inventory.getContext().getId(), inventory.getId())
                .extractAs(Inventory.class);
    }

    public static Inventory getInventoryV2(Context context, String id) {
        return Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .api(v2InventoriesRead, context.getType(), context.getId(), id)
                .extractAs(Inventory.class);
    }

    public static InventoryTagListV2Page inventoryTagListV2(Context context, String inventory) {
        return Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .api(v2InventoriesInventoryTagsRead, context.getType(), context.getId(), inventory)
                .extractAs(InventoryTagListV2Page.class);
    }

    public static void inventoryTagsV1(Context context, InventoryTagsV1 inventoryTags) {
        Http.builder()
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(serialize(inventoryTags))
                .api(v1InventoryTagsCreate, context.getType(), context.getId());
    }
}
