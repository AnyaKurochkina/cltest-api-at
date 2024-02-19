package tests.routes;

import core.helper.http.Path;

import static core.helper.Configure.KONG_URL;

public class ReferencesApi implements Api {
    @Route(method = Method.GET, path = "/api/healthcheck/", status = 200)
    public static Path apiHealthcheckList;

    @Route(method = Method.GET, path = "/api/v1/admin_package_import/", status = 200)
    public static Path apiV1AdminPackageImportList;

    @Route(method = Method.POST, path = "/api/v1/admin_package_import/", status = 201)
    public static Path apiV1AdminPackageImportCreate;

    @Route(method = Method.GET, path = "/api/v1/directories/", status = 200)
    public static Path apiV1DirectoriesList;

    @Route(method = Method.GET, path = "/api/v1/directories/audit_details/", status = 200)
    public static Path apiV1DirectoriesAuditDetails;

    @Route(method = Method.GET, path = "/api/v1/directories/audit_object_keys/", status = 200)
    public static Path apiV1DirectoriesAuditObjectKeys;

    @Route(method = Method.GET, path = "/api/v1/directories/{name}/", status = 200)
    public static Path apiV1DirectoriesRead;

    @Route(method = Method.GET, path = "/api/v1/directories/{name}/audit/", status = 200)
    public static Path apiV1DirectoriesAudit;

    @Route(method = Method.GET, path = "/api/v1/health/", status = 200)
    public static Path apiV1HealthList;

    @Route(method = Method.POST, path = "/api/v1/package_import/", status = 201)
    public static Path apiV1PackageImportCreate;

    @Route(method = Method.GET, path = "/api/v1/page_filters/", status = 200)
    public static Path apiV1PageFiltersList;

    @Route(method = Method.GET, path = "/api/v1/page_filters/audit_details/", status = 200)
    public static Path apiV1PageFiltersAuditDetails;

    @Route(method = Method.GET, path = "/api/v1/page_filters/audit_object_keys/", status = 200)
    public static Path apiV1PageFiltersAuditObjectKeys;

    @Route(method = Method.GET, path = "/api/v1/page_filters/{key}/", status = 200)
    public static Path apiV1PageFiltersRead;

    @Route(method = Method.GET, path = "/api/v1/page_filters/{key}/audit/", status = 200)
    public static Path apiV1PageFiltersAudit;

    @Route(method = Method.GET, path = "/api/v1/pages/", status = 200)
    public static Path apiV1PagesList;

    @Route(method = Method.GET, path = "/api/v1/pages/audit_details/", status = 200)
    public static Path apiV1PagesAuditDetails;

    @Route(method = Method.GET, path = "/api/v1/pages/audit_object_keys/", status = 200)
    public static Path apiV1PagesAuditObjectKeys;

    @Route(method = Method.GET, path = "/api/v1/pages/{id}/", status = 200)
    public static Path apiV1PagesRead;

    @Route(method = Method.GET, path = "/api/v1/pages/{id}/audit/", status = 200)
    public static Path apiV1PagesAudit;

    @Route(method = Method.GET, path = "/api/v1/private/directories/", status = 200)
    public static Path apiV1PrivateDirectoriesList;

    @Route(method = Method.POST, path = "/api/v1/private/directories/", status = 201)
    public static Path apiV1PrivateDirectoriesCreate;

    @Route(method = Method.POST, path = "/api/v1/private/directories/audit_by_object_keys/", status = 200)
    public static Path apiV1PrivateDirectoriesAuditByObjectKeys;

    @Route(method = Method.GET, path = "/api/v1/private/directories/audit_details/", status = 200)
    public static Path apiV1PrivateDirectoriesAuditDetails;

    @Route(method = Method.GET, path = "/api/v1/private/directories/audit_object_keys/", status = 200)
    public static Path apiV1PrivateDirectoriesAuditObjectKeys;

    @Route(method = Method.POST, path = "/api/v1/private/directories/obj_import/", status = 201)
    public static Path apiV1PrivateDirectoriesObjImport;

    @Route(method = Method.POST, path = "/api/v1/private/directories/objects_export/", status = 201)
    public static Path apiV1PrivateDirectoriesObjectsExport;

    @Route(method = Method.GET, path = "/api/v1/private/directories/{directory_name}/pages/", status = 200)
    public static Path apiV1PrivateDirectoriesPagesList;

