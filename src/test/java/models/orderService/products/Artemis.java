package models.orderService.products;

import core.enums.Role;
import core.helper.JsonHelper;
import core.helper.http.Http;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.authorizer.GlobalUser;
import models.authorizer.Project;
import models.authorizer.ProjectEnvironmentPrefix;
import models.orderService.interfaces.IProduct;
import models.portalBack.AccessGroup;
import models.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static core.helper.Configure.StateServiceURL;
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
    String segment;
    String dataCentre;
    @ToString.Include
    String osVersion;
    String artemisVersion;
    String domain;
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
            productName = "VTB Apache ActiveMQ Artemis RHEL";
        initProduct();
        if (domain == null)
            domain = OrderServiceSteps.getDomainBySegment(this, segment);
        if (flavor == null)
            flavor = getMinFlavor();
        if (osVersion == null)
            osVersion = getRandomOsVersion();
        if (dataCentre == null)
            dataCentre = OrderServiceSteps.getDataCentreBySegment(this, segment);
        if (artemisVersion == null)
            artemisVersion = getRandomProductVersionByPathEnum("artemis_version.enum");
        return this;
    }

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
    }

    public JSONObject toJson() {
        AccessGroup accessGroup = AccessGroup.builder().projectName(getProjectId()).build().createObject();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.cluster_name", "at-" + new Random().nextInt())
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform",  getPlatform())
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.artemis_version", artemisVersion)
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.project_name", getProjectId())
                .set("$.order.attrs.on_support", getSupport())
                .set("$.order.attrs.layout", getIdGeoDistribution("artemis", "artemis-1:artemis-1"))
                .set("$.order.label", getLabel())
                .build();
    }


    //Перезагрузить по питанию
    public void restart() {
        restart("reset_vm");
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
        OrderServiceSteps.executeAction("check_vm", this, null, this.getProjectId());
    }

    public void createService(String name, String ownerCert) {
        OrderServiceSteps.executeAction("vtb-artemis_create_service", this, new JSONObject("{\"max_size_bytes\":\"100Mb\",\"max_expiry_delay\":60000,\"min_expiry_delay\":10000,\"address_full_policy\":\"Fail\",\"name\":\"" + name + "\",\"owner_cert\":\"" + ownerCert + "\"}"), this.getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(SERVICE_PATH, name)));
    }

    public void createClient(String clientType, String name, String ownerCert) {
        OrderServiceSteps.executeAction("vtb-artemis_create_client", this, new JSONObject("{\"client_types\":\"" + clientType + "\",\"name\":\"" + name + "\",\"owner_cert\":\"" + ownerCert + "\"}"), this.getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(CLIENT_NAME_PATH, name)));
    }

    public void createClientWithService(String clientType, String name, String ownerCert, String serviceName) {
        OrderServiceSteps.executeAction("vtb-artemis_create_client", this, new JSONObject("{\"client_types\":\"" + clientType + "\",\"service_names\":[\"" + serviceName + "\"],\"name\":\"" + name + "\",\"owner_cert\":\"" + ownerCert + "\"}"), this.getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(RELATIONSHIP_PATH, name, serviceName)));
    }

    public void deleteClient(String name) {
        OrderServiceSteps.executeAction("vtb-artemis_delete_client", this, new JSONObject("{\"name\":\"" + name + "\"}"), this.getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(CLIENT_NAME_PATH, name)));
    }


    public void deleteService(String name) {
        OrderServiceSteps.executeAction("vtb-artemis_delete_service", this, new JSONObject("{\"name\":\"" + name + "\"}"), this.getProjectId());
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(SERVICE_PATH, name)));
    }

    public void exportConf(){
        OrderServiceSteps.executeAction("vtb-artemis_export_conf", this,null);
        GlobalUser user = GlobalUser.builder().role(Role.ORDER_SERVICE_ADMIN).build().createObject();
        //Проверяем что письмо успешно отправлено в сс (статус, емэйл и кол-во аттачей)
        new Http(StateServiceURL)
                .setRole(Role.ORDER_SERVICE_ADMIN)
                .get("/actions/?order_id={}", orderId)
                .assertStatus(200)
                .getResponse().then().assertThat()
                .rootPath("list.find{it.status.contains('send_mail:completed')}.data")
                .body("status", is(201))
                .body("response[0].toEmails[0]", equalTo(user.getEmail()))
                .body("response[0].attachments.size()", is(5));
    }

    @SneakyThrows
    public void updateCerts() {
        Date dateBeforeUpdate;
        Date dateAfterUpdate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateBeforeUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, certPath));
        super.updateCerts("vtb-artemis_update-cert");
        dateAfterUpdate = dateFormat.parse((String) OrderServiceSteps.getProductsField(this, certPath));
        Assertions.assertEquals(-1, dateBeforeUpdate.compareTo(dateAfterUpdate), "Предыдущая дата обновления сертификата больше либо равна новой дате обновления сертификата ");

    }

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_two_layer");
    }
}

