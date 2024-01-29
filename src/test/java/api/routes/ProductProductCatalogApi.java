package api.routes;

import core.helper.http.Path;

public class ProductProductCatalogApi extends ProductCatalogApi {
    //Products List
    @Route(method = Method.GET, path = "/api/v1/products/", status = 200)
    public static Path apiV1ProductsList;

    //Products Create
    @Route(method = Method.POST, path = "/api/v1/products/", status = 201)
    public static Path apiV1ProductsCreate;

    //Products Add tag list
    @Route(method = Method.POST, path = "/api/v1/products/add_tag_list/", status = 201)
    public static Path apiV1ProductsAddTagList;

    //Products Audit by object keys
    @Route(method = Method.POST, path = "/api/v1/products/audit_by_object_keys/", status = 200)
    public static Path apiV1ProductsAuditByObjectKeys;

    //Products Audit details
    @Route(method = Method.GET, path = "/api/v1/products/audit_details/", status = 200)
    public static Path apiV1ProductsAuditDetails;

    //Products Audit object keys
    @Route(method = Method.GET, path = "/api/v1/products/audit_object_keys/", status = 200)
    public static Path apiV1ProductsAuditObjectKeys;

    //Products Categories
    @Route(method = Method.GET, path = "/api/v1/products/categories/", status = 200)
    public static Path apiV1ProductsCategories;

    //Products Exists
    @Route(method = Method.GET, path = "/api/v1/products/exists/", status = 200)
    public static Path apiV1ProductsExists;

    //Products Load from bitbucket
    @Route(method = Method.POST, path = "/api/v1/products/load_from_bitbucket/", status = 201)
    public static Path apiV1ProductsLoadFromBitbucket;

    //Products Mass change
    @Route(method = Method.POST, path = "/api/v1/products/mass_change/", status = 201)
    public static Path apiV1ProductsMassChange;

    //Products Obj import
    @Route(method = Method.POST, path = "/api/v1/products/obj_import/", status = 201)
    public static Path apiV1ProductsObjImport;

    //Products Objects export
    @Route(method = Method.POST, path = "/api/v1/products/objects_export/", status = 201)
    public static Path apiV1ProductsObjectsExport;

    //Products Remove tag list
    @Route(method = Method.POST, path = "/api/v1/products/remove_tag_list/", status = 201)
    public static Path apiV1ProductsRemoveTagList;

    //Products Read
    @Route(method = Method.GET, path = "/api/v1/products/{id}/", status = 200)
    public static Path apiV1ProductsRead;

    //Products Update
    @Route(method = Method.PUT, path = "/api/v1/products/{id}/", status = 200)
    public static Path apiV1ProductsUpdate;

    //Products Partial update
    @Route(method = Method.PATCH, path = "/api/v1/products/{id}/", status = 200)
    public static Path apiV1ProductsPartialUpdate;

    //Products Delete
    @Route(method = Method.DELETE, path = "/api/v1/products/{id}/", status = 204)
    public static Path apiV1ProductsDelete;

    //Products Audit
    @Route(method = Method.GET, path = "/api/v1/products/{id}/audit/", status = 200)
    public static Path apiV1ProductsAudit;

    //Products Copy
    @Route(method = Method.POST, path = "/api/v1/products/{id}/copy/", status = 201)
    public static Path apiV1ProductsCopy;

    //Products Dump to bitbucket
    @Route(method = Method.POST, path = "/api/v1/products/{id}/dump_to_bitbucket/", status = 201)
    public static Path apiV1ProductsDumpToBitbucket;

    //Products Info
    @Route(method = Method.GET, path = "/api/v1/products/{id}/info/", status = 200)
    public static Path apiV1ProductsInfo;

    //Products Obj export
    @Route(method = Method.GET, path = "/api/v1/products/{id}/obj_export/", status = 200)
    public static Path apiV1ProductsObjExport;

    //Products Order restrictions Read
    @Route(method = Method.GET, path = "/api/v1/products/{id}/order_restrictions/", status = 200)
    public static Path apiV1ProductsOrderRestrictionsRead;

    //Products Order restrictions Create
    @Route(method = Method.POST, path = "/api/v1/products/{id}/order_restrictions/", status = 201)
    public static Path apiV1ProductsOrderRestrictionsCreate;

    //Products Order restrictions Partial update
    @Route(method = Method.PATCH, path = "/api/v1/products/{id}/order_restrictions/", status = 200)
    public static Path apiV1ProductsOrderRestrictionsPartialUpdate;

    //Products Order restrictions Delete
    @Route(method = Method.DELETE, path = "/api/v1/products/{id}/order_restrictions/", status = 204)
    public static Path apiV1ProductsOrderRestrictionsDelete;

    //Products Tag list
    @Route(method = Method.GET, path = "/api/v1/products/{id}/tag_list/", status = 200)
    public static Path apiV1ProductsTagList;

    //Products Version list
    @Route(method = Method.GET, path = "/api/v1/products/{id}/version_list/", status = 200)
    public static Path apiV1ProductsVersionList;

    //Products List
    @Route(method = Method.GET, path = "/api/v2/{context_type}/{context_id}/products/", status = 200)
    public static Path apiV2ProductsList;

    //Products Create
    @Route(method = Method.POST, path = "/api/v2/{context_type}/{context_id}/products/", status = 201)
    public static Path apiV2ProductsCreate;

