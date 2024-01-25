package api.routes;

import core.helper.http.Path;

import static core.helper.Configure.KONG_URL;

public class OrderServiceApi implements Api {
    //Обновление общего ресурса
    @Route(method = Method.PATCH, path = "/v1/projects/{project_name}/shared_resources/{config_uuid}", status = 200)
    public static Path patchV1ProjectsProjectNameSharedResourcesConfigUuid;

    //Общие проверки
    @Route(method = Method.POST, path = "/v1/projects/{project_name}/validate/common_validates", status = 200)
    public static Path postV1ProjectsProjectNameValidateCommonValidates;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/order_categories", status = 200)
    public static Path getV1ProjectsProjectNameOrderCategories;

    @Route(method = Method.DELETE, path = "/v1/projects/{project_name}/orders/{id}", status = 204)
    public static Path deleteV1ProjectsProjectNameOrdersId;

    @Route(method = Method.PATCH, path = "/v1/projects/{project_name}/orders/{id}", status = 200)
    public static Path patchV1ProjectsProjectNameOrdersId;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/orders/{id}", status = 200)
    public static Path getV1ProjectsProjectNameOrdersId;

    @Route(method = Method.POST, path = "/v1/projects/{project_name}/orders/set_item_order", status = 201)
    public static Path postV1ProjectsProjectNameOrdersSetItemOrder;

    @Route(method = Method.POST, path = "/v1/projects/{project_name}/orders/{order_id}/create_similar", status = 201)
    public static Path postV1ProjectsProjectNameOrdersOrderIdCreateSimilar;

    @Route(method = Method.POST, path = "/v1/projects/{project_name}/orders/preview", status = 201)
    public static Path postV1ProjectsProjectNameOrdersPreview;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/orders", status = 200)
    public static Path getV1ProjectsProjectNameOrders;

    @Route(method = Method.POST, path = "/v1/projects/{project_name}/orders", status = 201)
    public static Path postV1ProjectsProjectNameOrders;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/orders/{id}/fetch_maintenance_requests", status = 200)
    public static Path getV1ProjectsProjectNameOrdersIdFetchMaintenanceRequests;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/orders/{id}/check_maintenance_mode", status = 200)
    public static Path getV1ProjectsProjectNameOrdersIdCheckMaintenanceMode;

    //Выполнение действия с заказом
    @Route(method = Method.PATCH, path = "/v1/projects/{project_name}/orders/{id}/actions/{action_name}", status = 200)
    public static Path patchV1ProjectsProjectNameOrdersIdActionsActionName;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/orders/{id}/actions/errors", status = 200)
    public static Path getV1ProjectsProjectNameOrdersIdActionsErrors;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/orders/{id}/actions/history/{action_id}/output", status = 200)
    public static Path getV1ProjectsProjectNameOrdersIdActionsHistoryActionIdOutput;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/orders/{id}/actions/history/{action_id}", status = 200)
    public static Path getV1ProjectsProjectNameOrdersIdActionsHistoryActionId;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/orders/{id}/actions/history", status = 200)
    public static Path getV1ProjectsProjectNameOrdersIdActionsHistory;

    @Route(method = Method.POST, path = "/v1/projects/{project_name}/orders/bulk_import", status = 201)
    public static Path postV1ProjectsProjectNameOrdersBulkImport;

    @Route(method = Method.POST, path = "/v1/projects/{project_name}/orders/import", status = 201)
    public static Path postV1ProjectsProjectNameOrdersImport;

    @Route(method = Method.DELETE, path = "/v1/projects/{project_name}/orders/vault_service", status = 204)
    public static Path deleteV1ProjectsProjectNameOrdersVaultService;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/orders/vault_service", status = 200)
    public static Path getV1ProjectsProjectNameOrdersVaultService;

    @Route(method = Method.POST, path = "/v1/projects/{project_name}/orders/vault_service", status = 201)
    public static Path postV1ProjectsProjectNameOrdersVaultService;

    @Route(method = Method.DELETE, path = "/v1/projects/{project_name}/orders/ceph_tenants", status = 204)
    public static Path deleteV1ProjectsProjectNameOrdersCephTenants;

