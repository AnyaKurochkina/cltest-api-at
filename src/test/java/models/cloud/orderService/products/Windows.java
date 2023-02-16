package models.cloud.orderService.products;

import core.helper.JsonHelper;
import io.qameta.allure.Step;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;
import models.Entity;
import models.cloud.portalBack.AccessGroup;
import models.cloud.authorizer.Project;
import models.cloud.orderService.interfaces.IProduct;
import models.cloud.subModels.Flavor;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import steps.orderService.OrderServiceSteps;
import steps.portalBack.PortalBackSteps;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true)
@Log4j2
@Data
@NoArgsConstructor
@SuperBuilder
public class Windows extends IProduct {
    @ToString.Include
    String osVersion;
    String role;
    Flavor flavor;
    private static String ADD_DISK_PATH = "data.find{it.type=='vm'}.data.config.extra_disks.any{it.path=='%s'}";
    private static String DISK_SIZE = "data.find{it.type=='vm'}.data.config.extra_disks.find{it.path=='%s'}.size";
    private static String DISK_SERIAL = "data.find{it.type=='vm'}.data.config.extra_disks.find{it.path=='%s'}.serial";
    private static String DISK_IS_CONNECTED = "data.find{it.type=='vm'}.data.config.extra_disks.find{it.path=='%s'}.is_connected";
    public static String ADD_DISK = "Добавить диск";

    private final static Map<String, String> roles = Stream.of(new String[][] {
            { "generic_application", "ap" },
            { "microsoft_sql_server", "sq" },
            { "frontend_web_service", "we" },
            { "mock_server", "mk" },
            { "reporting_point", "rp" },
            { "security_tools", "se" },
            { "autotest_scripts", "as" },
            { "proxy_server", "px" },
            { "generic_gateway", "gw" },
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    @Override
    @Step("Заказ продукта")
    protected void create() {
        createProduct();
        String host = (String) OrderServiceSteps.getProductsField(this, "product_data[0].hostname");
        Assertions.assertTrue(host.contains("-" + roles.get(role)));
    }

    @Override
    public Entity init() {
        jsonTemplate = "/orders/windows_server.json";
        productName = "Windows server";
        initProduct();
        if(role == null){
            role = (String) roles.keySet().toArray()[(int) (Math.random() * roles.size())];
        }
        if(flavor == null)
            flavor = getMinFlavor();
        if(osVersion == null)
            osVersion = getRandomOsVersion();
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
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        String accessGroup = PortalBackSteps.getRandomAccessGroup(getProjectId(), getDomain(), "compute");
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", getDomain())
                .set("$.order.attrs.default_nic.net_segment", getSegment())
                .set("$.order.attrs.data_center", getDataCentre())
                .set("$.order.attrs.platform",  getPlatform())
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.role", role)
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup)
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", getSupport())
                .set("$.order.label", getLabel())
                .build();
    }

    public void astromAdd() {
        OrderServiceSteps.executeAction("windows_astrom_add", this, new JSONObject().put("check_agree", true));
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(ADD_DISK_PATH, "X")));
    }

    public void astromDelete() {
        OrderServiceSteps.executeAction("windows_astrom_delete", this, null);
        Assertions.assertFalse((Boolean) OrderServiceSteps.getProductsField(this, String.format(ADD_DISK_PATH, "X")));
    }

    //Добавить диск
    public void addDisk(String disk) {
        OrderServiceSteps.executeAction("windows_add_disk", this, new JSONObject("{path: \"" + disk + "\", size: 10, file_system: \"ntfs\"}"), this.getProjectId());
        Assertions.assertTrue((Boolean) OrderServiceSteps.getProductsField(this, String.format(ADD_DISK_PATH, disk)));
    }

    //Расширить диск
    public void expandMountPoint(String disk) {
//        int sizeBefore = (Integer) OrderServiceSteps.getProductsField(this, String.format(DISK_SIZE, disk));
        OrderServiceSteps.executeAction("windows_expand_disk", this, new JSONObject("{path: \"" + disk + "\", size: 12}"), this.getProjectId());
        int sizeAfter = (Integer) OrderServiceSteps.getProductsField(this, String.format(DISK_SIZE, disk));
        Assertions.assertEquals(sizeAfter, 12);
    }

    public void unmountDisk(String disk) {
        String serial = (String) OrderServiceSteps.getProductsField(this, String.format(DISK_SERIAL, disk));
        OrderServiceSteps.executeAction("windows_unmount_disk", this,
                new JSONObject("{path: \"" + disk + "\", serial: \"" + serial + "\", is_connected: true}"), getProjectId());
        boolean isConnected = (Boolean) OrderServiceSteps.getProductsField(this, String.format(DISK_IS_CONNECTED, disk));
        Assertions.assertFalse(isConnected, "Диск не отключен");
    }

    public void mountDisk(String disk) {
        String serial = (String) OrderServiceSteps.getProductsField(this, String.format(DISK_SERIAL, disk));
        OrderServiceSteps.executeAction("windows_mount_disk", this,
                new JSONObject("{path: \"" + disk + "\", serial: \"" + serial + "\", is_connected: false}"), getProjectId());
        boolean isConnected = (Boolean) OrderServiceSteps.getProductsField(this, String.format(DISK_IS_CONNECTED, disk));
        Assertions.assertTrue(isConnected, "Диск не подключен");
    }

    public void deleteDisk(String disk) {
        String serial = (String) OrderServiceSteps.getProductsField(this, String.format(DISK_SERIAL, disk));
        OrderServiceSteps.executeAction("windows_delete_disk", this,
                new JSONObject("{path: \"" + disk + "\", serial: \"" + serial + "\", is_connected: false}"), getProjectId());
        boolean isCreatedDisk = (Boolean) OrderServiceSteps.getProductsField(this, String.format(ADD_DISK_PATH, disk));
        Assertions.assertFalse(isCreatedDisk, "Диск не удален");
    }

    //Проверить конфигурацию
    public void refreshVmConfig() {
        OrderServiceSteps.executeAction("check_vm", this, null, this.getProjectId());
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

    @Step("Удаление продукта")
    @Override
    protected void delete() {
        delete("delete_vm");
    }

}
