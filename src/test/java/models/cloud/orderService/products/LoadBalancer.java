package models.cloud.orderService.products;

import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.subModels.Flavor;
import models.cloud.subModels.loadBalancer.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;
import steps.references.ReferencesStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class LoadBalancer extends IProduct {
    @ToString.Include
    String osVersion;
    Flavor flavor;
    String password;
    String zone;
    @Builder.Default
    List<Backend> backends = new ArrayList<>();
    @Builder.Default
    List<Frontend> frontends = new ArrayList<>();
    @Builder.Default
    List<Gslb> gslbs = new ArrayList<>();
    @Builder.Default
    List<RouteSni> routes = new ArrayList<>();
    private final String FRONTEND_PATH = "data.find{it.type=='cluster'}.data.config.frontends.find{it.frontend_name == '%s'}";
    private final String BACKEND_PATH = "data.find{it.type=='cluster'}.data.config.backends.find{it.backend_name == '%s'}";
    private final String GSLIB_PATH = "data.find{it.type=='cluster'}.data.config.polaris_config.find{it.globalname.contains('%s')}";
    private final String ROUTE_PATH = "data.find{it.type=='cluster'}.data.config.sni_routes.find{it.route_name.contains('%s')}";
    private final String BACKUP_LAST_PATH = "data.find{it.type=='cluster'}.data.config.backup_dirs.sort{it.index}.last()";

    @Override
    public Entity init() {
        jsonTemplate = "/orders/load_balancer.json";
        productName = "Load Balancer";
        initProduct();
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (password == null)
            password = "AuxDG%Yg%wtfCqL3!kopIPvX%ud1HY@J";
        if (segment == null)
            setSegment(OrderServiceSteps.getNetSegment(this));
        if (availabilityZone == null)
            setAvailabilityZone(OrderServiceSteps.getAvailabilityZone(this));
        if (platform == null)
            setPlatform(OrderServiceSteps.getPlatform(this));
        if (domain == null)
            setDomain(OrderServiceSteps.getDomain(this));
        if (zone == null)
            setZone(ReferencesStep.getJsonPathList(String
                            .format("tags__contains=%s,available&directory__name=gslb_servers", segment))
                    .getString("[0].data.name"));
        if (flavor == null)
            flavor = getMinFlavor();
        return this;
    }

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.availability_zone", getAvailabilityZone())
                .set("$.order.attrs.platform", getPlatform())
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.password", password)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup())
                .set("$.order.attrs.ad_logon_grants[0].role", "superuser")
                .set("$.order.attrs.dns_zone", zone)
                .set("$.order.attrs.ad_integration", true)
                .set("$.order.project_name", project.id)
                .set("$.order.label", getLabel())
                .set("$.order.attrs.layout", getIdGeoDistribution("balancer-2"))
                .set("$.order.attrs.on_support", getSupport())
                .build();
    }

    public void sync() {
        OrderServiceSteps.executeAction("balancer_release_sync_info", this, null, this.getProjectId());
    }

    public void gslbSync() {
        OrderServiceSteps.executeAction("balancer_gslb_release_sync_info", this, null, this.getProjectId());
    }

    public void revertConfig(Backend backend) {
        addBackend(backend);
        Map<Integer, String> res = OrderServiceSteps.getProductsField(this, BACKUP_LAST_PATH, Map.class);
        JSONObject data = new JSONObject().put("backup", new JSONObject(res));
        OrderServiceSteps.executeAction("balancer_release_revert_config", this, data, this.getProjectId());
        Assertions.assertNull(OrderServiceSteps.getObjectClass(this,
                String.format(BACKEND_PATH, backend.getBackendName()), Backend.class), "Backend не удален");
        backends.remove(backend);
        save();
    }

    public void deleteAllGslb() {
        OrderServiceSteps.executeAction("balancer_gslb_release_delete_publications_by_orderid", this, null, this.getProjectId());
        gslbs.clear();
    }

    public void addBackend(Backend backend) {
        if (backends.contains(backend))
            return;
        OrderServiceSteps.executeAction("balancer_release_create_backend", this, new JSONObject(JsonHelper.toJson(backend)), this.getProjectId());
        Assertions.assertNotNull(OrderServiceSteps.getObjectClass(this,
                String.format(BACKEND_PATH, backend.getBackendName()), Backend.class), "Backend не создался");
        backends.add(backend);
        save();
    }

    public void changeActiveStandbyMode(StandbyMode standbyMode) {
        OrderServiceSteps.executeAction("balancer_release_change_active_standby_mode", this, serialize(standbyMode), this.getProjectId());
    }

    public void addFrontend(Frontend frontend) {
        if (frontends.contains(frontend))
            return;
        OrderServiceSteps.executeAction("balancer_release_create_frontend", this, new JSONObject(JsonHelper.toJson(frontend)), this.getProjectId());
        Assertions.assertNotNull(OrderServiceSteps.getObjectClass(this,
                String.format(FRONTEND_PATH, frontend.getFrontendName()), Frontend.class), "Frontend не создался");
        frontends.add(frontend);
        save();
    }

    public void addGslb(Gslb gslb) {
        if (gslbs.contains(gslb))
            return;
        OrderServiceSteps.executeAction("balancer_gslb_release_create_publication", this, new JSONObject(JsonHelper.toJson(gslb)), this.getProjectId());
        Assertions.assertNotNull(OrderServiceSteps.getObjectClass(this,
                String.format(GSLIB_PATH, gslb.getGlobalname()), Gslb.class), "gslb не создался");
        gslbs.add(gslb);
        save();
    }

    public void addRouteSni(RouteSni route) {
        if (routes.contains(route))
            return;
        OrderServiceSteps.executeAction("balancer_release_create_route_sni", this, new JSONObject(JsonHelper.toJson(route)), this.getProjectId());
        Assertions.assertNotNull(OrderServiceSteps.getObjectClass(this,
                String.format(ROUTE_PATH, route.getRoutes().get(0).getName()), RouteSni.RouteCheck.class), "route не создался");
        routes.add(route);
        save();
    }

    public void deleteBackend(Backend backend) {
        OrderServiceSteps.executeAction("balancer_release_delete_backend", this,
                new JSONObject().put("backend_name", backend.getBackendName()), this.getProjectId());
        Assertions.assertNull(OrderServiceSteps.getObjectClass(this,
                String.format(BACKEND_PATH, backend.getBackendName()), Backend.class), "Backend не удален");
        backends.remove(backend);
        save();
        if (isDev())
            Assertions.assertFalse(isStateContains(backend.getBackendName()));
    }

    public void editBackend(String backendName, String action, List<Server> servers) {
        OrderServiceSteps.executeAction("balancer_release_edit_backend", this,
                new JSONObject().put("backend_name", backendName).put("action", action).put("servers", servers), this.getProjectId());
    }

    public void createHealthCheck(HealthCheck healthCheck) {
        OrderServiceSteps.executeAction("balancer_release_create_health_check", this,
                serialize(healthCheck), this.getProjectId());
    }

    public void editFrontEnd(Frontend frontend, boolean isTcpBackend, String backendName, Integer port) {
        String backendFiled = isTcpBackend ? "default_backend_name_tcp" : "default_backend_name_http";
        JSONObject data = new JSONObject().put(backendFiled, backendName).put("frontend", serialize(frontend)).put("frontend_port", port);
        OrderServiceSteps.executeAction("balancer_release_edit_frontend", this, data, this.getProjectId());
    }

    public void deleteFrontend(Frontend frontend) {
        OrderServiceSteps.executeAction("balancer_release_delete_frontend", this,
                new JSONObject().put("frontend_name", frontend.getFrontendName()), this.getProjectId());
        Assertions.assertNull(OrderServiceSteps.getObjectClass(this,
                String.format(FRONTEND_PATH, frontend.getFrontendName()), Frontend.class), "Frontend не удален");
        frontends.remove(frontend);
        save();
        if (isDev())
            Assertions.assertFalse(isStateContains(frontend.getFrontendName()));
    }

    public void deleteFrontends(List<Frontend> frontends) {
        OrderServiceSteps.executeAction("balancer_release_delete_frontends", this,
                new JSONObject().put("frontends", serializeList(frontends)), this.getProjectId());
        frontends.forEach(e -> {
            Assertions.assertNull(OrderServiceSteps.getObjectClass(this,
                    String.format(FRONTEND_PATH, e.getFrontendName()), Frontend.class), "Frontend не удален");
            frontends.remove(e);
        });
        save();
    }

    public void deleteGslb(Gslb gslb) {
        gslbs.remove(gslb);
        gslb = (Gslb) OrderServiceSteps.getObjectClass(this, String.format(GSLIB_PATH, gslb.getGlobalname()), Gslb.class);
        deleteGslbSource(gslb.getGlobalname());
        Assertions.assertNull(OrderServiceSteps.getObjectClass(this, String.format(GSLIB_PATH, gslb.getGlobalname()), Gslb.class), "gslb не удален");
        save();
    }

    public void deleteGslbSource(String globalName) {
        OrderServiceSteps.executeAction("balancer_gslb_release_delete_publication", this,
                new JSONObject().put("globalname", globalName), this.getProjectId());
    }

    public Boolean isStateContains(String name) {
        String url = (String) OrderServiceSteps.getProductsField(this, "data.find{it.data.config.containsKey('console_urls')}.data.config.console_urls[0]");
        RequestSpecification specification = RestAssured.given()
                .config(RestAssured.config().sslConfig(Http.sslConfig));
        return RestAssured.given().spec(specification).auth().preemptive().basic("stats", password)
                .post(url)
                .then()
                .statusCode(200)
                .extract().response().htmlPath()
                .getBoolean(String.format("**.any{it.@name == '%s'}", name));
    }

    public void stopHard() {
        stopHard("stop_vm_hard");
    }

    public void stopSoft() {
        stopSoft("stop_vm_soft");
    }

    public void start() {
        start("start_vm");
    }

    public void restart() {
        restart("reset_vm");
    }

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point_new", "/app", 10);
    }

    public void resize(Flavor flavor) {
        resize("resize_vm", flavor);
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("balancer_release_delete");
    }

}
