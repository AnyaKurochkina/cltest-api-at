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
import models.cloud.authorizer.Organization;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.subModels.Flavor;
import models.cloud.subModels.loadBalancer.Backend;
import models.cloud.subModels.loadBalancer.Frontend;
import models.cloud.subModels.loadBalancer.Gslb;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;
import steps.portalBack.PortalBackSteps;

import java.util.*;

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
    @Builder.Default
    List<Backend> backends = new ArrayList<>();
    @Builder.Default
    List<Frontend> frontends = new ArrayList<>();
    @Builder.Default
    List<Gslb> gslbs = new ArrayList<>();
    private final String FRONTEND_PATH = "data.find{it.type=='cluster'}.data.config.frontends.find{it.frontend_name == '%s'}";
    private final String BACKEND_PATH = "data.find{it.type=='cluster'}.data.config.backends.find{it.backend_name == '%s'}";
    private final String GSLIB_PATH = "data.find{it.type=='cluster'}.data.config.polaris_config.find{it.globalname.contains('%s')}";
    private final String BACKUP_LAST_PATH = "data.find{it.type=='cluster'}.data.config.backup_dirs.sort{it.index}.last()";
    @Override
    public Entity init() {
        jsonTemplate = "/orders/load_balancer.json";
        productName = "Load Balancer";
        initProduct();
        if (flavor == null)
            flavor = getMinFlavor();
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (password == null)
            password = "W1clvyliiSCyE0gs";
        if(segment == null)
            setSegment(OrderServiceSteps.getNetSegment(this));
        if(dataCentre == null)
            setDataCentre(OrderServiceSteps.getDataCentre(this));
        if(platform == null)
            setPlatform(OrderServiceSteps.getPlatform(this));
        if(domain == null)
            setDomain(OrderServiceSteps.getDomain(this));
        return this;
    }

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    @Override
    public JSONObject toJson() {
        Organization org = Organization.builder().build().createObject();
        Project project = Project.builder().id(projectId).build().createObject();
        String accessGroup = PortalBackSteps.getRandomAccessGroup(getProjectId(), getDomain(), "compute");
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.data_center", getDataCentre())
                .set("$.order.attrs.platform", getPlatform())
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.password", password)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup)
                .set("$.order.attrs.ad_logon_grants[0].role", "superuser")
                .set("$.order.attrs.ad_integration", true)
                .set("$.order.project_name", project.id)
                .set("$.order.label", getLabel())
                .set("$.order.attrs.layout", getIdGeoDistribution("balancer-1", envType().toUpperCase(), "balancer", org.getName()))
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
    }

    public void addBackend(Backend backend) {
        if (backends.contains(backend))
            return;
        OrderServiceSteps.executeAction("balancer_release_create_backend", this, new JSONObject(JsonHelper.toJson(backend)), this.getProjectId());
        Assertions.assertNotNull(OrderServiceSteps.getObjectClass(this,
                String.format(BACKEND_PATH, backend.getBackendName()), Backend.class), "Backend не создался");
        backends.add(backend);
        save();
        if (isDev())
            Assertions.assertTrue(isStateContains(backend.getBackendName()));
    }

    public void addFrontend(Frontend frontend) {
        if (frontends.contains(frontend))
            return;
        OrderServiceSteps.executeAction("balancer_release_create_frontend", this, new JSONObject(JsonHelper.toJson(frontend)), this.getProjectId());
        Assertions.assertNotNull(OrderServiceSteps.getObjectClass(this,
                String.format(FRONTEND_PATH, frontend.getFrontendName()), Frontend.class), "Frontend не создался");
        frontends.add(frontend);
        save();
        if (isDev())
            Assertions.assertTrue(isStateContains(frontend.getFrontendName()));
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

    public void deleteGslb(Gslb gslb) {
        gslbs.remove(gslb);
        gslb = (Gslb) OrderServiceSteps.getObjectClass(this, String.format(GSLIB_PATH, gslb.getGlobalname()), Gslb.class);
        OrderServiceSteps.executeAction("balancer_gslb_release_delete_publication", this,
                new JSONObject().put("globalname", gslb.getGlobalname()), this.getProjectId());
        Assertions.assertNull(OrderServiceSteps.getObjectClass(this, String.format(GSLIB_PATH, gslb.getGlobalname()), Gslb.class), "gslb не удален");
        save();
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
