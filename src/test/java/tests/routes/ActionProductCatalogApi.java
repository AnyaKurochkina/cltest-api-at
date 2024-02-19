package tests.routes;

import core.helper.http.Path;

public class ActionProductCatalogApi extends ProductCatalogApi {
    //Actions List
    @Route(method = Method.GET, path = "/api/v1/actions/", status = 200)
    public static Path apiV1ActionsList;

    //Actions Create
    @Route(method = Method.POST, path = "/api/v1/actions/", status = 201)
    public static Path apiV1ActionsCreate;

    //Actions Add tag list
    @Route(method = Method.POST, path = "/api/v1/actions/add_tag_list/", status = 201)
    public static Path apiV1ActionsAddTagList;

    //Actions Audit by object keys
    @Route(method = Method.POST, path = "/api/v1/actions/audit_by_object_keys/", status = 200)
    public static Path apiV1ActionsAuditByObjectKeys;

    //Actions Audit details
    @Route(method = Method.GET, path = "/api/v1/actions/audit_details/", status = 200)
    public static Path apiV1ActionsAuditDetails;

    //Actions Audit object keys
    @Route(method = Method.GET, path = "/api/v1/actions/audit_object_keys/", status = 200)
    public static Path apiV1ActionsAuditObjectKeys;

    //Actions Exists
    @Route(method = Method.GET, path = "/api/v1/actions/exists/", status = 200)
    public static Path apiV1ActionsExists;

    //Actions Load from bitbucket
    @Route(method = Method.POST, path = "/api/v1/actions/load_from_bitbucket/", status = 201)
    public static Path apiV1ActionsLoadFromBitbucket;

    //Actions Mass change
    @Route(method = Method.POST, path = "/api/v1/actions/mass_change/", status = 200)
    public static Path apiV1ActionsMassChange;

    //Actions Obj import
    @Route(method = Method.POST, path = "/api/v1/actions/obj_import/", status = 200)
    public static Path apiV1ActionsObjImport;

    //Actions Objects export
    @Route(method = Method.POST, path = "/api/v1/actions/objects_export/", status = 200)
    public static Path apiV1ActionsObjectsExport;

    //Actions Remove tag list
    @Route(method = Method.POST, path = "/api/v1/actions/remove_tag_list/", status = 204)
    public static Path apiV1ActionsRemoveTagList;

    //Actions Read
    @Route(method = Method.GET, path = "/api/v1/actions/{id}/", status = 200)
    public static Path apiV1ActionsRead;

    //Actions Partial update
    @Route(method = Method.PATCH, path = "/api/v1/actions/{id}/", status = 200)
    public static Path apiV1ActionsPartialUpdate;

    //Actions update
    @Route(method = Method.PUT, path = "/api/v1/actions/{id}/", status = 200)
    public static Path apiV1ActionsUpdate;

    //Actions Delete
    @Route(method = Method.DELETE, path = "/api/v1/actions/{id}/", status = 204)
    public static Path apiV1ActionsDelete;

    //Actions Audit
    @Route(method = Method.GET, path = "/api/v1/actions/{id}/audit/", status = 200)
    public static Path apiV1ActionsAudit;

    //Actions Copy
    @Route(method = Method.POST, path = "/api/v1/actions/{id}/copy/", status = 201)
    public static Path apiV1ActionsCopy;

    //Actions Dump to bitbucket
    @Route(method = Method.POST, path = "/api/v1/actions/{id}/dump_to_bitbucket/", status = 201)
    public static Path apiV1ActionsDumpToBitbucket;

    //Actions Obj export
    @Route(method = Method.GET, path = "/api/v1/actions/{id}/obj_export/", status = 200)
    public static Path apiV1ActionsObjExport;

    //Actions Tag list
    @Route(method = Method.GET, path = "/api/v1/actions/{id}/tag_list/", status = 200)
    public static Path apiV1ActionsTagList;

    //Actions Version list
    @Route(method = Method.GET, path = "/api/v1/actions/{id}/version_list/", status = 200)
    public static Path apiV1ActionsVersionList;

    //Actions List
    @Route(method = Method.GET, path = "/api/v2/actions/", status = 200)
    public static Path apiV2ActionsList;

    //Actions Create
    @Route(method = Method.POST, path = "/api/v2/actions/", status = 201)
    public static Path apiV2ActionsCreate;

    //Actions Add tag list
    @Route(method = Method.POST, path = "/api/v2/actions/add_tag_list/", status = 201)
    public static Path apiV2ActionsAddTagList;

    //Actions Audit by object keys
    @Route(method = Method.POST, path = "/api/v2/actions/audit_by_object_keys/", status = 200)
    public static Path apiV2ActionsAuditByObjectKeys;

    //Actions Audit details
    @Route(method = Method.GET, path = "/api/v2/actions/audit_details/", status = 200)
    public static Path apiV2ActionsAuditDetails;

    //Actions Audit object keys
    @Route(method = Method.GET, path = "/api/v2/actions/audit_object_keys/", status = 200)
    public static Path apiV2ActionsAuditObjectKeys;

    //Actions Exists
    @Route(method = Method.GET, path = "/api/v2/actions/exists/", status = 200)
    public static Path apiV2ActionsExists;

    //Actions Load from bitbucket
    @Route(method = Method.POST, path = "/api/v2/actions/load_from_bitbucket/", status = 201)
    public static Path apiV2ActionsLoadFromBitbucket;

    //Actions Mass change
    @Route(method = Method.POST, path = "/api/v2/actions/mass_change/", status = 201)
    public static Path apiV2ActionsMassChange;

    //Actions Obj import
    @Route(method = Method.POST, path = "/api/v2/actions/obj_import/", status = 201)
    public static Path apiV2ActionsObjImport;

    //Actions Objects export
    @Route(method = Method.POST, path = "/api/v2/actions/objects_export/", status = 201)
    public static Path apiV2ActionsObjectsExport;

    //Actions Remove tag list
    @Route(method = Method.POST, path = "/api/v2/actions/remove_tag_list/", status = 201)
    public static Path apiV2ActionsRemoveTagList;

    //Actions Read
    @Route(method = Method.GET, path = "/api/v2/actions/{name}/", status = 200)
    public static Path apiV2ActionsRead;

    //Actions Partial update
    @Route(method = Method.PATCH, path = "/api/v2/actions/{name}/", status = 200)
    public static Path apiV2ActionsPartialUpdate;

    //Actions Delete
    @Route(method = Method.DELETE, path = "/api/v2/actions/{name}/", status = 204)
    public static Path apiV2ActionsDelete;

    //Actions Audit
    @Route(method = Method.GET, path = "/api/v2/actions/{name}/audit/", status = 200)
    public static Path apiV2ActionsAudit;

    //Actions Copy
    @Route(method = Method.POST, path = "/api/v2/actions/{name}/copy/", status = 201)
    public static Path apiV2ActionsCopy;

    //Actions Dump to bitbucket
    @Route(method = Method.POST, path = "/api/v2/actions/{name}/dump_to_bitbucket/", status = 201)
    public static Path apiV2ActionsDumpToBitbucket;

    //Actions Obj export
    @Route(method = Method.GET, path = "/api/v2/actions/{name}/obj_export/", status = 200)
    public static Path apiV2ActionsObjExport;

    //Actions Tag list
    @Route(method = Method.GET, path = "/api/v2/actions/{name}/tag_list/", status = 200)
    public static Path apiV2ActionsTagList;

    //Actions Version list
    @Route(method = Method.GET, path = "/api/v2/actions/{name}/version_list/", status = 200)
    public static Path apiV2ActionsVersionList;

}