    @Route(method = Method.POST, path = "/v1/projects/{project_name}/orders/ceph_tenants", status = 201)
    public static Path postV1ProjectsProjectNameOrdersCephTenants;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/orders/ceph_tenant", status = 200)
    public static Path getV1ProjectsProjectNameOrdersCephTenant;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/orders/{id}/change_project/available", status = 200)
    public static Path getV1ProjectsProjectNameOrdersIdChangeProjectAvailable;

    //Получение списка ВМ
    @Route(method = Method.GET, path = "/v1/projects/{project_name}/items/vms", status = 200)
    public static Path getV1ProjectsProjectNameItemsVms;

    //Проверка на выполнение действия с заказом по имени продукта
    @Route(method = Method.PATCH, path = "/v1/projects/{project_name}/project_orders/{product_name}/actions/{action_name}/dry_run", status = 200)
    public static Path patchV1ProjectsProjectNameProjectOrdersProductNameActionsActionNameDryRun;

    //Выполнение действия с заказом по имени продукта
    @Route(method = Method.PATCH, path = "/v1/projects/{project_name}/project_orders/{product_name}/actions/{action_name}", status = 200)
    public static Path patchV1ProjectsProjectNameProjectOrdersProductNameActionsActionName;

    @Route(method = Method.DELETE, path = "/v1/projects/{project_name}/project_orders/{product_name}", status = 204)
    public static Path deleteV1ProjectsProjectNameProjectOrdersProductName;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/project_orders/{product_name}", status = 200)
    public static Path getV1ProjectsProjectNameProjectOrdersProductName;

    @Route(method = Method.POST, path = "/v1/projects/{project_name}/project_orders/{product_name}", status = 201)
    public static Path postV1ProjectsProjectNameProjectOrdersProductName;

    @Route(method = Method.POST, path = "/v1/projects/{project_name}/project_orders/{product_name}/dry_run", status = 201)
    public static Path postV1ProjectsProjectNameProjectOrdersProductNameDryRun;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/order_actions", status = 200)
    public static Path getV1ProjectsProjectNameOrderActions;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/snats/{id}", status = 200)
    public static Path getV1ProjectsProjectNameComputeSnatsId;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/vips/{id}", status = 200)
    public static Path getV1ProjectsProjectNameComputeVipsId;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/backups/{id}", status = 200)
    public static Path getV1ProjectsProjectNameComputeBackupsId;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/public_ips/{id}", status = 200)
    public static Path getV1ProjectsProjectNameComputePublicIpsId;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/instances/{id}", status = 200)
    public static Path getV1ProjectsProjectNameComputeInstancesId;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/nics/{id}", status = 200)
    public static Path getV1ProjectsProjectNameComputeNicsId;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/snapshots/{id}", status = 200)
    public static Path getV1ProjectsProjectNameComputeSnapshotsId;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/images/{id}", status = 200)
    public static Path getV1ProjectsProjectNameComputeImagesId;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/volumes/{id}", status = 200)
    public static Path getV1ProjectsProjectNameComputeVolumesId;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/snats", status = 200)
    public static Path getV1ProjectsProjectNameComputeSnats;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/vips", status = 200)
    public static Path getV1ProjectsProjectNameComputeVips;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/backups", status = 200)
    public static Path getV1ProjectsProjectNameComputeBackups;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/public_ips", status = 200)
    public static Path getV1ProjectsProjectNameComputePublicIps;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/instances", status = 200)
    public static Path getV1ProjectsProjectNameComputeInstances;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/nics", status = 200)
    public static Path getV1ProjectsProjectNameComputeNics;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/snapshots", status = 200)
    public static Path getV1ProjectsProjectNameComputeSnapshots;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/images", status = 200)
    public static Path getV1ProjectsProjectNameComputeImages;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/volumes", status = 200)
    public static Path getV1ProjectsProjectNameComputeVolumes;

    @Route(method = Method.GET, path = "/v1/projects/{project_name}/compute/placement_policies", status = 200)
    public static Path getV1ProjectsProjectNamePlacementPolicies;

    @Route(method = Method.GET, path = "/v1/availability_zones", status = 200)
    public static Path getV1AvailabilityZones;

    @Route(method = Method.GET, path = "/v1/data_centers", status = 200)
    public static Path getV1DataCenters;

    @Route(method = Method.GET, path = "/v1/data_centers/{code}", status = 200)
    public static Path getV1DataCentersCode;

    @Route(method = Method.GET, path = "/v1/domains", status = 200)
    public static Path getV1Domains;

