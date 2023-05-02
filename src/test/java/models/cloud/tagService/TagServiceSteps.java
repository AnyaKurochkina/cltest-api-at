package models.cloud.tagService;

import core.enums.Role;
import core.helper.Configure;
import core.helper.http.Http;
import core.helper.http.QueryBuilder;
import models.cloud.tagService.v1.FilterResultV1;
import models.cloud.tagService.v1.InventoryTagsV1;
import models.cloud.tagService.v2.FilterResultV2;
import models.cloud.tagService.v2.InventoryTagsV2;

import java.util.List;

import static models.Entity.serialize;


public class TagServiceSteps {

    public static FilterResultV1 inventoryFilterV1(Context context, Filter filter, QueryBuilder query) {
        query.add("page", 1);
        return new Http(Configure.TagService)
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(serialize(filter))
                .post("/v1/{}/{}/inventories/{}", context.getType(), context.getId(), query)
                .assertStatus(200)
                .extractAllPages(FilterResultV1.class);
    }

    public static FilterResultV1 inventoryFilterV1(Context context, Filter filter) {
        return inventoryFilterV1(context, filter, new QueryBuilder());
    }

    public static FilterResultV2 inventoryFilterV2(Context context, Filter filter, QueryBuilder query) {
        query.add("page", 1);
        return new Http(Configure.TagService)
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(serialize(filter))
                .post("/v2/{}/{}/inventories/filter/{}", context.getType(), context.getId(), query)
                .assertStatus(200)
                .extractAllPages(FilterResultV2.class);
    }

    public static FilterResultV2 inventoryFilterV2(Context context, Filter filter) {
        return inventoryFilterV2(context, filter, new QueryBuilder());
    }

    public static void inventoryTagsV2(Context context, String inventoryId, String dataSource, List<InventoryTagsV2.Tag> tags) {
        InventoryTagsV2 inventoryTags = new InventoryTagsV2(dataSource, tags);
        new Http(Configure.TagService)
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(serialize(inventoryTags))
                .put("/v2/{}/{}/inventories/{}/inventory-tags/", context.getType(), context.getId(), inventoryId)
                .assertStatus(200);
    }

    public static void inventoryTagsV1(Context context, InventoryTagsV1 inventoryTags) {
        new Http(Configure.TagService)
                .setRole(Role.TAG_SERVICE_ADMIN)
                .body(serialize(inventoryTags))
                .post("/v1/{}/{}/inventory-tags/", context.getType(), context.getId())
                .assertStatus(201);
    }
}
