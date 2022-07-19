package models.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.Project;
import models.orderService.interfaces.IProduct;
import models.portalBack.AccessGroup;
import models.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import steps.orderService.OrderServiceSteps;


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
    String redisPassword;
    String redisVersion;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        domain = OrderServiceSteps.getDomainBySegment(this, segment);
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/redis.json";
        if (productName == null)
            productName = "Redis";
        initProduct();
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (redisPassword == null)
            redisPassword = "8AEv023pMDHVw1w4zZZE23HjPAKmVDvdtpK8Qddme94VJBHKhgy";
        if (dataCentre == null)
            dataCentre = OrderServiceSteps.getDataCentreBySegment(this, segment);
        if (redisVersion == null)
            redisVersion = getRandomProductVersionByPathEnum("redis_version.enum");
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
                .set("$.order.attrs.redis_version", redisVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.project_name", projectId)
                .set("$.order.attrs.on_support", isTest())
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.redis_password", redisPassword)
                .set("$.order.label", getLabel())
                .build();
    }

    //Изменить конфигурацию
    public void resize(Flavor flavor) {
        OrderServiceSteps.executeAction("resize_two_layer", this, new JSONObject("{\"flavor\": " + flavor.toString() + ",\"warning\":{}}"), this.getProjectId());
        int cpusAfter = (Integer) OrderServiceSteps.getProductsField(this, CPUS);
        int memoryAfter = (Integer) OrderServiceSteps.getProductsField(this, MEMORY);
        Assertions.assertEquals(flavor.data.cpus, cpusAfter, "Конфигурация cpu не изменилась или изменилась неверно");
        Assertions.assertEquals(flavor.data.memory, memoryAfter, "Конфигурация ram не изменилась или изменилась неверно");
    }

    @SneakyThrows
    public void checkConnect() {
        String url = "";
        try {
            url = (String) OrderServiceSteps.getProductsField(this, CONNECTION_URL);
            Jedis jedis = new Jedis(url);
            jedis.auth(redisPassword);
            jedis.close();
        } catch (Exception e) {
            connectVmException("Ошибка подключения к Redis по url " + url + " : " + e);
        }
        log.debug("Успешное подключение к Redis");
    }

    //Расширить
    public void expandMountPoint() {
        expandMountPoint("expand_mount_point_new", "/app/redis/data", 10);
    }

    public void resetPassword() {
        String password = "UEijLKcQJN2pZ0Iqvxh1EXCuZ86pPGiEpdxwLRLWL4QnIOG2KPlGrw5jkLEScQZ9";
        OrderServiceSteps.executeAction("reset_redis_password", this, new JSONObject(String.format("{redis_password: \"%s\"}", password)), this.getProjectId());
        redisPassword = password;
        save();
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