    //Products Add tag list
    @Route(method = Method.POST, path = "/api/v2/{context_type}/{context_id}/products/add_tag_list/", status = 201)
    public static Path apiV2ProductsAddTagList;

    //Products Audit by object keys
    @Route(method = Method.POST, path = "/api/v2/{context_type}/{context_id}/products/audit_by_object_keys/", status = 200)
    public static Path apiV2ProductsAuditByObjectKeys;

    //Products Audit details
    @Route(method = Method.GET, path = "/api/v2/{context_type}/{context_id}/products/audit_details/", status = 200)
    public static Path apiV2ProductsAuditDetails;

    //Products Audit object keys
    @Route(method = Method.GET, path = "/api/v2/{context_type}/{context_id}/products/audit_object_keys/", status = 200)
    public static Path apiV2ProductsAuditObjectKeys;

    //Products Categories
    @Route(method = Method.GET, path = "/api/v2/{context_type}/{context_id}/products/categories/", status = 200)
    public static Path apiV2ProductsCategories;

    //Products Exists
    @Route(method = Method.GET, path = "/api/v2/{context_type}/{context_id}/products/exists/", status = 200)
    public static Path apiV2ProductsExists;

    //Products Load from bitbucket
    @Route(method = Method.POST, path = "/api/v2/{context_type}/{context_id}/products/load_from_bitbucket/", status = 201)
    public static Path apiV2ProductsLoadFromBitbucket;

    //Products Mass change
    @Route(method = Method.POST, path = "/api/v2/{context_type}/{context_id}/products/mass_change/", status = 201)
    public static Path apiV2ProductsMassChange;

    //Products Obj import
    @Route(method = Method.POST, path = "/api/v2/{context_type}/{context_id}/products/obj_import/", status = 201)
    public static Path apiV2ProductsObjImport;

    //Products Objects export
    @Route(method = Method.POST, path = "/api/v2/{context_type}/{context_id}/products/objects_export/", status = 201)
    public static Path apiV2ProductsObjectsExport;

    //Products Remove tag list
    @Route(method = Method.POST, path = "/api/v2/{context_type}/{context_id}/products/remove_tag_list/", status = 201)
    public static Path apiV2ProductsRemoveTagList;

    //Products Read
    @Route(method = Method.GET, path = "/api/v2/{context_type}/{context_id}/products/{name}/", status = 200)
    public static Path apiV2ProductsRead;

    //Products Update
    @Route(method = Method.PUT, path = "/api/v2/{context_type}/{context_id}/products/{name}/", status = 200)
    public static Path apiV2ProductsUpdate;

    //Products Partial update
    @Route(method = Method.PATCH, path = "/api/v2/{context_type}/{context_id}/products/{name}/", status = 200)
    public static Path apiV2ProductsPartialUpdate;

    //Products Delete
    @Route(method = Method.DELETE, path = "/api/v2/{context_type}/{context_id}/products/{name}/", status = 204)
    public static Path apiV2ProductsDelete;

    //Products Audit
    @Route(method = Method.GET, path = "/api/v2/{context_type}/{context_id}/products/{name}/audit/", status = 200)
    public static Path apiV2ProductsAudit;

    //Products Copy
    @Route(method = Method.POST, path = "/api/v2/{context_type}/{context_id}/products/{name}/copy/", status = 201)
    public static Path apiV2ProductsCopy;

    //Products Dump to bitbucket
    @Route(method = Method.POST, path = "/api/v2/{context_type}/{context_id}/products/{name}/dump_to_bitbucket/", status = 201)
    public static Path apiV2ProductsDumpToBitbucket;

    //Products Info
    @Route(method = Method.GET, path = "/api/v2/{context_type}/{context_id}/products/{name}/info/", status = 200)
    public static Path apiV2ProductsInfo;

    //Products Obj export
    @Route(method = Method.GET, path = "/api/v2/{context_type}/{context_id}/products/{name}/obj_export/", status = 200)
    public static Path apiV2ProductsObjExport;

    //Products Order restrictions Read
    @Route(method = Method.GET, path = "/api/v2/{context_type}/{context_id}/products/{name}/order_restrictions/", status = 200)
    public static Path apiV2ProductsOrderRestrictionsRead;

    //Products Order restrictions Create
    @Route(method = Method.POST, path = "/api/v2/{context_type}/{context_id}/products/{name}/order_restrictions/", status = 201)
    public static Path apiV2ProductsOrderRestrictionsCreate;

    //Products Order restrictions Partial update
    @Route(method = Method.PATCH, path = "/api/v2/{context_type}/{context_id}/products/{name}/order_restrictions/", status = 200)
    public static Path apiV2ProductsOrderRestrictionsPartialUpdate;

    //Products Order restrictions Delete
    @Route(method = Method.DELETE, path = "/api/v2/{context_type}/{context_id}/products/{name}/order_restrictions/", status = 204)
    public static Path apiV2ProductsOrderRestrictionsDelete;

    //Products Tag list
    @Route(method = Method.GET, path = "/api/v2/{context_type}/{context_id}/products/{name}/tag_list/", status = 200)
    public static Path apiV2ProductsTagList;

    //Products Version list
    @Route(method = Method.GET, path = "/api/v2/{context_type}/{context_id}/products/{name}/version_list/", status = 200)
    public static Path apiV2ProductsVersionList;
}