    @Route(method = Method.GET, path = "/v1/net_segments", status = 200)
    public static Path getV1NetSegments;

    @Route(method = Method.GET, path = "/v1/net_segments/{code}", status = 200)
    public static Path getV1NetSegmentsCode;

    @Route(method = Method.GET, path = "/v1/orders", status = 200)
    public static Path getV1Orders;

    @Route(method = Method.PATCH, path = "/v1/orders", status = 200)
    public static Path patchV1Orders;

    @Route(method = Method.DELETE, path = "/v1/orders", status = 204)
    public static Path deleteV1Orders;

    @Route(method = Method.POST, path = "/v1/orders/run_maintenance", status = 201)
    public static Path postV1OrdersRunMaintenance;

    @Route(method = Method.POST, path = "/v1/orders/update_tariff_plan_id", status = 201)
    public static Path postV1OrdersUpdateTariffPlanId;

    @Route(method = Method.GET, path = "/v1/orders/geo_distribution_info", status = 200)
    public static Path getV1OrdersGeoDistributionInfo;

    @Route(method = Method.GET, path = "/v1/orders/available_disk_types", status = 200)
    public static Path getV1OrdersAvailableDiskTypes;

    @Route(method = Method.GET, path = "/v1/orders/product_label", status = 200)
    public static Path getV1OrdersProductLabel;

    @Route(method = Method.GET, path = "/v1/orders/check_ip", status = 200)
    public static Path getV1OrdersCheckIp;

    @Route(method = Method.GET, path = "/v1/orders/check_field_uniqueness", status = 200)
    public static Path getV1OrdersCheckFieldUniqueness;

    @Route(method = Method.POST, path = "/v1/orders/{id}/add_exist_action", status = 201)
    public static Path postV1OrdersIdAddExistAction;

    @Route(method = Method.GET, path = "/v1/orders/{id}", status = 200)
    public static Path getV1OrdersId;

    @Route(method = Method.POST, path = "/v1/orders/{id}/update_orders_error", status = 201)
    public static Path postV1OrdersIdUpdateOrdersError;

    @Route(method = Method.DELETE, path = "/v1/orders/project_orders/{product_name}", status = 204)
    public static Path deleteV1OrdersProjectOrdersProductName;

    @Route(method = Method.GET, path = "/v1/orders/project_orders/{product_name}", status = 200)
    public static Path getV1OrdersProjectOrdersProductName;

    @Route(method = Method.POST, path = "/v1/orders/project_orders/{product_name}", status = 201)
    public static Path postV1OrdersProjectOrdersProductName;

    @Route(method = Method.POST, path = "/v1/orders/project_orders/{product_name}/dry_run", status = 201)
    public static Path postV1OrdersProjectOrdersProductNameDryRun;

    //Проверка на выполнение действия с заказом по имени продукта
    @Route(method = Method.PATCH, path = "/v1/orders/project_orders/{product_name}/actions/{action_name}/dry_run", status = 200)
    public static Path patchV1OrdersProjectOrdersProductNameActionsActionNameDryRun;

    //Выполнение действия с продуктом
    @Route(method = Method.PATCH, path = "/v1/orders/project_orders/{product_name}/actions/{action_name}", status = 200)
    public static Path patchV1OrdersProjectOrdersProductNameActionsActionName;

    @Route(method = Method.POST, path = "/v1/orders/actions", status = 201)
    public static Path postV1OrdersActions;

    @Route(method = Method.GET, path = "/v1/platforms", status = 200)
    public static Path getV1Platforms;

    @Route(method = Method.GET, path = "/v1/platforms/{code}", status = 200)
    public static Path getV1PlatformsCode;

    @Route(method = Method.GET, path = "/v1/products/resource_pools", status = 200)
    public static Path getV1ProductsResourcePools;

    @Route(method = Method.GET, path = "/v1/health", status = 200)
    public static Path getV1Health;

    @Route(method = Method.PATCH, path = "/v1/order_actions", status = 200)
    public static Path patchV1OrderActions;

    @Route(method = Method.GET, path = "/v1/order_actions", status = 200)
    public static Path getV1OrderActions;

    @Route(method = Method.GET, path = "/v1/version", status = 200)
    public static Path getV1Version;

    @Override
    public String url() {
        return KONG_URL + "order-service/api";
    }
}
