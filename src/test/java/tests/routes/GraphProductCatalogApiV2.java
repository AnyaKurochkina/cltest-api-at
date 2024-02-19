package tests.routes;

import core.helper.http.Path;

public class GraphProductCatalogApiV2 extends ProductCatalogApi {

    @Api.Route(method = Api.Method.GET, path = "/api/v2/graphs/{name}/input_vars/", status = 200)
    public static Path apiV2GraphsInputVars;

    //Graphs List
    @Route(method = Method.GET, path = "/api/v2/graphs/", status = 200)
    public static Path apiV2GraphsList;

    //Graphs Create
    @Route(method = Method.POST, path = "/api/v2/graphs/", status = 201)
    public static Path apiV2GraphsCreate;

    //Graphs Add tag list
    @Route(method = Method.POST, path = "/api/v2/graphs/add_tag_list/", status = 201)
    public static Path apiV2GraphsAddTagList;

    //Graphs Audit by object keys
    @Route(method = Method.POST, path = "/api/v2/graphs/audit_by_object_keys/", status = 200)
    public static Path apiV2GraphsAuditByObjectKeys;

    //Graphs Audit details
    @Route(method = Method.GET, path = "/api/v2/graphs/audit_details/", status = 200)
    public static Path apiV2GraphsAuditDetails;

    //Graphs Audit object keys
    @Route(method = Method.GET, path = "/api/v2/graphs/audit_object_keys/", status = 200)
    public static Path apiV2GraphsAuditObjectKeys;

    //Graphs Exists
    @Route(method = Method.GET, path = "/api/v2/graphs/exists/", status = 200)
    public static Path apiV2GraphsExists;

    //Graphs Load from bitbucket
    @Route(method = Method.POST, path = "/api/v2/graphs/load_from_bitbucket/", status = 201)
    public static Path apiV2GraphsLoadFromBitbucket;

    //Graphs Obj import
    @Route(method = Method.POST, path = "/api/v2/graphs/obj_import/", status = 201)
    public static Path apiV2GraphsObjImport;

    //Graphs Objects export
    @Route(method = Method.POST, path = "/api/v2/graphs/objects_export/", status = 201)
    public static Path apiV2GraphsObjectsExport;

    //Graphs Remove tag list
    @Route(method = Method.POST, path = "/api/v2/graphs/remove_tag_list/", status = 201)
    public static Path apiV2GraphsRemoveTagList;

    //Graphs Read
    @Route(method = Method.GET, path = "/api/v2/graphs/{name}/", status = 200)
    public static Path apiV2GraphsRead;

    //Graphs Partial update
    @Route(method = Method.PATCH, path = "/api/v2/graphs/{name}/", status = 200)
    public static Path apiV2GraphsPartialUpdate;

    //Graphs Delete
    @Route(method = Method.DELETE, path = "/api/v2/graphs/{name}/", status = 204)
    public static Path apiV2GraphsDelete;

    //Graphs Audit
    @Route(method = Method.GET, path = "/api/v2/graphs/{name}/audit/", status = 200)
    public static Path apiV2GraphsAudit;

    //Graphs Copy
    @Route(method = Method.POST, path = "/api/v2/graphs/{name}/copy/", status = 201)
    public static Path apiV2GraphsCopy;

    //Graphs Dump to bitbucket
    @Route(method = Method.POST, path = "/api/v2/graphs/{name}/dump_to_bitbucket/", status = 201)
    public static Path apiV2GraphsDumpToBitbucket;

    //Graphs Obj export
    @Route(method = Method.GET, path = "/api/v2/graphs/{name}/obj_export/", status = 200)
    public static Path apiV2GraphsObjExport;

    //Graphs Tag list
    @Route(method = Method.GET, path = "/api/v2/graphs/{name}/tag_list/", status = 200)
    public static Path apiV2GraphsTagList;

    //Graphs Used
    @Route(method = Method.GET, path = "/api/v2/graphs/{name}/used/", status = 200)
    public static Path apiV2GraphsUsed;

    //Graphs Version list
    @Route(method = Method.GET, path = "/api/v2/graphs/{name}/version_list/", status = 200)
    public static Path apiV2GraphsVersionList;
}
