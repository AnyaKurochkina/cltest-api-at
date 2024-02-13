package tests.routes;

import core.helper.http.Path;

public class AllowedActionProductCatalogApi extends ProductCatalogApi {
    //Allowed actions List
    @Route(method = Method.GET, path = "/api/v1/allowed_actions/", status = 200)
    public static Path apiV1AllowedActionsList;

    //Allowed actions Create
    @Route(method = Method.POST, path = "/api/v1/allowed_actions/", status = 201)
    public static Path apiV1AllowedActionsCreate;

    //Allowed actions Audit by object keys
    @Route(method = Method.POST, path = "/api/v1/allowed_actions/audit_by_object_keys/", status = 200)
    public static Path apiV1AllowedActionsAuditByObjectKeys;

    //Allowed actions Audit details
    @Route(method = Method.GET, path = "/api/v1/allowed_actions/audit_details/", status = 200)
    public static Path apiV1AllowedActionsAuditDetails;

    //Allowed actions Audit object keys
    @Route(method = Method.GET, path = "/api/v1/allowed_actions/audit_object_keys/", status = 200)
    public static Path apiV1AllowedActionsAuditObjectKeys;

    //Allowed actions Check action
    @Route(method = Method.GET, path = "/api/v1/allowed_actions/check_action/", status = 200)
    public static Path apiV1AllowedActionsCheckAction;

    //Allowed actions Exists
    @Route(method = Method.GET, path = "/api/v1/allowed_actions/exists/", status = 200)
    public static Path apiV1AllowedActionsExists;

    //Allowed actions Load from bitbucket
    @Route(method = Method.POST, path = "/api/v1/allowed_actions/load_from_bitbucket/", status = 201)
    public static Path apiV1AllowedActionsLoadFromBitbucket;

    //Allowed actions Obj import
    @Route(method = Method.POST, path = "/api/v1/allowed_actions/obj_import/", status = 201)
    public static Path apiV1AllowedActionsObjImport;

    //Allowed actions Objects export
    @Route(method = Method.POST, path = "/api/v1/allowed_actions/objects_export/", status = 201)
    public static Path apiV1AllowedActionsObjectsExport;

    //Allowed actions Read
    @Route(method = Method.GET, path = "/api/v1/allowed_actions/{id}/", status = 200)
    public static Path apiV1AllowedActionsRead;

    //Allowed actions Update
    @Route(method = Method.PUT, path = "/api/v1/allowed_actions/{id}/", status = 200)
    public static Path apiV1AllowedActionsUpdate;

    //Allowed actions Partial update
    @Route(method = Method.PATCH, path = "/api/v1/allowed_actions/{id}/", status = 200)
    public static Path apiV1AllowedActionsPartialUpdate;

    //Allowed actions Delete
    @Route(method = Method.DELETE, path = "/api/v1/allowed_actions/{id}/", status = 204)
    public static Path apiV1AllowedActionsDelete;

    //Allowed actions Audit
    @Route(method = Method.GET, path = "/api/v1/allowed_actions/{id}/audit/", status = 200)
    public static Path apiV1AllowedActionsAudit;

    //Allowed actions Copy
    @Route(method = Method.POST, path = "/api/v1/allowed_actions/{id}/copy/", status = 201)
    public static Path apiV1AllowedActionsCopy;

    //Allowed actions Dump to bitbucket
    @Route(method = Method.POST, path = "/api/v1/allowed_actions/{id}/dump_to_bitbucket/", status = 201)
    public static Path apiV1AllowedActionsDumpToBitbucket;

    //Allowed actions Obj export
    @Route(method = Method.GET, path = "/api/v1/allowed_actions/{id}/obj_export/", status = 200)
    public static Path apiV1AllowedActionsObjExport;
}
