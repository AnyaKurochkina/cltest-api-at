package tests.routes;

import core.helper.http.Path;

public class TemplateProductCatalogApi extends ProductCatalogApi {
    //Templates List
    @Route(method = Method.GET, path = "/api/v1/templates/", status = 200)
    public static Path apiV1TemplatesList;

    //Templates Create
    @Route(method = Method.POST, path = "/api/v1/templates/", status = 201)
    public static Path apiV1TemplatesCreate;

    //Templates Add tag list
    @Route(method = Method.POST, path = "/api/v1/templates/add_tag_list/", status = 201)
    public static Path apiV1TemplatesAddTagList;

    //Templates Audit by object keys
    @Route(method = Method.POST, path = "/api/v1/templates/audit_by_object_keys/", status = 200)
    public static Path apiV1TemplatesAuditByObjectKeys;

    //Templates Audit details
    @Route(method = Method.GET, path = "/api/v1/templates/audit_details/", status = 200)
    public static Path apiV1TemplatesAuditDetails;

    //Templates Audit object keys
    @Route(method = Method.GET, path = "/api/v1/templates/audit_object_keys/", status = 200)
    public static Path apiV1TemplatesAuditObjectKeys;

    //Templates Exists
    @Route(method = Method.GET, path = "/api/v1/templates/exists/", status = 200)
    public static Path apiV1TemplatesExists;

    //Templates Load from bitbucket
    @Route(method = Method.POST, path = "/api/v1/templates/load_from_bitbucket/", status = 201)
    public static Path apiV1TemplatesLoadFromBitbucket;

    //Templates Obj import
    @Route(method = Method.POST, path = "/api/v1/templates/obj_import/", status = 201)
    public static Path apiV1TemplatesObjImport;

    //Templates Objects export
    @Route(method = Method.POST, path = "/api/v1/templates/objects_export/", status = 201)
    public static Path apiV1TemplatesObjectsExport;

    //Templates Remove tag list
    @Route(method = Method.POST, path = "/api/v1/templates/remove_tag_list/", status = 201)
    public static Path apiV1TemplatesRemoveTagList;

    //Templates Read
    @Route(method = Method.GET, path = "/api/v1/templates/{id}/", status = 200)
    public static Path apiV1TemplatesRead;

    //Templates Partial update
    @Route(method = Method.PATCH, path = "/api/v1/templates/{id}/", status = 200)
    public static Path apiV1TemplatesPartialUpdate;

    //Templates Delete
    @Route(method = Method.DELETE, path = "/api/v1/templates/{id}/", status = 204)
    public static Path apiV1TemplatesDelete;

    //Templates Audit
    @Route(method = Method.GET, path = "/api/v1/templates/{id}/audit/", status = 200)
    public static Path apiV1TemplatesAudit;

    //Templates Copy
    @Route(method = Method.POST, path = "/api/v1/templates/{id}/copy/", status = 201)
    public static Path apiV1TemplatesCopy;

    //Templates Dump to bitbucket
    @Route(method = Method.POST, path = "/api/v1/templates/{id}/dump_to_bitbucket/", status = 201)
    public static Path apiV1TemplatesDumpToBitbucket;

    //Templates Obj export
    @Route(method = Method.GET, path = "/api/v1/templates/{id}/obj_export/", status = 200)
    public static Path apiV1TemplatesObjExport;

    //Templates Tag list
    @Route(method = Method.GET, path = "/api/v1/templates/{id}/tag_list/", status = 200)
    public static Path apiV1TemplatesTagList;

    //Templates Used
    @Route(method = Method.GET, path = "/api/v1/templates/{id}/used/", status = 200)
    public static Path apiV1TemplatesUsed;

    //Templates Version list
    @Route(method = Method.GET, path = "/api/v1/templates/{id}/version_list/", status = 200)
    public static Path apiV1TemplatesVersionList;
}
