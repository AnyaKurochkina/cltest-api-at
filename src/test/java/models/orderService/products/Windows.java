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
    String segment;
    String dataCentre;
    @ToString.Include
    String platform;
    @ToString.Include
    String osVersion;
    String role;
    public String domain;
    Flavor flavor;
    private static String ADD_DISK_PATH = "data.find{it.type=='vm'}.config.extra_disks.any{it.path=='%s'}";
    private static String DISK_SIZE = "data.find{it.type=='vm'}.config.extra_disks.find{it.path=='%s'}.size";
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
        domain = orderServiceSteps.getDomainBySegment(this, segment);
        createProduct();
        String host = (String) orderServiceSteps.getProductsField(this, "product_data[0].hostname");
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
        if(dataCentre == null)
            dataCentre = orderServiceSteps.getDataCentreBySegment(this, segment);
        return this;
    }

    @Override
    public JSONObject toJson() {
        Project project = Project.builder().id(projectId).build().createObject();
        AccessGroup accessGroup = AccessGroup.builder().projectName(project.id).build().createObject();
        return JsonHelper.getJsonTemplate(jsonTemplate)
                .set("$.order.product_id", productId)
                .set("$.order.attrs.domain", domain)
                .set("$.order.attrs.default_nic.net_segment", segment)
                .set("$.order.attrs.data_center", dataCentre)
                .set("$.order.attrs.platform", platform)
                .set("$.order.attrs.os_version", osVersion)
                .set("$.order.attrs.role", role)
                .set("$.order.attrs.flavor", new JSONObject(flavor.toString()))
                .set("$.order.attrs.ad_logon_grants[0].groups[0]", accessGroup.getPrefixName())
                .set("$.order.project_name", project.id)
                .set("$.order.attrs.on_support", project.getProjectEnvironment().getEnvType().contains("TEST"))
                .set("$.order.label", getLabel())
                .build();
    }

    //Добавить диск
    public void addDisk(String disk) {
        orderServiceSteps.executeAction("windows_add_disk", this, new JSONObject("{path: \"" + disk + "\", size: 10, file_system: \"ntfs\"}"), this.getProjectId());
        Assertions.assertTrue((Boolean) orderServiceSteps.getProductsField(this, String.format(ADD_DISK_PATH, disk)));
    }

    //Расширить диск
    public void expandMountPoint(String disk) {
        int sizeBefore = (Integer) orderServiceSteps.getProductsField(this, String.format(DISK_SIZE, disk));
        orderServiceSteps.executeAction("windows_expand_disk", this, new JSONObject("{path: \"" + disk + "\", size: 1}"), this.getProjectId());
        int sizeAfter = (Integer) orderServiceSteps.getProductsField(this, String.format(DISK_SIZE, disk));
        Assertions.assertEquals(sizeBefore, sizeAfter - 1, "sizeBefore >= sizeAfter");
    }

    //Проверить конфигурацию
    public void refreshVmConfig() {
        orderServiceSteps.executeAction("check_vm", this, null, this.getProjectId());
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
