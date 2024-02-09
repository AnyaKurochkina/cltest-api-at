package tests.routes;

import core.helper.http.Path;

public class ItemVisualTemplateProductCatalogApi extends ProductCatalogApi {
    //Item visual templates List
    @Route(method = Method.GET, path = "/api/v1/item_visual_templates/", status = 200)
    public static Path apiV1ItemVisualTemplatesList;

    //Item visual templates Create
    @Route(method = Method.POST, path = "/api/v1/item_visual_templates/", status = 201)
    public static Path apiV1ItemVisualTemplatesCreate;

    //Item visual templates Add tag list
    @Route(method = Method.POST, path = "/api/v1/item_visual_templates/add_tag_list/", status = 201)
    public static Path apiV1ItemVisualTemplatesAddTagList;

    //Item visual templates Audit by object keys
    @Route(method = Method.POST, path = "/api/v1/item_visual_templates/audit_by_object_keys/", status = 200)
    public static Path apiV1ItemVisualTemplatesAuditByObjectKeys;

    //Item visual templates Audit details
    @Route(method = Method.GET, path = "/api/v1/item_visual_templates/audit_details/", status = 200)
    public static Path apiV1ItemVisualTemplatesAuditDetails;

    //Item visual templates Audit object keys
    @Route(method = Method.GET, path = "/api/v1/item_visual_templates/audit_object_keys/", status = 200)
    public static Path apiV1ItemVisualTemplatesAuditObjectKeys;

    //Item visual templates Exists
    @Route(method = Method.GET, path = "/api/v1/item_visual_templates/exists/", status = 200)
    public static Path apiV1ItemVisualTemplatesExists;

    //Item visual templates Item visual template
    @Route(method = Method.GET, path = "/api/v1/item_visual_templates/item_visual_template/{event_type}/{event_provider}/", status = 200)
    public static Path apiV1ItemVisualTemplatesItemVisualTemplate;

    //Item visual templates Load from bitbucket
    @Route(method = Method.POST, path = "/api/v1/item_visual_templates/load_from_bitbucket/", status = 201)
    public static Path apiV1ItemVisualTemplatesLoadFromBitbucket;

    //Item visual templates Obj import
    @Route(method = Method.POST, path = "/api/v1/item_visual_templates/obj_import/", status = 201)
    public static Path apiV1ItemVisualTemplatesObjImport;

    //Item visual templates Objects export
    @Route(method = Method.POST, path = "/api/v1/item_visual_templates/objects_export/", status = 201)
    public static Path apiV1ItemVisualTemplatesObjectsExport;

    //Item visual templates Remove tag list
    @Route(method = Method.POST, path = "/api/v1/item_visual_templates/remove_tag_list/", status = 201)
    public static Path apiV1ItemVisualTemplatesRemoveTagList;

    //Item visual templates Read
    @Route(method = Method.GET, path = "/api/v1/item_visual_templates/{id}/", status = 200)
    public static Path apiV1ItemVisualTemplatesRead;

    //Item visual templates Update
    @Route(method = Method.PUT, path = "/api/v1/item_visual_templates/{id}/", status = 200)
    public static Path apiV1ItemVisualTemplatesUpdate;

    //Item visual templates Partial update
    @Route(method = Method.PATCH, path = "/api/v1/item_visual_templates/{id}/", status = 200)
    public static Path apiV1ItemVisualTemplatesPartialUpdate;

    //Item visual templates Delete
    @Route(method = Method.DELETE, path = "/api/v1/item_visual_templates/{id}/", status = 204)
    public static Path apiV1ItemVisualTemplatesDelete;

    //Item visual templates Audit
    @Route(method = Method.GET, path = "/api/v1/item_visual_templates/{id}/audit/", status = 200)
    public static Path apiV1ItemVisualTemplatesAudit;

    //Item visual templates Copy
    @Route(method = Method.POST, path = "/api/v1/item_visual_templates/{id}/copy/", status = 201)
    public static Path apiV1ItemVisualTemplatesCopy;

    //Item visual templates Dump to bitbucket
    @Route(method = Method.POST, path = "/api/v1/item_visual_templates/{id}/dump_to_bitbucket/", status = 201)
    public static Path apiV1ItemVisualTemplatesDumpToBitbucket;

    //Item visual templates Obj export
    @Route(method = Method.GET, path = "/api/v1/item_visual_templates/{id}/obj_export/", status = 200)
    public static Path apiV1ItemVisualTemplatesObjExport;

    //Item visual templates Tag list
    @Route(method = Method.GET, path = "/api/v1/item_visual_templates/{id}/tag_list/", status = 200)
    public static Path apiV1ItemVisualTemplatesTagList;
}
