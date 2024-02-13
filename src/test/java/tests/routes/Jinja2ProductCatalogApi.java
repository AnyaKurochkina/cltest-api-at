package tests.routes;

import core.helper.http.Path;

public class Jinja2ProductCatalogApi extends ProductCatalogApi {
    //Jinja2 templates List
    @Route(method = Method.GET, path = "/api/v1/jinja2_templates/", status = 200)
    public static Path apiV1Jinja2TemplatesList;

    //Jinja2 templates Create
    @Route(method = Method.POST, path = "/api/v1/jinja2_templates/", status = 201)
    public static Path apiV1Jinja2TemplatesCreate;

    //Jinja2 templates Audit by object keys
    @Route(method = Method.POST, path = "/api/v1/jinja2_templates/audit_by_object_keys/", status = 200)
    public static Path apiV1Jinja2TemplatesAuditByObjectKeys;

    //Jinja2 templates Audit details
    @Route(method = Method.GET, path = "/api/v1/jinja2_templates/audit_details/", status = 200)
    public static Path apiV1Jinja2TemplatesAuditDetails;

    //Jinja2 templates Audit object keys
    @Route(method = Method.GET, path = "/api/v1/jinja2_templates/audit_object_keys/", status = 200)
    public static Path apiV1Jinja2TemplatesAuditObjectKeys;

    //Jinja2 templates Exists
    @Route(method = Method.GET, path = "/api/v1/jinja2_templates/exists/", status = 200)
    public static Path apiV1Jinja2TemplatesExists;

    //Jinja2 templates Load from bitbucket
    @Route(method = Method.POST, path = "/api/v1/jinja2_templates/load_from_bitbucket/", status = 201)
    public static Path apiV1Jinja2TemplatesLoadFromBitbucket;

    //Jinja2 templates Obj import
    @Route(method = Method.POST, path = "/api/v1/jinja2_templates/obj_import/", status = 201)
    public static Path apiV1Jinja2TemplatesObjImport;

    //Jinja2 templates Objects export
    @Route(method = Method.POST, path = "/api/v1/jinja2_templates/objects_export/", status = 201)
    public static Path apiV1Jinja2TemplatesObjectsExport;

    //Jinja2 templates Read
    @Route(method = Method.GET, path = "/api/v1/jinja2_templates/{id}/", status = 200)
    public static Path apiV1Jinja2TemplatesRead;

    //Jinja2 templates Update
    @Route(method = Method.PUT, path = "/api/v1/jinja2_templates/{id}/", status = 200)
    public static Path apiV1Jinja2TemplatesUpdate;

    //Jinja2 templates Partial update
    @Route(method = Method.PATCH, path = "/api/v1/jinja2_templates/{id}/", status = 200)
    public static Path apiV1Jinja2TemplatesPartialUpdate;

    //Jinja2 templates Delete
    @Route(method = Method.DELETE, path = "/api/v1/jinja2_templates/{id}/", status = 204)
    public static Path apiV1Jinja2TemplatesDelete;

    //Jinja2 templates Audit
    @Route(method = Method.GET, path = "/api/v1/jinja2_templates/{id}/audit/", status = 200)
    public static Path apiV1Jinja2TemplatesAudit;

    //Jinja2 templates Copy
    @Route(method = Method.POST, path = "/api/v1/jinja2_templates/{id}/copy/", status = 201)
    public static Path apiV1Jinja2TemplatesCopy;

    //Jinja2 templates Dump to bitbucket
    @Route(method = Method.POST, path = "/api/v1/jinja2_templates/{id}/dump_to_bitbucket/", status = 201)
    public static Path apiV1Jinja2TemplatesDumpToBitbucket;

    //Jinja2 templates Obj export
    @Route(method = Method.GET, path = "/api/v1/jinja2_templates/{id}/obj_export/", status = 200)
    public static Path apiV1Jinja2TemplatesObjExport;

    //Jinja2 templates Used
    @Route(method = Method.GET, path = "/api/v1/jinja2_templates/{id}/used/", status = 200)
    public static Path apiV1Jinja2TemplatesUsed;

    //Jinja2 templates Version list
    @Route(method = Method.GET, path = "/api/v1/jinja2_templates/{id}/version_list/", status = 200)
    public static Path apiV1Jinja2TemplatesVersionList;
}
