package tests.routes;

import core.helper.http.Path;

public class ServiceProductCatalogApi extends ProductCatalogApi {
    //Services List
    @Route(method = Method.GET, path = "/api/v1/services/", status = 200)
    public static Path apiV1ServicesList;

    //Services Create
    @Route(method = Method.POST, path = "/api/v1/services/", status = 201)
    public static Path apiV1ServicesCreate;

    //Services Audit by object keys
    @Route(method = Method.POST, path = "/api/v1/services/audit_by_object_keys/", status = 200)
    public static Path apiV1ServicesAuditByObjectKeys;

    //Services Audit details
    @Route(method = Method.GET, path = "/api/v1/services/audit_details/", status = 200)
    public static Path apiV1ServicesAuditDetails;

    //Services Audit object keys
    @Route(method = Method.GET, path = "/api/v1/services/audit_object_keys/", status = 200)
    public static Path apiV1ServicesAuditObjectKeys;

    //Services Exists
    @Route(method = Method.GET, path = "/api/v1/services/exists/", status = 200)
    public static Path apiV1ServicesExists;

    //Services Load from bitbucket
    @Route(method = Method.POST, path = "/api/v1/services/load_from_bitbucket/", status = 201)
    public static Path apiV1ServicesLoadFromBitbucket;

    //Services Obj import
    @Route(method = Method.POST, path = "/api/v1/services/obj_import/", status = 201)
    public static Path apiV1ServicesObjImport;

    //Services Objects export
    @Route(method = Method.POST, path = "/api/v1/services/objects_export/", status = 201)
    public static Path apiV1ServicesObjectsExport;

    //Services Read
    @Route(method = Method.GET, path = "/api/v1/services/{id}/", status = 200)
    public static Path apiV1ServicesRead;

    //Services Partial update
    @Route(method = Method.PATCH, path = "/api/v1/services/{id}/", status = 200)
    public static Path apiV1ServicesPartialUpdate;

    //Services Delete
    @Route(method = Method.DELETE, path = "/api/v1/services/{id}/", status = 204)
    public static Path apiV1ServicesDelete;

    //Services Audit
    @Route(method = Method.GET, path = "/api/v1/services/{id}/audit/", status = 200)
    public static Path apiV1ServicesAudit;

    //Services Copy
    @Route(method = Method.POST, path = "/api/v1/services/{id}/copy/", status = 201)
    public static Path apiV1ServicesCopy;

    //Services Dump to bitbucket
    @Route(method = Method.POST, path = "/api/v1/services/{id}/dump_to_bitbucket/", status = 201)
    public static Path apiV1ServicesDumpToBitbucket;

    //Services Obj export
    @Route(method = Method.GET, path = "/api/v1/services/{id}/obj_export/", status = 200)
    public static Path apiV1ServicesObjExport;

    //Services Version list
    @Route(method = Method.GET, path = "/api/v1/services/{id}/version_list/", status = 200)
    public static Path apiV1ServicesVersionList;

    //Services List
    @Route(method = Method.GET, path = "/api/v2/services/", status = 200)
    public static Path apiV2ServicesList;

    //Services Create
    @Route(method = Method.POST, path = "/api/v2/services/", status = 201)
    public static Path apiV2ServicesCreate;

    //Services Audit by object keys
    @Route(method = Method.POST, path = "/api/v2/services/audit_by_object_keys/", status = 200)
    public static Path apiV2ServicesAuditByObjectKeys;

    //Services Audit details
    @Route(method = Method.GET, path = "/api/v2/services/audit_details/", status = 200)
    public static Path apiV2ServicesAuditDetails;

    //Services Audit object keys
    @Route(method = Method.GET, path = "/api/v2/services/audit_object_keys/", status = 200)
    public static Path apiV2ServicesAuditObjectKeys;

    //Services Exists
    @Route(method = Method.GET, path = "/api/v2/services/exists/", status = 200)
    public static Path apiV2ServicesExists;

    //Services Load from bitbucket
    @Route(method = Method.POST, path = "/api/v2/services/load_from_bitbucket/", status = 201)
    public static Path apiV2ServicesLoadFromBitbucket;

    //Services Obj import
    @Route(method = Method.POST, path = "/api/v2/services/obj_import/", status = 201)
    public static Path apiV2ServicesObjImport;

    //Services Objects export
    @Route(method = Method.POST, path = "/api/v2/services/objects_export/", status = 201)
    public static Path apiV2ServicesObjectsExport;

    //Services Read
    @Route(method = Method.GET, path = "/api/v2/services/{name}/", status = 200)
    public static Path apiV2ServicesRead;

    //Services Partial update
    @Route(method = Method.PATCH, path = "/api/v2/services/{name}/", status = 200)
    public static Path apiV2ServicesPartialUpdate;

    //Services Delete
    @Route(method = Method.DELETE, path = "/api/v2/services/{name}/", status = 204)
    public static Path apiV2ServicesDelete;

    //Services Audit
    @Route(method = Method.GET, path = "/api/v2/services/{name}/audit/", status = 200)
    public static Path apiV2ServicesAudit;

    //Services Copy
    @Route(method = Method.POST, path = "/api/v2/services/{name}/copy/", status = 201)
    public static Path apiV2ServicesCopy;

    //Services Dump to bitbucket
    @Route(method = Method.POST, path = "/api/v2/services/{name}/dump_to_bitbucket/", status = 201)
    public static Path apiV2ServicesDumpToBitbucket;

    //Services Obj export
    @Route(method = Method.GET, path = "/api/v2/services/{name}/obj_export/", status = 200)
    public static Path apiV2ServicesObjExport;

    //Services Version list
    @Route(method = Method.GET, path = "/api/v2/services/{name}/version_list/", status = 200)
    public static Path apiV2ServicesVersionList;
}
