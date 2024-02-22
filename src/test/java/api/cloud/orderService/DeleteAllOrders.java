package api.cloud.orderService;

import api.Tests;
import core.enums.Role;
import core.helper.Configure;
import core.helper.http.Http;
import core.helper.http.Path;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import models.AbstractEntity;
import models.cloud.authorizer.Folder;
import org.junit.ProductArgumentsProvider;
import org.junit.Source;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import steps.calculator.CalcCostSteps;
import steps.orderService.OrderServiceSteps;
import steps.resourceManager.ResourceManagerSteps;
import ui.t1.tests.engine.AbstractComputeTest;

import java.util.*;

import static tests.routes.OrderServiceApi.*;
import static tests.routes.VpcApi.getNetworksApiV1ProjectsProjectNameNetworksGet;
import static tests.routes.VpcApi.getSecurityGroupsApiV1ProjectsProjectNameSecurityGroupsGet;

@DisplayName("Тестовый набор по удалению всех заказов из проекта")
@Tag("deleteorders")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeleteAllOrders extends Tests {

    @ParameterizedTest(name = "[{1}] {0}")
    @Tag("deleteAll")
    @Source(ProductArgumentsProvider.ENV)
    @DisplayName("Удаление всех успешных заказов из проекта")
    public void deleteOrders(String env, Integer num) {
        OrderServiceSteps.deleteOrders(env, label -> label.startsWith("AT-API"));
    }

    @Test
    @DisplayName("Вывод всех ошибочных заказов")
    void printAllErrorOrders() {
        Http.setFixedRole(Role.ORDER_SERVICE_ADMIN);
        List<Order> orders = new ArrayList<>();
        List<String> projects = Arrays.asList("proj-ln4zg69jek", "proj-rddf0uwi0q", "proj-ahjjqmlgnm",
                "proj-bhbyhmik3a", "proj-zoz17np8rb", "proj-114wetem0c", "proj-1oob0zjo5h", "proj-6wpfrbes0g", "proj-aei4kz2yu4",
                "proj-lcwn3pwg7z", "proj-50duh5yxy6", "proj-xryy5l8ei5", "proj-yhi3rxo07h", "proj-5ejgs0vfzf", "proj-nedsjpgpjk", "proj-urmmc38ka8", "proj-sezxpgqlb6");
        if (Configure.ENV.equals("blue")) {
            projects = Arrays.asList("proj-iv550odo9a", "proj-3wgrmny2yu", "proj-td00y68hfk",
                    "proj-anw4ujlh5u", "proj-bw5aabeuw1", "proj-2xdbtyzqs3", "proj-lww1vo6okh", "proj-7w4eov3old", "proj-99p4fdfs5c",
                    "proj-ytwcbh7rlr", "proj-6sq3n30eh0", "proj-fnxokdmi0b", "proj-i6ul07p131", "proj-pr0n40cx1e", "proj-0c0ki636z5", "proj-p9b5mtehhq");
        }
        for (String projectId : projects) {
            OrderServiceSteps.getProductsWithStatus(projectId, "changing", "damaged", "failure", "pending", "creation_error", "locked", "deprovisioned_error", "warning")
                    .forEach(e -> orders.add(new Order(e, projectId, CalcCostSteps.getCostByUid(e, projectId))));
        }
        System.out.println("Битые заказы:");
        for (Order order : orders) {
            if(order.cost == null || order.cost == 0.0f)
                System.out.printf("%s/all/orders/%s/main?context=%s&type=project&org=vtb%n",
                    Configure.getAppProp("base.url"), order.id, order.projectId);
        }
        System.out.println("Битые заказы со списаниями:");
        for (Order order : orders) {
            if(order.cost != null && order.cost != 0.0f)
                System.out.printf("%s/all/orders/%s/main?context=%s&type=project&org=vtb %.02f₽/сут.%n",
                        Configure.getAppProp("base.url"), order.id, order.projectId, order.cost * 60 * 24);
        }
    }

    @Test
    @DisplayName("Удаление всех успешных заказов T1 Engine")
    void deleteOrdersCompute() {
        Map<Path, Class<? extends AbstractEntity>> computeEntities = new HashMap<Path, Class<? extends AbstractEntity>>() {{
            put(getV1ProjectsProjectNameComputeInstances, AbstractComputeTest.InstanceEntity.class);
            put(getV1ProjectsProjectNameComputeVolumes, AbstractComputeTest.VolumeEntity.class);
            put(getV1ProjectsProjectNameComputeImages, AbstractComputeTest.ImageEntity.class);
            put(getV1ProjectsProjectNameComputeSnapshots, AbstractComputeTest.SnapshotEntity.class);
            put(getV1ProjectsProjectNameComputePublicIps, AbstractComputeTest.PublicIpEntity.class);
            put(getV1ProjectsProjectNameComputeVips, AbstractComputeTest.VipEntity.class);
            put(getV1ProjectsProjectNameComputeSnats, AbstractComputeTest.ComputeEntity.class);
            put(getV1ProjectsProjectNameComputeBackups, AbstractComputeTest.ComputeEntity.class);
            put(getV1ProjectsProjectNamePlacementPolicies, AbstractComputeTest.PlacementEntity.class);
        }};
        Map<Path, Class<? extends AbstractEntity>> vpcEntities = new HashMap<Path, Class<? extends AbstractEntity>>() {{
            put(getNetworksApiV1ProjectsProjectNameNetworksGet, AbstractComputeTest.NetworkEntity.class);
            put(getSecurityGroupsApiV1ProjectsProjectNameSecurityGroupsGet, AbstractComputeTest.SecurityGroupEntity.class);
        }};

        Folder folderPollProject = Folder.builder().title("ProjectPool").build().onlyGetObject();
        for (String projectId : ResourceManagerSteps.getChildren(folderPollProject.getName())) {
            for (Map.Entry<Path, Class<? extends AbstractEntity>> entry : computeEntities.entrySet()) {
                addComputeEntitiesForRemove(entry.getKey(), projectId, entry.getValue());
            }
            for (Map.Entry<Path, Class<? extends AbstractEntity>> entry : vpcEntities.entrySet()) {
                addVpcEntitiesForRemove(entry.getKey(), projectId, entry.getValue());
            }
        }
        AbstractEntity.deleteCurrentTestEntities();
    }

    @SneakyThrows
    private static void addComputeEntitiesForRemove(Path path, String projectId, Class<? extends AbstractEntity> computeEntity) {
        List<String> list = Http.builder()
                .setRole(Role.CLOUD_ADMIN)
                .api(path, projectId)
                .jsonPath()
                .getList("list.order_id");
        for (String id : list)
            computeEntity.getDeclaredConstructor(String.class, String.class).newInstance(projectId, id)
                    .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @SneakyThrows
    private static void addVpcEntitiesForRemove(Path path, String projectId, Class<? extends AbstractEntity> computeEntity) {
        List<String> list = Http.builder()
                .setRole(Role.CLOUD_ADMIN)
                .api(path, projectId)
                .jsonPath()
                .getList("findAll{it.name != 'default'}.id");
        for (String id : list)
            computeEntity.getDeclaredConstructor(String.class, String.class).newInstance(projectId, id)
                    .deleteMode(AbstractEntity.Mode.AFTER_TEST);
    }

    @AllArgsConstructor
    private static class Order {
        String id;
        String projectId;
        Float cost;
    }
}
