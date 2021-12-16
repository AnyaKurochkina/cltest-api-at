package models.orderService.products;

import core.helper.Http;
import core.helper.JsonHelper;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.AccessGroup;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironment;
import models.orderService.interfaces.IProduct;
import models.orderService.interfaces.ProductStatus;
import models.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static core.helper.Configure.OrderServiceURL;


@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class RabbitMQCluster extends IProduct {
    static String RABBITMQ_USER = "data.find{it.type=='cluster'}.config.users[0]";
    @ToString.Include
    String segment;
    String dataCentre;
    @ToString.Include
    String platform;
    String domain;
    String role;
    Flavor flavor;
    String osVersion;

    @Override
    @Step("Заказ продукта")
    protected void create() {
        domain = orderServiceSteps.getDomainBySegment(this, segment);
        log.info("Отправка запроса на создание заказа для " + productName);
        JsonPath jsonPath = new Http(OrderServiceURL)
                .setProjectId(projectId)
                .body(toJson())
                .post("projects/" + projectId + "/orders")
                .assertStatus(201)
                .jsonPath();
        orderId = jsonPath.get("[0].id");
        orderServiceSteps.checkOrderStatus("success", this);
        setStatus(ProductStatus.CREATED);
        compareCostOrderAndPrice();
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/rabbitmq_cluster.json";
        productName = "RabbitMQ Cluster";
        role = "administrator";
        Project project = Project.builder().projectEnvironment(new ProjectEnvironment(env)).isForOrders(true).build().createObject();
        if (projectId == null) {
            projectId = project.getId();
        }
        if (productId == null) {
            productId = orderServiceSteps.getProductId(this);
        }
        return this;
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(project.id).build().createObject();
        switch (project.getProjectEnvironment().getEnvType()) {
            case ("TEST"):
                role = "manager";
                break;
            case ("DEV"):
                role = "administrator";
                break;
        }
        List<Flavor> flavorList = referencesStep.getProductFlavorsLinkedList(this);
        flavor = flavorList.get(0);
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getName())
                .set("$.order.attrs.web_console_grants[0].role", role)
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.web_console_grants[0].groups[0]", accessGroup.getName())
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.on_support", project.getProjectEnvironment().getEnvType().contains("TEST"))
                .build();
    }

    //Создать пользователя RabbitMQ
    public void rabbitmqCreateUser() {
        String user = "testapiuser";
        orderServiceSteps.executeAction("rabbitmq_create_user", this, new JSONObject(String.format("{rabbitmq_users: [{user: \"%s\", password: \"%s\"}]}", user, user)));
        String username = (String) orderServiceSteps.getProductsField(this, RABBITMQ_USER);
        Assertions.assertEquals(user, username);
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }

    @SneakyThrows
    public void updateCerts() {
        Date dateBeforeUpdate;
        Date dateAfterUpdate;
        super.updateCerts("rabbitmq_update_certs");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateBeforeUpdate = dateFormat.parse((String) orderServiceSteps.getProductsField(this, "attrs.preview_items.data.find{it.config.containsKey('certificate_expiration')}.config.certificate_expiration"));
        dateAfterUpdate = dateFormat.parse((String) orderServiceSteps.getProductsField(this, "data.find{it.config.containsKey('certificate_expiration')}.config.certificate_expiration"));
        Assertions.assertEquals(-1, dateBeforeUpdate.compareTo(dateAfterUpdate),
                String.format("Предыдущая дата: %s обновления сертификата больше либо равна новой дате обновления сертификата: %s", dateBeforeUpdate, dateAfterUpdate));
    }

    public void resize() {
        resize("resize_vm");
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

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point", "/app", 10);
    }
}
