package models.cloud.orderService.products;

import core.helper.JsonHelper;
import core.helper.http.Http;
import core.utils.AssertUtils;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.subModels.Flavor;
import models.cloud.subModels.loadBalancer.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.ActionParameters;
import steps.orderService.OrderServiceSteps;
import steps.references.ReferencesStep;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private final String FRONTEND_PATH = "data.find{it.type=='cluster'}.data.config.frontends.find{it.frontend_name == '%s'}";
    private final String BACKEND_PATH = "data.find{it.type=='cluster'}.data.config.backends.find{it.backend_name == '%s'}";
    private final String GSLIB_PATH = "data.find{it.type=='cluster'}.data.config.polaris_config.find{it.globalname.contains('%s')}";
    private final String ROUTE_PATH = "data.find{it.type=='cluster'}.data.config.sni_routes.find{it.route_name.contains('%s')}";
    private final String ROUTE_PATH_BACKEND = "data.find{it.type=='cluster'}.data.config.sni_routes.find{it.route_name.contains('%s') && it.backend_name.contains('%s')}";
    private final String BACKUP_LAST_PATH = "data.find{it.type=='cluster'}.data.config.backup_dirs.sort{it.index}[-2].backup_name";
    private final String ALIAS_PATH = "data.find{it.type=='cluster'}.data.config.sni_routes.find{it.aliases.contains('%s')}";
    private final String HEALTH_CHECK_PATH = "data.find{it.type=='cluster'}.data.config.health_checks.find{it.backend_name == '%s'}";

    @Override
    public Entity init() {
        jsonTemplate = "/orders/load_balancer.json";
        productName = "Load Balancer";
        initProduct();
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
        if (osVersion == null)
            osVersion = getRandomOsVersion();
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
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_sync_info").product(this).build());
    }

    public void revertConfig(Backend backend) {
        String backup = OrderServiceSteps.getObjectClass(this, BACKUP_LAST_PATH, String.class);
        JSONObject data = new JSONObject().put("backup", backup);
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_revert_config").product(this).data(data).build());
        Assertions.assertFalse(isExistBackend(backend.getBackendName()), "Backend не удален");
    }

    public void deleteAllGslb() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_gslb_release_delete_publications_by_orderid").product(this).build());
    }

    public void addBackend(Backend backend) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_create_backend").product(this)
                .data(new JSONObject(JsonHelper.toJson(backend))).build());
        Assertions.assertTrue(isExistBackend(backend.getBackendName()), "Backend не создался");
    }

    public void addFrontend(Frontend frontend) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_create_frontend").product(this)
                .data(new JSONObject(JsonHelper.toJson(frontend))).build());
        Assertions.assertTrue(isExistFrontend(frontend.getFrontendName()), "Frontend не создался");
    }

    public void addGslb(Gslb gslb) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_gslb_release_create_publication")
                .product(this).data(new JSONObject(JsonHelper.toJson(gslb))).build());
        Assertions.assertTrue(isExistGslb(gslb.getGlobalname()), "Gslb не создался");
    }

    public void addRoute(String globalName, RouteSni.Route... routes) {
        if (routes.length == 0)
            return;
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_create_route_sni").product(this)
                .data(new JSONObject().put("globalname", globalNameFull(globalName)).put("routes", serializeList(routes))).build());
        for (RouteSni.Route route : routes)
            Assertions.assertTrue(isExistRoute(route.getName()), "Route не создался");
    }

    public void addBackendUseCache(Backend backend) {
        if (!isExistBackend(backend.getBackendName()))
            addBackend(backend);
    }

    public void addFrontendUseCache(Frontend frontend) {
        if (!isExistFrontend(frontend.getFrontendName()))
            addFrontend(frontend);
    }

    public void addGslbUseCache(Gslb gslb) {
        if (!isExistGslb(gslb.getGlobalname()))
            addGslb(gslb);
    }

    public void addRouteUseCache(String globalName, RouteSni.Route... routes) {
        List<RouteSni.Route> routesForCreate = new ArrayList<>();
        for (RouteSni.Route route : routes)
            if (!isExistRoute(route.getName()))
                routesForCreate.add(route);
        addRoute(globalName, routesForCreate.toArray(new RouteSni.Route[0]));
    }

    private boolean isExistBackend(String name) {
        return Objects.nonNull(OrderServiceSteps.getObjectClass(this, String.format(BACKEND_PATH, name), Backend.class));
    }

    private boolean isExistFrontend(String name) {
        return Objects.nonNull(OrderServiceSteps.getObjectClass(this, String.format(FRONTEND_PATH, name), Frontend.class));
    }

    private boolean isExistGslb(String name) {
        return Objects.nonNull(OrderServiceSteps.getObjectClass(this, String.format(GSLIB_PATH, name), Gslb.class));
    }

    private boolean isExistRoute(String name) {
        return Objects.nonNull(OrderServiceSteps.getObjectClass(this, String.format(ROUTE_PATH, name), RouteSni.RouteCheck.class));
    }

    public void addAliases(String routeName, String... aliases) {
        if (aliases.length == 0)
            return;
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_create_alias_for_sni").product(this)
                .data(new JSONObject().put("route_name", routeByName(routeName).getRouteName()).put("aliases", serializeList(aliases))).build());
        List<String> routeAliases = routeByName(routeName).getAliases();
        for (String alias : aliases)
            AssertUtils.assertContainsList(routeAliases, alias);
    }

    public void deleteBackends(Backend... backends) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_delete_backends").product(this)
                .data(new JSONObject().put("selected", serializeList(backends))).build());
        for (Backend backend : backends) {
            Assertions.assertFalse(isExistBackend(backend.getBackendName()), "Backend не удален");
        }
    }

    public void editBackend(Backend backend) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_edit_backend").product(this)
                .data(serialize(backend)).build());
    }

    public void editHealthCheck(HealthCheck healthCheck) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_create_health_check").product(this)
                .data(serialize(healthCheck)).build());
    }

    public void editFrontEnd(Frontend frontend) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_edit_frontend").product(this)
                .data(new JSONObject(JsonHelper.toJson(frontend))).build());
    }

    public void deleteFrontends(Frontend... frontends) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_delete_frontends").product(this)
                .data(new JSONObject().put("selected", serializeList(frontends))).build());
        for (Frontend frontend : frontends)
            Assertions.assertFalse(isExistFrontend(frontend.getFrontendName()), "Frontend не удален");
    }

    public void deleteGslb(Gslb gslb) {
        gslb = OrderServiceSteps.getObjectClass(this, String.format(GSLIB_PATH, gslb.getGlobalname()), Gslb.class);
        deleteGslbSource(gslb.getGlobalname());
        Assertions.assertNull(OrderServiceSteps.getObjectClass(this, String.format(GSLIB_PATH, gslb.getGlobalname()), Gslb.class), "gslb не удален");
    }

    public String globalNameFull(String globalName) {
        return Objects.requireNonNull(OrderServiceSteps.getObjectClass(this, String.format(GSLIB_PATH, globalName), Gslb.class),
                        "Не найден GSLB " + globalName)
                .getGlobalname();
    }

    public String healthCheckByBackendName(String backendName) {
        return Objects.requireNonNull(OrderServiceSteps.getObjectClass(this, String.format(HEALTH_CHECK_PATH, backendName), HealthCheck.class),
                        "Не найден healthCheck по backend " + backendName)
                .getCheckName();
    }

    public RouteSni.RouteCheck routeByName(String routeName) {
        return OrderServiceSteps.getObjectClass(this, String.format(ROUTE_PATH, routeName), RouteSni.RouteCheck.class);
    }

    public void deleteGslbSource(String globalName) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_gslb_release_delete_publication")
                .product(this).data(new JSONObject().put("globalname", globalName)).build());
    }

    public void deleteRouteSni(String routeName) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_delete_sni_route").product(this)
                .data(new JSONObject().put("sni_route", routeByName(routeName).getRouteName())).build());
        Assertions.assertFalse(isExistRoute(routeName), "Route не удалился");
    }

    public void editRouteSni(RouteSni.Route route, String backendName) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_edit_sni_route").product(this)
                .data(new JSONObject().put("backend_name", backendName).put("sni_route", routeByName(route.getName()).getRouteName())).build());
        Assertions.assertEquals(backendName, routeByName(route.getName()).getBackendName(), "BackendName не изменен");
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
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_expand_mount_point").product(this)
                .data(new JSONObject().put("size", 10).put("mount", "/app")).build());
    }

    public void resize(Flavor flavor) {
        resize("resize_vm", flavor);
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("balancer_release_delete");
    }

    public void fullSync() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_full_sync").product(this).build());
    }

    public void changePublicationsMaintenanceMode(ChangePublicationsMaintenanceMode mode) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_gslb_release_change_publications_maintenance_mode")
                .product(this).data(serialize(mode)).build());
    }

    public void editTimeouts(int clientTimeout, int connectTimeout, int serverTimeout) {
        JSONObject data = new JSONObject().put("client_timeout", clientTimeout).put("connect_timeout", connectTimeout).put("server_timeout", serverTimeout);
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_edit_timeouts").product(this).data(data).build());
    }

    public void updateOs() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_update_os").product(this)
                .data(new JSONObject().put("accept", true)).build());
    }

    public void resizeClusterVms(Flavor flavor) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_resize_cluster_vms").product(this).timeout(Duration.ofHours(2))
                .data(new JSONObject().put("flavor", new JSONObject(flavor.toString())).put("accept", true)).build());
        int cpusAfter = (Integer) OrderServiceSteps.getProductsField(this, CPUS);
        int memoryAfter = (Integer) OrderServiceSteps.getProductsField(this, MEMORY);
        Assertions.assertEquals(flavor.data.cpus, cpusAfter, "Конфигурация cpu не изменилась или изменилась неверно");
        Assertions.assertEquals(flavor.data.memory, memoryAfter, "Конфигурация ram не изменилась или изменилась неверно");
    }

    public void addHaproxy(int haproxyCount) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_add_haproxy").checkPrebilling(false).product(this)
                .data(new JSONObject().put("new_haproxy_count", haproxyCount).put("check_agree", true)).timeout(Duration.ofMinutes(60)).build());
    }

    public void complexCreate(ComplexCreate complex) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_complex_create").product(this)
                .data(serialize(complex)).build());
    }

    public void updateCluster(boolean requiredUpdateCerts) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("balancer_release_update_cluster").timeout(Duration.ofHours(2)).product(this)
                .data(new JSONObject().put("required_update_certs", requiredUpdateCerts).put("accept", true)).build());
    }
}
