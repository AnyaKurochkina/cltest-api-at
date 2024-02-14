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
public class RedisSentinel extends IProduct {
    private final static String USERNAME_PATH = "data.find{it.type=='app'}.data.config.users.any{it.user_name=='%s'}";
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
        jsonTemplate = "/orders/redis_sentinel.json";
        if (productName == null)
            productName = "Redis Sentinel Astra (Redis с репликацией)";
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
                .set("$.order.attrs.layout", getIdGeoDistribution("master-1:replica-1:arbiter-1"))
                .set("$.order.label", getLabel())
                .build();
    }

    //Изменить конфигурацию
    public void resize(Flavor flavor) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("resize_two_layer").product(this)
                .data(new JSONObject().put("flavor", flavor.toJson()).put("warning", new JSONObject()).put("check_agree", true)).build());
        int cpusAfter = (Integer) OrderServiceSteps.getProductsField(this, CPUS);
        int memoryAfter = (Integer) OrderServiceSteps.getProductsField(this, MEMORY);
        Assertions.assertEquals(flavor.data.cpus, cpusAfter, "Конфигурация cpu не изменилась или изменилась неверно");
        Assertions.assertEquals(flavor.data.memory, memoryAfter, "Конфигурация ram не изменилась или изменилась неверно");
    }

    @SneakyThrows
    public void checkConnect() {
        String url = "";
        try {
            url = "redis://" + OrderServiceSteps.getProductsField(this, CONNECTION_URL + "[0]");
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
        OrderServiceSteps.runAction(ActionParameters.builder().name("reset_sentinel_redis_user_password").product(this)
                .data(new JSONObject().put("redis_password", password).put("user_name", appUser)).build());
        appUserPassword = password;
        save();
    }

    public void changeNotifyKeyspaceEvents(String attr) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("change_redis_sentinel_param_notify").product(this)
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

    public void createUser(String user, String password) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("redis_sentinel_create_user").product(this)
                .data(new JSONObject().put("redis_password", password).put("user_name", user)).build());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(
                this, String.format(USERNAME_PATH, user)), String.format("Пользователь %s не найден", user));
    }

    public void deleteUser(String user) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("redis_sentinel_delete_user").product(this)
                .data(new JSONObject().put("user_name", user)).build());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(
                this, String.format(USERNAME_PATH, user)), String.format("Пользователь %s найден", user));
    }

    @Step("Redis. Обновить ОС")
    public void updateOsStandalone() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("update_os_redis_sentinel").product(this).build());
    }
}
