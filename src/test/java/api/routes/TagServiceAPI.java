package api.routes;

import core.helper.http.Api;

public class TagServiceAPI implements Routes {
    @Route(method = Method.GET, path = "/v1/clear-cache/", status = 200)
    public static Api v1ClearCacheList;
    @Route(method = Method.GET, path = "/v1/health", status = 200)
    public static Api v1HealthList;
    @Route(method = Method.GET, path = "/v1/inventory-acl/", status = 200)
    public static Api v1InventoryAclList;
    @Route(method = Method.PUT, path = "/v1/inventory-acl/batch/", status = 200)
    public static Api v1InventoryAclInventoryAclBatchUpdate;
    @Route(method = Method.GET, path = "/v1/inventory-acl/{id}/", status = 200)
    public static Api v1InventoryAclRead;
    @Route(method = Method.PUT, path = "/v1/inventory-acl/{id}/", status = 200)
    public static Api v1InventoryAclUpdate;
    @Route(method = Method.PATCH, path = "/v1/inventory-acl/{id}/", status = 200)
    public static Api v1InventoryAclPartialUpdate;
    @Route(method = Method.POST, path = "/v1/lists/", status = 201)
    public static Api v1ListsCreate;
    @Route(method = Method.POST, path = "/v1/managers/{context_type}/{context_id}/inventories/", status = 201)
    public static Api v1ManagersInventoriesCreate;
    @Route(method = Method.POST, path = "/v1/start-tagging-task/", status = 201)
    public static Api v1StartTaggingTaskCreate;
    @Route(method = Method.POST, path = "/v1/tags-by-keys/", status = 201)
    public static Api v1TagsByKeysCreate;
    @Route(method = Method.POST, path = "/v1/{context_type}/{context_id}/inventories/", status = 200) //201
    public static Api v1InventoriesCreate;
    @Route(method = Method.GET, path = "/v1/{context_type}/{context_id}/inventory-tags/", status = 200)
    public static Api v1InventoryTagsList;
    @Route(method = Method.POST, path = "/v1/{context_type}/{context_id}/inventory-tags/", status = 201)
    public static Api v1InventoryTagsCreate;
    @Route(method = Method.GET, path = "/v1/{context_type}/{context_id}/inventory-tags/{id}/", status = 200)
    public static Api v1InventoryTagsRead;
    @Route(method = Method.GET, path = "/v1/{context_type}/{context_id}/tags/", status = 200)
    public static Api v1TagsList;
    @Route(method = Method.POST, path = "/v1/{context_type}/{context_id}/tags/", status = 201)
    public static Api v1TagsCreate;
    @Route(method = Method.GET, path = "/v1/{context_type}/{context_id}/tags/{id}/", status = 200)
    public static Api v1TagsRead;
    @Route(method = Method.PUT, path = "/v1/{context_type}/{context_id}/tags/{id}/", status = 200)
    public static Api v1TagsUpdate;
    @Route(method = Method.PATCH, path = "/v1/{context_type}/{context_id}/tags/{id}/", status = 200)
    public static Api v1TagsPartialUpdate;
    @Route(method = Method.DELETE, path = "/v1/{context_type}/{context_id}/tags/{id}/", status = 204)
    public static Api v1TagsDelete;
    @Route(method = Method.GET, path = "/v1/{context_type}/{context_id}/tags/{id}/inventories/", status = 200)
    public static Api v1TagsInventories;
    @Route(method = Method.PUT, path = "/v1/{context_type}/{context_id}/tags/{id}/inventory-tags/", status = 200)
    public static Api v1TagsInventoryTagsUpdate;
    @Route(method = Method.DELETE, path = "/v1/{context_type}/{context_id}/tags/{id}/inventory-tags/", status = 204)
    public static Api v1TagsInventoryTagsDelete;
    @Route(method = Method.GET, path = "/v1/{context_type}/{context_id}/tags/{id}/unique-values/", status = 200)
    public static Api v1TagsUniqueValues;

    @Route(method = Method.PUT, path = "/v2/admin/inventories/batch/", status = 200)
    public static Api v2AdminInventoriesBatch;
    @Route(method = Method.GET, path = "/v2/lists/", status = 200)
    public static Api v2ListsList;
    @Route(method = Method.POST, path = "/v2/lists/", status = 201)
    public static Api v2ListsCreate;
    @Route(method = Method.PUT, path = "/v2/lists/import/", status = 201)
    public static Api v2ListsImportList;
    @Route(method = Method.GET, path = "/v2/lists/{name}/", status = 200)
    public static Api v2ListsRead;
    @Route(method = Method.PUT, path = "/v2/lists/{name}/", status = 200)
    public static Api v2ListsUpdate;
    @Route(method = Method.PATCH, path = "/v2/lists/{name}/", status = 200)
    public static Api v2ListsPartialUpdate;
    @Route(method = Method.DELETE, path = "/v2/lists/{name}/", status = 204)
    public static Api v2ListsDelete;
    @Route(method = Method.POST, path = "/v2/lists/{name}/elements/", status = 201)
    public static Api v2ListsElementCreate;
    @Route(method = Method.PUT, path = "/v2/lists/{name}/elements/{key}/", status = 200)
    public static Api v2ListsElementsUpdate;
    @Route(method = Method.PATCH, path = "/v2/lists/{name}/elements/{key}/", status = 200)
    public static Api v2ListsElementsPartialUpdate;
    @Route(method = Method.DELETE, path = "/v2/lists/{name}/elements/{key}/", status = 204)
    public static Api v2ListsElementsDelete;
    @Route(method = Method.GET, path = "/v2/lists/{name}/export/", status = 200)
    public static Api v2ListsExportList;
    @Route(method = Method.GET, path = "/v2/{context_type}/{context_id}/inventories/", status = 200)
    public static Api v2InventoriesList;
    @Route(method = Method.POST, path = "/v2/{context_type}/{context_id}/inventories/", status = 201)
    public static Api v2InventoriesCreate;
    @Route(method = Method.DELETE, path = "/v2/{context_type}/{context_id}/inventories/batch/", status = 204)
    public static Api v2InventoriesBatch;
    @Route(method = Method.POST, path = "/v2/{context_type}/{context_id}/inventories/filter/", status = 200) //201
    public static Api v2InventoriesFilterCreate;
    @Route(method = Method.GET, path = "/v2/{context_type}/{context_id}/inventories/{id}/", status = 200)
    public static Api v2InventoriesRead;
    @Route(method = Method.PUT, path = "/v2/{context_type}/{context_id}/inventories/{id}/", status = 200)
    public static Api v2InventoriesUpdate;
    @Route(method = Method.PATCH, path = "/v2/{context_type}/{context_id}/inventories/{id}/", status = 200)
    public static Api v2InventoriesPartialUpdate;
    @Route(method = Method.DELETE, path = "/v2/{context_type}/{context_id}/inventories/{id}/", status = 204)
    public static Api v2InventoriesDelete;
    @Route(method = Method.GET, path = "/v2/{context_type}/{context_id}/inventories/{id}/inventory-tags/", status = 200)
    public static Api v2InventoriesInventoryTagsRead;
    @Route(method = Method.PUT, path = "/v2/{context_type}/{context_id}/inventories/{id}/inventory-tags/", status = 200)
    public static Api v2InventoriesInventoryTagsUpdate;
}
