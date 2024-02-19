package tests.routes;

import core.helper.http.Path;

import static core.helper.Configure.KONG_URL;


public class TagServiceAPI implements Api {
    //deprecated
    @Route(method = Method.POST, path = "/v1/{context_type}/{context_id}/inventories/", status = 200)
    public static Path v1InventoriesCreate;

    @Route(method = Method.GET, path = "/v1/{context_type}/{context_id}/inventory-tags/", status = 200)
    public static Path v1InventoryTagsList;

    @Route(method = Method.POST, path = "/v1/{context_type}/{context_id}/inventory-tags/", status = 201)
    public static Path v1InventoryTagsCreate;

    @Route(method = Method.GET, path = "/v1/{context_type}/{context_id}/inventory-tags/{id}/", status = 200)
    public static Path v1InventoryTagsRead;

    @Route(method = Method.GET, path = "/v1/{context_type}/{context_id}/tags/", status = 200)
    public static Path v1TagsList;

    @Route(method = Method.POST, path = "/v1/{context_type}/{context_id}/tags/", status = 201)
    public static Path v1TagsCreate;

    @Route(method = Method.DELETE, path = "/v1/{context_type}/{context_id}/tags/{id}/", status = 204)
    public static Path v1TagsDelete;

    @Route(method = Method.GET, path = "/v1/{context_type}/{context_id}/tags/{id}/", status = 200)
    public static Path v1TagsRead;

    @Route(method = Method.PATCH, path = "/v1/{context_type}/{context_id}/tags/{id}/", status = 200)
    public static Path v1TagsPartialUpdate;

    @Route(method = Method.PUT, path = "/v1/{context_type}/{context_id}/tags/{id}/", status = 200)
    public static Path v1TagsUpdate;

    @Route(method = Method.GET, path = "/v1/{context_type}/{context_id}/tags/{id}/inventories/", status = 200)
    public static Path v1TagsInventories;

    @Route(method = Method.DELETE, path = "/v1/{context_type}/{context_id}/tags/{id}/inventory-tags/", status = 204)
    public static Path v1TagsInventoryTagsDelete;

    @Route(method = Method.PUT, path = "/v1/{context_type}/{context_id}/tags/{id}/inventory-tags/", status = 200)
    public static Path v1TagsInventoryTagsUpdate;

    @Route(method = Method.GET, path = "/v1/{context_type}/{context_id}/tags/{id}/unique-values/", status = 200)
    public static Path v1TagsUniqueValues;

    @Route(method = Method.GET, path = "/v1/clear-cache/", status = 200)
    public static Path v1ClearCacheList;

    @Route(method = Method.GET, path = "/v1/health", status = 200)
    public static Path v1HealthList;

    @Route(method = Method.GET, path = "/v1/inventory-acl/", status = 200)
    public static Path v1InventoryAclList;

    @Route(method = Method.GET, path = "/v1/inventory-acl/{id}/", status = 200)
    public static Path v1InventoryAclRead;

    @Route(method = Method.PATCH, path = "/v1/inventory-acl/{id}/", status = 200)
    public static Path v1InventoryAclPartialUpdate;

    @Route(method = Method.PUT, path = "/v1/inventory-acl/{id}/", status = 200)
    public static Path v1InventoryAclUpdate;

    @Route(method = Method.PUT, path = "/v1/inventory-acl/batch/", status = 200)
    public static Path v1InventoryAclInventoryAclBatchUpdate;

    @Route(method = Method.POST, path = "/v1/lists/", status = 201)
    public static Path v1ListsCreate;

    //deprecated
    @Route(method = Method.POST, path = "/v1/managers/{context_type}/{context_id}/inventories/", status = 200)
    public static Path v1ManagersInventoriesCreate;

    @Route(method = Method.POST, path = "/v1/start-tagging-task/", status = 201)
    public static Path v1StartTaggingTaskCreate;

    @Route(method = Method.POST, path = "/v1/tags-by-keys/", status = 201)
    public static Path v1TagsByKeysCreate;

    @Route(method = Method.GET, path = "/v2/{context_type}/{context_id}/inventories/", status = 200)
    public static Path v2InventoriesList;

    @Route(method = Method.POST, path = "/v2/{context_type}/{context_id}/inventories/", status = 201)
    public static Path v2InventoriesCreate;

    @Route(method = Method.DELETE, path = "/v2/{context_type}/{context_id}/inventories/{id}/", status = 204)
    public static Path v2InventoriesDelete;

    @Route(method = Method.GET, path = "/v2/{context_type}/{context_id}/inventories/{id}/", status = 200)
    public static Path v2InventoriesRead;

    @Route(method = Method.PATCH, path = "/v2/{context_type}/{context_id}/inventories/{id}/", status = 200)
    public static Path v2InventoriesPartialUpdate;

    @Route(method = Method.PUT, path = "/v2/{context_type}/{context_id}/inventories/{id}/", status = 200)
    public static Path v2InventoriesUpdate;

    @Route(method = Method.GET, path = "/v2/{context_type}/{context_id}/inventories/{id}/inventory-tags/", status = 200)
    public static Path v2InventoriesInventoryTagsRead;

    @Route(method = Method.PUT, path = "/v2/{context_type}/{context_id}/inventories/{id}/inventory-tags/", status = 200)
    public static Path v2InventoriesInventoryTagsUpdate;

    @Route(method = Method.DELETE, path = "/v2/{context_type}/{context_id}/inventories/batch/", status = 204)
    public static Path v2InventoriesBatch;

    @Route(method = Method.POST, path = "/v2/{context_type}/{context_id}/inventories/filter/", status = 200)
    public static Path v2InventoriesContextFilterCreate;

    @Route(method = Method.POST, path = "/v2/inventories/filter/", status = 200)
    public static Path v2InventoriesFilterCreate;

    @Route(method = Method.PUT, path = "/v2/admin/inventories/batch/", status = 200)
    public static Path v2AdminInventoriesBatch;

    @Route(method = Method.GET, path = "/v2/lists/", status = 200)
    public static Path v2ListsList;

    @Route(method = Method.POST, path = "/v2/lists/", status = 201)
    public static Path v2ListsCreate;

    @Route(method = Method.DELETE, path = "/v2/lists/{name}/", status = 204)
    public static Path v2ListsDelete;

    @Route(method = Method.GET, path = "/v2/lists/{name}/", status = 200)
    public static Path v2ListsRead;

    @Route(method = Method.PATCH, path = "/v2/lists/{name}/", status = 200)
    public static Path v2ListsPartialUpdate;

    @Route(method = Method.PUT, path = "/v2/lists/{name}/", status = 200)
    public static Path v2ListsUpdate;

    @Route(method = Method.POST, path = "/v2/lists/{name}/elements/", status = 201)
    public static Path v2ListsElementCreate;

    @Route(method = Method.DELETE, path = "/v2/lists/{name}/elements/{key}/", status = 204)
    public static Path v2ListsElementsDelete;

    @Route(method = Method.PATCH, path = "/v2/lists/{name}/elements/{key}/", status = 200)
    public static Path v2ListsElementsPartialUpdate;

    @Route(method = Method.PUT, path = "/v2/lists/{name}/elements/{key}/", status = 200)
    public static Path v2ListsElementsUpdate;

    @Route(method = Method.GET, path = "/v2/lists/{name}/export/", status = 200)
    public static Path v2ListsExportList;

    @Route(method = Method.PUT, path = "/v2/lists/import/", status = 201)
    public static Path v2ListsImportList;

    @Override
    public String url() {
        return KONG_URL + "tags-service/api";
    }
}
