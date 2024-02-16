package models.cloud.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
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
import org.json.JSONObject;
import steps.orderService.ActionParameters;
import steps.orderService.OrderServiceSteps;

import java.time.Duration;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class OpenMessagingAstra extends IProduct {
    Flavor flavor;
    String osVersion;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/open_messaging_astra.json";
        productName = "OpenMessaging Astra";
        if (env.equalsIgnoreCase("LT"))
            productName = "OpenMessaging LT Astra";
        initProduct();
        if (segment == null)
            setSegment(OrderServiceSteps.getNetSegment(this));
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (flavor == null)
            flavor = getMinFlavor();
        if (availabilityZone == null)
            setAvailabilityZone(OrderServiceSteps.getAvailabilityZone(this));
        if (platform == null)
            setPlatform(OrderServiceSteps.getPlatform(this));
        if (domain == null)
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
        Project project = Project.builder().id(projectId).build().createObject();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.availability_zone", getAvailabilityZone())
                .set("$.order.attrs.platform", getPlatform())
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup())
                .set("$.order.attrs.ad_logon_grants[0].role", isDev() ? "superuser" : "user")
                .set("$.order.attrs.ad_integration", true)
                .set("$.order.project_name", project.id)
                .set("$.order.label", getLabel())
                .set("$.order.attrs.on_support", getSupport())
                .build();
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }

    public void upgradeSetup() {
        String name = "open-messaging_upgrade_setup_release";
        if (env.equalsIgnoreCase("LT"))
            name = "openmessaging_lt_upgrade_setup_release";
        OrderServiceSteps.runAction(ActionParameters.builder().name(name).product(this).build());
    }

    public void updateOS() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("openmessaging_lt_update_os_release").product(this).build());
    }

    public void updateCerts() {
        updateCerts("openmessaging_lt_update_certificates_release");
    }

    public void verticalScaling() {
        final Flavor maxFlavor = getMaxFlavor();
        JSONObject data = JsonHelper.getJsonTemplate("/orders/open_messaging_astra_vertical_scaling.json")
                .set("$.current_flavor", flavor.getName())
                .set("$.state_service_flavor_name", flavor.getName())
                .set("$.state_service_ram", flavor.getMemory())
                .set("$.state_service_cpu", flavor.getCpus())
                .set("$.flavor", new JSONObject(maxFlavor.toString())).build();
        OrderServiceSteps.runAction(ActionParameters.builder().name("openmessaging_vertical_scaling_release").product(this)
                .data(data).timeout(Duration.ofHours(1)).build());
        flavor = maxFlavor;
        save();
    }
}