    @Route(method = Method.POST, path = "/api/v1/private/directories/{directory_name}/pages/", status = 201)
    public static Path apiV1PrivateDirectoriesPagesCreate;

    @Route(method = Method.POST, path = "/api/v1/private/directories/{directory_name}/pages/audit_by_object_keys/", status = 200)
    public static Path apiV1PrivateDirectoriesPagesAuditByObjectKeys;

    @Route(method = Method.GET, path = "/api/v1/private/directories/{directory_name}/pages/audit_details/", status = 200)
    public static Path apiV1PrivateDirectoriesPagesAuditDetails;

    @Route(method = Method.GET, path = "/api/v1/private/directories/{directory_name}/pages/audit_object_keys/", status = 200)
    public static Path apiV1PrivateDirectoriesPagesAuditObjectKeys;

    @Route(method = Method.POST, path = "/api/v1/private/directories/{directory_name}/pages/obj_import/", status = 201)
    public static Path apiV1PrivateDirectoriesPagesObjImport;

    @Route(method = Method.POST, path = "/api/v1/private/directories/{directory_name}/pages/objects_export/", status = 201)
    public static Path apiV1PrivateDirectoriesPagesObjectsExport;

    @Route(method = Method.GET, path = "/api/v1/private/directories/{directory_name}/pages/{id}/", status = 200)
    public static Path apiV1PrivateDirectoriesPagesRead;

    @Route(method = Method.PUT, path = "/api/v1/private/directories/{directory_name}/pages/{id}/", status = 200)
    public static Path apiV1PrivateDirectoriesPagesUpdate;

    @Route(method = Method.PATCH, path = "/api/v1/private/directories/{directory_name}/pages/{id}/", status = 200)
    public static Path apiV1PrivateDirectoriesPagesPartialUpdate;

    @Route(method = Method.DELETE, path = "/api/v1/private/directories/{directory_name}/pages/{id}/", status = 204)
    public static Path apiV1PrivateDirectoriesPagesDelete;

    @Route(method = Method.GET, path = "/api/v1/private/directories/{directory_name}/pages/{id}/audit/", status = 200)
    public static Path apiV1PrivateDirectoriesPagesAudit;

    @Route(method = Method.GET, path = "/api/v1/private/directories/{directory_name}/pages/{id}/obj_export/", status = 200)
    public static Path apiV1PrivateDirectoriesPagesObjExport;

    @Route(method = Method.GET, path = "/api/v1/private/directories/{name}/", status = 200)
    public static Path apiV1PrivateDirectoriesRead;

    @Route(method = Method.PUT, path = "/api/v1/private/directories/{name}/", status = 200)
    public static Path apiV1PrivateDirectoriesUpdate;

    @Route(method = Method.PATCH, path = "/api/v1/private/directories/{name}/", status = 200)
    public static Path apiV1PrivateDirectoriesPartialUpdate;

    @Route(method = Method.DELETE, path = "/api/v1/private/directories/{name}/", status = 204)
    public static Path apiV1PrivateDirectoriesDelete;

    @Route(method = Method.GET, path = "/api/v1/private/directories/{name}/audit/", status = 200)
    public static Path apiV1PrivateDirectoriesAudit;

    @Route(method = Method.GET, path = "/api/v1/private/directories/{name}/obj_export/", status = 200)
    public static Path apiV1PrivateDirectoriesObjExport;

    @Route(method = Method.GET, path = "/api/v1/private/page_filters/", status = 200)
    public static Path apiV1PrivatePageFiltersList;

    @Route(method = Method.POST, path = "/api/v1/private/page_filters/", status = 201)
    public static Path apiV1PrivatePageFiltersCreate;

    @Route(method = Method.POST, path = "/api/v1/private/page_filters/audit_by_object_keys/", status = 200)
    public static Path apiV1PrivatePageFiltersAuditByObjectKeys;

    @Route(method = Method.GET, path = "/api/v1/private/page_filters/audit_details/", status = 200)
    public static Path apiV1PrivatePageFiltersAuditDetails;

    @Route(method = Method.GET, path = "/api/v1/private/page_filters/audit_object_keys/", status = 200)
    public static Path apiV1PrivatePageFiltersAuditObjectKeys;

    @Route(method = Method.POST, path = "/api/v1/private/page_filters/obj_import/", status = 201)
    public static Path apiV1PrivatePageFiltersObjImport;

