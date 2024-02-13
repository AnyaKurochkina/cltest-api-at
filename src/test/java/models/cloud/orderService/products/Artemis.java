package models.cloud.orderService.products;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.authorizer.GlobalUser;
import models.cloud.authorizer.Project;
import models.cloud.authorizer.ProjectEnvironmentPrefix;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.ActionParameters;
import steps.orderService.OrderServiceSteps;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static core.helper.Configure.stateServiceURL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class Artemis extends IProduct {
    @ToString.Include
    String osVersion;
    String artemisVersion;
    Flavor flavor;
    private final static String CLIENT_NAME_PATH = "data.find{it.data.config.containsKey('clients')}.data.config.clients.list_user_type_name.any{it.name=='%s'}";
    private final static String RELATIONSHIP_PATH = "data.find{it.data.config.clients.containsKey('list_user_type_relationship')}.data.config.clients.list_user_type_relationship.find{it.name==('%s')}.relationship.any{it.name=='%s'}";
    private final static String SERVICE_PATH = "data.find{it.data.config.containsKey('list_services_and_clients_name')}.data.config.list_services_and_clients_name.any{it.name=='%s'}";


    @Override
    public Entity init() {
        jsonTemplate = "/orders/artemis.json";
        Project project = Project.builder().projectEnvironmentPrefix(new ProjectEnvironmentPrefix(env)).isForOrders(true).build().createObject();
        if (projectId == null) {
            setProjectId(project.getId());
        }
        if (productName == null)
            productName = "VTB Artemis";
        initProduct();
        if (artemisVersion == null)
            artemisVersion = getRandomProductVersionByPathEnum("artemis_version.enum");
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

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    public JSONObject toJson() {
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain())
                .set("$.order.attrs.cluster_name", "at-" + new Random().nextInt())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.availability_zone", getAvailabilityZone())
                .set("$.order.attrs.platform", getPlatform())
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.artemis_version", artemisVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup())
                .set("$.order.project_name", getProjectId())
                .set("$.order.attrs.on_support", getSupport())
                .set("$.order.attrs.layout", getIdGeoDistribution("artemis-2:artemis-2"))
                .set("$.order.label", getLabel())
                .build();
    }


    //Перезапуск кластера
    public void restart() {
        restart("vtb-artemis_restart");
    }

    //Выключить принудительно
    public void stopHard() {
        stopHard("stop_vm_hard");
    }

    //Выключить
    public void stopSoft() {
        stopSoft("stop_vm_soft");
    }

    //Включить
    public void start() {
        start("start_vm");
    }

    public void resize(Flavor flavor) {
        resize("resize_vm", flavor);
    }

    public void expandMountPoint() {
        expandMountPoint("expand_mount_point_new", "/app", 10);
    }

    //Проверить конфигурацию
    public void refreshVmConfig() {
        OrderServiceSteps.runAction(ActionParameters.builder().name("check_vm").product(this).build());
    }

    public void createService(String name, String ownerCert) {
        JSONObject obj = JsonHelper.getJsonTemplate("/orders/artemis_service.json")
                .set("$.name", name)
                .set("$.owner_cert", ownerCert)
                .build();
        OrderServiceSteps.runAction(ActionParameters.builder().name("vtb-artemis_create_service").product(this).data(obj).build());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(SERVICE_PATH, name)));
    }

    public void verticalScaling(Flavor maxFlavor) {
        JSONObject data = JsonHelper.getJsonTemplate("/orders/artemis_vertical_scaling.json")
                .set("$.current_flavor", flavor.getName())
                .set("$.state_service_flavor_name", flavor.getName())
                .set("$.state_service_ram", flavor.getMemory())
                .set("$.state_service_cpu", flavor.getCpus())
                .set("$.flavor", new JSONObject(maxFlavor.toString())).build();
        OrderServiceSteps.runAction(ActionParameters.builder().name("vtb-artemis_vertical_scaling_cluster").product(this)
                .data(data).timeout(Duration.ofHours(1)).build());
        flavor = maxFlavor;
        save();
    }

    public void switchProtocol(boolean core, boolean amqp) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("vtb_artemis_switch_protocol").product(this)
                .data(new JSONObject().put("AMQP", amqp).put("CORE", core)).build());
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Client {
        String addressFullPolicy = "FAIL";
        String clientTypes = "own";
        Integer maxExpiryDelay = 60000;
        Integer minExpiryDelay = 10000;
        String name = "name";
        String ownerCert = "gfhgh";
        Integer slowConsumerCheckPeriod = 10;
        String slowConsumerPolicy = "NOTIFY";
        Integer slowConsumerThreshold = -1;
        Boolean useSeparation = false;
        List<String> serviceNames = new ArrayList<>();
    }

    public void createClient(Client client) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("vtb-artemis_create_client").product(this).data(serialize(client)).build());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(CLIENT_NAME_PATH, client.getName())));
        if (!client.getServiceNames().isEmpty())
            for (String serviceName : client.getServiceNames())
                Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(RELATIONSHIP_PATH, client.getName(), serviceName)));
    }

    public void deleteClient(String name) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("vtb-artemis_delete_client").product(this)
                .data(new JSONObject().put("name", name)).build());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(CLIENT_NAME_PATH, name)));
    }


    public void deleteService(String name) {
        OrderServiceSteps.runAction(ActionParameters.builder().name("vtb-artemis_delete_service").product(this)
                .data(new JSONObject().put("name", name)).build());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(SERVICE_PATH, name)));
    }

    public void exportConf() {
        OrderServiceSteps.runAction(ActionParameters.builder().role(Role.ORDER_SERVICE_ADMIN).name("vtb-artemis_export_conf").product(this).build());
        GlobalUser user = GlobalUser.builder().role(Role.ORDER_SERVICE_ADMIN).build().createObject();
        //Проверяем что письмо успешно отправлено в сс (статус, емэйл и кол-во аттачей)
        new Http(stateServiceURL)
                .setRole(Role.CLOUD_ADMIN)
                .get("/api/v1/projects/{}/actions/?order_id={}", projectId, orderId)
                .assertStatus(200)
                .getResponse().then().assertThat()
                .rootPath("list.find{it.status.contains('send_mail:completed')}.data")
                .body("status", is(201))
                .body("response[0].toEmails[0]", equalTo(user.getEmail()))
                .body("response[0].attachments.size()", is(5));
    }

    public static String CERT_END_DATE = "data.find{it.data.config.containsKey('cert_end_date')}.data.config.cert_end_date";

    @SneakyThrows
    public void updateCertsArtemis(String action) {
        Date dateBeforeUpdate;
        Date dateAfterUpdate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateBeforeUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, CERT_END_DATE));
        super.updateCerts(action);
        dateAfterUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, CERT_END_DATE));
        Assertions.assertEquals(-1, dateBeforeUpdate.compareTo(dateAfterUpdate),
                "Предыдущая дата обновления сертификата больше либо равна новой дате обновления сертификата ");
    }

    public void updateCertsArtemis() {
        updateCertsArtemis("vtb-artemis_update-cert");
    }

    public void updateExpiredCertsArtemis() {
        updateCertsArtemis("vtb-artemis_update-expired-cert");
    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }
}

