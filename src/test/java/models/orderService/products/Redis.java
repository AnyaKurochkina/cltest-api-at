package models.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.portalBack.AccessGroup;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.util.List;


@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class Redis extends IProduct {
    @ToString.Include
    String segment;
    String dataCentre;
    @ToString.Include
    String platform;
    String domain;
    @ToString.Include
    String osVersion;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        domain = orderServiceSteps.getDomainBySegment(this, segment);
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/redis.json";
        productName = "Redis";
        initProduct();
        return this;
    }

    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(project.id).build().createObject();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.project_name", projectId)
                .set("$.order.attrs.on_support", project.getProjectEnvironment().getEnvType().contains("TEST"))
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.label", getLabel())
                .build();
    }

    //Изменить конфигурацию
    public void resize(Flavor flavor) {
        orderServiceSteps.executeAction("resize_two_layer", this, new JSONObject("{\"flavor\": " + flavor.toString() + ",\"warning\":{}}"));
        int cpusAfter = (Integer) orderServiceSteps.getProductsField(this, CPUS);
        int memoryAfter = (Integer) orderServiceSteps.getProductsField(this, MEMORY);
        Assertions.assertEquals(flavor.data.cpus, cpusAfter, "Конфигурация cpu не изменилась или изменилась неверно");
        Assertions.assertEquals(flavor.data.memory, memoryAfter, "Конфигурация ram не изменилась или изменилась неверно");
    }

    //Расширить
    public void expandMountPoint() {
        expandMountPoint("expand_mount_point", "/app/redis/data", 10);
    }

    public void resetPassword() {
        String password = "yxjpjk7xvOImb1O9vZZiGUlsItkqLqtbB1VPZHzL6";
        orderServiceSteps.executeAction("reset_redis_password", this, new JSONObject(String.format("{redis_password: \"%s\"}", password)));
    }

    public void restart() {
        restart("reset_two_layer");
    }

    public void stopSoft() {
        stopSoft("stop_two_layer");
    }

    public void start() {
        start("start_two_layer");
    }

    public void stopHard() {
        stopHard("stop_hard_two_layer");
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }

}