    @Route(method = Method.POST, path = "/api/v1/private/page_filters/objects_export/", status = 201)
    public static Path apiV1PrivatePageFiltersObjectsExport;

    @Route(method = Method.GET, path = "/api/v1/private/page_filters/{key}/", status = 200)
    public static Path apiV1PrivatePageFiltersRead;

    @Route(method = Method.PUT, path = "/api/v1/private/page_filters/{key}/", status = 200)
    public static Path apiV1PrivatePageFiltersUpdate;

    @Route(method = Method.PATCH, path = "/api/v1/private/page_filters/{key}/", status = 200)
    public static Path apiV1PrivatePageFiltersPartialUpdate;

    @Route(method = Method.DELETE, path = "/api/v1/private/page_filters/{key}/", status = 204)
    public static Path apiV1PrivatePageFiltersDelete;

    @Route(method = Method.GET, path = "/api/v1/private/page_filters/{key}/audit/", status = 200)
    public static Path apiV1PrivatePageFiltersAudit;

    @Route(method = Method.GET, path = "/api/v1/private/page_filters/{key}/obj_export/", status = 200)
    public static Path apiV1PrivatePageFiltersObjExport;

    @Route(method = Method.POST, path = "/api/v1/private/update_page/", status = 201)
    public static Path apiV1PrivateUpdatePageCreate;

    @Route(method = Method.GET, path = "/api/v1/trees/", status = 200)
    public static Path apiV1TreesList;

    @Route(method = Method.GET, path = "/api/v1/trees/{name}/", status = 200)
    public static Path apiV1TreesRead;

    @Route(method = Method.GET, path = "/api/v1/trees/{name}/directory_pages/{directory_name}/", status = 200)
    public static Path apiV1TreesPages;

    @Route(method = Method.GET, path = "/api/v2/private/directories/{directory_name}/pages/", status = 200)
    public static Path apiV2PrivateDirectoriesPagesList;

    @Route(method = Method.POST, path = "/api/v2/private/directories/{directory_name}/pages/", status = 201)
    public static Path apiV2PrivateDirectoriesPagesCreate;

    @Route(method = Method.POST, path = "/api/v2/private/directories/{directory_name}/pages/audit_by_object_keys/", status = 200)
    public static Path apiV2PrivateDirectoriesPagesAuditByObjectKeys;

    @Route(method = Method.GET, path = "/api/v2/private/directories/{directory_name}/pages/audit_details/", status = 200)
    public static Path apiV2PrivateDirectoriesPagesAuditDetails;

    @Route(method = Method.GET, path = "/api/v2/private/directories/{directory_name}/pages/audit_object_keys/", status = 200)
    public static Path apiV2PrivateDirectoriesPagesAuditObjectKeys;

    @Route(method = Method.POST, path = "/api/v2/private/directories/{directory_name}/pages/obj_import/", status = 201)
    public static Path apiV2PrivateDirectoriesPagesObjImport;

    @Route(method = Method.POST, path = "/api/v2/private/directories/{directory_name}/pages/objects_export/", status = 201)
    public static Path apiV2PrivateDirectoriesPagesObjectsExport;

    @Route(method = Method.GET, path = "/api/v2/private/directories/{directory_name}/pages/{name}/", status = 200)
    public static Path apiV2PrivateDirectoriesPagesRead;

    @Route(method = Method.PUT, path = "/api/v2/private/directories/{directory_name}/pages/{name}/", status = 200)
    public static Path apiV2PrivateDirectoriesPagesUpdate;

    @Route(method = Method.PATCH, path = "/api/v2/private/directories/{directory_name}/pages/{name}/", status = 200)
    public static Path apiV2PrivateDirectoriesPagesPartialUpdate;

    @Route(method = Method.DELETE, path = "/api/v2/private/directories/{directory_name}/pages/{name}/", status = 204)
    public static Path apiV2PrivateDirectoriesPagesDelete;

    @Route(method = Method.GET, path = "/api/v2/private/directories/{directory_name}/pages/{name}/audit/", status = 200)
    public static Path apiV2PrivateDirectoriesPagesAudit;

    @Route(method = Method.GET, path = "/api/v2/private/directories/{directory_name}/pages/{name}/obj_export/", status = 200)
    public static Path apiV2PrivateDirectoriesPagesObjExport;

    @Override
    public String url() {
        return KONG_URL + "references";
    }
}
