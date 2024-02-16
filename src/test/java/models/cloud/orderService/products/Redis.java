package models.cloud.orderService.products;

import core.helper.JsonHelper;
import core.helper.JsonTemplate;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import redis.clients.jedis.Jedis;
import steps.orderService.ActionParameters;
import steps.orderService.OrderServiceSteps;


@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class Redis extends IProduct {
    @ToString.Include
    String osVersion;
    String appUserPassword;
    String appUser;
    String redisVersion;
    Flavor flavor;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/redis.json";
        if (productName == null)
            productName = "Redis (Astra)";
        if (appUser == null)
            appUser = "appuser";
        initProduct();
        if (appUserPassword == null) {
            appUserPassword = "8AEv023pMDHVw1w4zZZE23HjPAKmVDvdtpK8Qddme94VJBHKhgy";
        }
        if (redisVersion == null)
            redisVersion = getRandomProductVersionByPathEnum("redis_version.enum");
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
        if (flavor == null)
            flavor = getMinFlavor();
        return this;
    }

    public JSONObject toJson() {
        String accessGroup = accessGroup();
        JsonTemplate template = JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId);
        if (envType().contains("prod")) {
            template.put("$.order.attrs", "geo_distribution", true)
                    .put("$.order.attrs", "layout", getIdGeoDistribution("2-single-node-servers", "redis", platform, segment));
        }
        return template.set("$.order.attrs.domain", getDomain())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.availability_zone", getAvailabilityZone())
                .set("$.order.attrs.platform", getPlatform())
                .set("$.order.attrs.redis_version", redisVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup)
                .set("$.order.attrs.ad_logon_grants[0].role", isDev() ? "superuser" : "user")
                .set("$.order.project_name", projectId)
                .set("$.order.attrs.on_support", getSupport())
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.user_password", appUserPassword)
                .set("$.order.attrs.user", appUser)
                .set("$.order.label", getLabel())
                .build();
    }

    //Изменить конфигурацию
    public void resize(Flavor flavor) {
        String actionName = "resize_two_layer";
        if (isProd()) {
            actionName = "resize_vm";
        }
        OrderServiceSteps.runAction(ActionParameters.builder().name(actionName).product(this)
                .data(new JSONObject().put("flavor", new JSONObject(flavor.toString())).put("check_agree", true).put("warning", new JSONObject())).build());
        int cpusAfter = (Integer) OrderServiceSteps.getProductsField(this, CPUS);
        int memoryAfter = (Integer) OrderServiceSteps.getProductsField(this, MEMORY);
        Assertions.assertEquals(flavor.data.cpus, cpusAfter, "Конфигурация cpu не изменилась или изменилась неверно");
        Assertions.assertEquals(flavor.data.memory, memoryAfter, "Конфигурация ram не изменилась или изменилась неверно");
    }

    @SneakyThrows
    public void checkConnect() {
        String url = "";
        try {
            url = (String) OrderServiceSteps.getProductsField(this, CONNECTION_URL + "[0]");
            Jedis jedis = new Jedis(url);
            jedis.auth(appUser, appUserPassword);
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
        OrderServiceSteps.runAction(ActionParameters.builder().name("reset_redis_user_password").product(this)
                .data(new JSONObject().put("redis_password", password).put("user_name", appUser)).build());
        appUserPassword = password;
        save();
    }

    public void changeNotifyKeyspaceEvents(String attr) {
        String actionName = "change_redis_param_notify";
        if (isProd()) {
            actionName = "change_redis_param_notify_cluster";
        }
        OrderServiceSteps.runAction(ActionParameters.builder().name(actionName).product(this)
                .data(new JSONObject().put("notify_keyspace_events", attr)).build());
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

    public void updateOsStandalone() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("update_os_standalone").product(this).build());
    }
}
