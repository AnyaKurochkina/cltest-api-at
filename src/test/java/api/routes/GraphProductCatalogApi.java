package api.routes;

import core.helper.http.Path;

public class GraphProductCatalogApi extends ProductCatalogApi {
    //Graphs List
    @Route(method = Method.GET, path = "/api/v1/graphs/", status = 200)
    public static Path apiV1GraphsList;

    //Graphs Create
    @Route(method = Method.POST, path = "/api/v1/graphs/", status = 201)
    public static Path apiV1GraphsCreate;

    //Graphs Add tag list
    @Route(method = Method.POST, path = "/api/v1/graphs/add_tag_list/", status = 201)
    public static Path apiV1GraphsAddTagList;

    //Graphs Audit by object keys
    @Route(method = Method.POST, path = "/api/v1/graphs/audit_by_object_keys/", status = 200)
    public static Path apiV1GraphsAuditByObjectKeys;

    //Graphs Audit details
    @Route(method = Method.GET, path = "/api/v1/graphs/audit_details/", status = 200)
    public static Path apiV1GraphsAuditDetails;

    //Graphs Audit object keys
    @Route(method = Method.GET, path = "/api/v1/graphs/audit_object_keys/", status = 200)
    public static Path apiV1GraphsAuditObjectKeys;

    //Graphs Exists
    @Route(method = Method.GET, path = "/api/v1/graphs/exists/", status = 200)
    public static Path apiV1GraphsExists;

    //Graphs Load from bitbucket
    @Route(method = Method.POST, path = "/api/v1/graphs/load_from_bitbucket/", status = 201)
    public static Path apiV1GraphsLoadFromBitbucket;

    //Graphs Obj import
    @Route(method = Method.POST, path = "/api/v1/graphs/obj_import/", status = 201)
    public static Path apiV1GraphsObjImport;

    //Graphs Objects export
    @Route(method = Method.POST, path = "/api/v1/graphs/objects_export/", status = 201)
    public static Path apiV1GraphsObjectsExport;

    //Graphs Remove tag list
    @Route(method = Method.POST, path = "/api/v1/graphs/remove_tag_list/", status = 201)
    public static Path apiV1GraphsRemoveTagList;

    //Graphs Read
    @Route(method = Method.GET, path = "/api/v1/graphs/{id}/", status = 200)
    public static Path apiV1GraphsRead;

    //Graphs Partial update
    @Route(method = Method.PATCH, path = "/api/v1/graphs/{id}/", status = 200)
    public static Path apiV1GraphsPartialUpdate;

    //Graphs Delete
    @Route(method = Method.DELETE, path = "/api/v1/graphs/{id}/", status = 204)
    public static Path apiV1GraphsDelete;

    //Graphs Audit
    @Route(method = Method.GET, path = "/api/v1/graphs/{id}/audit/", status = 200)
    public static Path apiV1GraphsAudit;

    //Graphs Copy
    @Route(method = Method.POST, path = "/api/v1/graphs/{id}/copy/", status = 201)
    public static Path apiV1GraphsCopy;

    //Graphs Dump to bitbucket
    @Route(method = Method.POST, path = "/api/v1/graphs/{id}/dump_to_bitbucket/", status = 201)
    public static Path apiV1GraphsDumpToBitbucket;

    //Graphs Obj export
    @Api.Route(method = Method.GET, path = "/api/v1/graphs/{id}/obj_export/", status = 200)
    public static Path apiV1GraphsObjExport;

    //Graphs Tag list
    @Route(method = Method.GET, path = "/api/v1/graphs/{id}/tag_list/", status = 200)
    public static Path apiV1GraphsTagList;

    //Graphs Used Read
    @Route(method = Method.GET, path = "/api/v1/graphs/{id}/used/", status = 200)
    public static Path apiV1GraphsUsedRead;

    //Graphs Version list
    @Route(method = Method.GET, path = "/api/v1/graphs/{id}/version_list/", status = 200)
    public static Path apiV1GraphsVersionList;
}
