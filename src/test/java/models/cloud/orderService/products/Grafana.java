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

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class Grafana extends IProduct {
    @ToString.Include
    String osVersion;
    Flavor flavor;
    String grafanaVersion;
    String usersPassword;
    String users;

    @Override
    public Entity init() {
        jsonTemplate = "/orders/grafana.json";
        productName = "Grafana";
        initProduct();
        if (segment == null)
            setSegment(OrderServiceSteps.getNetSegment(this));
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (availabilityZone == null)
            setAvailabilityZone(OrderServiceSteps.getAvailabilityZone(this));
        if (platform == null)
            setPlatform(OrderServiceSteps.getPlatform(this));
        if (domain == null)
            setDomain(OrderServiceSteps.getDomain(this));
        if (grafanaVersion == null)
            grafanaVersion = getRandomProductVersionByPathEnum("grafana_version.enum");
        if (usersPassword == null)
            usersPassword = "Ya30GpR49Dget4yY6v3DBBNjJOwcd";
        if (users == null)
            users = "grafana_user";
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
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup())
                .set("$.order.attrs.ad_logon_grants[0].role", isDev() ? "superuser" : "user")
                .set("$.order.attrs.ad_integration", true)
                .set("$.order.project_name", project.id)
                .set("$.order.label", getLabel())
                .set("$.order.attrs.on_support", getSupport())
                .set("$.order.attrs.users", getUsers())
                .set("$.order.attrs.users_password", getUsersPassword())
                .set("$.order.attrs.grafana_version", getGrafanaVersion())
                .build();
    }

    @Step("Расширить")
    public void expandMountPoint() {
        expandMountPoint("expand_mount_point_new", "/app", 10);
    }

    @Step("Сбросить пароль")
    public void resetPassword(String password) {
        JSONObject jsonData = new JSONObject().put("user_name", getUsers()).put("users_password", password);
        OrderServiceSteps.runAction(ActionParameters.builder().name("reset_grafana_user_password").product(this).data(jsonData).build());
        usersPassword = password;
        save();
    }

    @Step("Удалить")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }

}
